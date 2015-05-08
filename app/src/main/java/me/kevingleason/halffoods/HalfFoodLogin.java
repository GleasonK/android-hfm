package me.kevingleason.halffoods;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.kevingleason.halffoods.MainActivity;
import me.kevingleason.halffoods.R;
import me.kevingleason.halffoods.adt.User;
import me.kevingleason.halffoods.http.RequestFunctions;
import me.kevingleason.halffoods.util.Auth;
import me.kevingleason.halffoods.util.HFConfig;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 *
 * Email auto-populate currently assumes one GMail per device (else it will select a random of the
 *  GMail emails in the list). Also assumes that GMail is primary.
 */
public class HalfFoodLogin extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    //Population fields
    private String mPrimaryEmail;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;
    private View mSignOutButtons;
    private View mLoginFormView;
    private String mErrorMessage;

    //GCM
    public static boolean GCM_REGISTERED = false;
    GoogleCloudMessaging gcm;
    String regid, storedRedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(getString(R.string.half_food_preference_file), MODE_PRIVATE);
        boolean userIsSet = sp.contains(getString(R.string.half_food_preference_username)) &&
                sp.contains(getString(R.string.half_food_preference_password));
        // Set up the login form.
//        Typeface brightonBold = Typeface.createFromAsset(this.getAssets(), "fonts/Brighton Bold Plain.ttf");
        mUserView = (EditText) findViewById(R.id.username);
//        mUserView.setTypeface(brightonBold);

        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setTypeface(brightonBold);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mUserLabel = (TextView) findViewById(R.id.sign_in_user_label);
        TextView mPassLabel = (TextView) findViewById(R.id.sign_in_pass_label);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);


        mUserView.setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);
        mPasswordView.setImeActionLabel("Sign In", KeyEvent.KEYCODE_ENTER);
        setFont(mUserLabel, mPassLabel, mSignInButton);
        if (userIsSet) {
            try {
                User user = Auth.getUserInfo(this);
                mAuthTask = new UserLoginTask(user.getEmail(), user.getPassword());
                boolean loggedIn = mAuthTask.execute((Void) null).get();
                if (loggedIn) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    mUserView.setError("Invalid username or password.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    public void setFont(TextView... views){
//        Typeface brightonBold = Typeface.createFromAsset(this.getAssets(), "fonts/Brighton Bold Plain.ttf");

        for(TextView v : views){
//            v.setTypeface(brightonBold);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passError = isPasswordValid(password);
        if (!TextUtils.isEmpty(password) && passError.length()!=0) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.setError(passError);
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUserValid(email)) {
            mUserView.setError("Username must be 3 characters");
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            try {
                mAuthTask = new UserLoginTask(email, password);
                boolean loggedIn = mAuthTask.execute((Void) null).get();
                if (loggedIn) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    mUserView.setError("Invalid username or password.");
                }
            }
            catch (InterruptedException e ) { e.printStackTrace(); }
            catch (ExecutionException e) { e.printStackTrace(); }
        }
    }

    private boolean isUserValid(String user) {
        //TODO: Replace this with your own logic
        //Not necessary when using usernames
//        return true;
        return !user.contains("\"");
    }

    private String isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        String error = "";
        if (password.length() < 8) return "Password must be at least 8 characters." ;
        else return "";
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Do Post
            ArrayList<NameValuePair> kvp = new ArrayList<NameValuePair>();
            ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();

            kvp.add(new BasicNameValuePair("email", mEmail));
            kvp.add(new BasicNameValuePair("password", mPassword));

            headers.add(new BasicNameValuePair("Content-type","application/json"));
            headers.add(new BasicNameValuePair("Accept","application/json"));

            JSONObject json = null;
            try {
                String response = RequestFunctions.executeHttpPostHeaders(HFConfig.POST_SIGN_IN_URL, headers, kvp);
                json = new JSONObject(response);
                Log.d("LOGIN","AgoraLogin " + json.toString());
                if (json.getBoolean("success")){
                    JSONObject userJSON = json.getJSONObject("user");
                    SharedPreferences sp = getSharedPreferences(getString(R.string.half_food_preference_file),MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(getString(R.string.half_food_preference_id),userJSON.getInt("id"));
                    editor.putString(getString(R.string.half_food_preference_username), userJSON.getString("email"));
                    editor.putString(getString(R.string.half_food_preference_password), mPassword);
                    editor.putString(getString(R.string.half_food_preference_auth), userJSON.getString("auth_token"));
                    //Commit since it cannot be done in background, need values before next screen loads.
                    editor.commit();
                    return true;
                }
                else {
                    mErrorMessage=json.getString("message");
                    return false;
                }
            }
            catch (IOException e){ e.printStackTrace(); }
            catch (JSONException e){ e.printStackTrace(); }

            if (json == null) {
                mPasswordView.setError("ERROR: Could not connect");
                return false;
            }
            return false;

            // TODO: register the new account here.

        }

        @Override
        protected void onPreExecute(){
//            showProgress(true);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                if (mErrorMessage != null) {
                    mPasswordView.setError(mErrorMessage);
                    mErrorMessage=null;
                }
                else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                }
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}




