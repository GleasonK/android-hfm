package me.kevingleason.halffoods.sell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.kevingleason.halffoods.MainActivity;
import me.kevingleason.halffoods.R;

public class SellActivity extends Activity {
    public static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_TAKE_PHOTO = 1;
    public File mImageFile;
    public Uri mImageUri;
    public String mCurrentPhotoPath;
    public ImageView mImageView;
    public EditText mEditPrice;
    public EditText mFoodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        mImageView = (ImageView) findViewById(R.id.sell_take_photo);

        mEditPrice = (EditText) findViewById(R.id.sell_food_price);
        mFoodName  = (EditText) findViewById(R.id.sell_food_name);

        int textLength = mEditPrice.getText().length();
        mEditPrice.setSelection(textLength, textLength);
        mEditPrice.setRawInputType(Configuration.KEYBOARD_12KEY);
        mEditPrice.addTextChangedListener(new TextWatcher(){
            DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$"))
                {
                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
                    if (userInput.length() > 0) {
                        Float in=Float.parseFloat(userInput);
                        float percen = in/100;
                        mEditPrice.setText("$"+dec.format(percen));
                        mEditPrice.setSelection(mEditPrice.getText().length());
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setImage(mImageView);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void setImage(ImageView imageView){
        System.out.println(mImageFile.getAbsolutePath());
        Bitmap myBitmap = BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
        mImageView.setImageBitmap(cropBitmap(myBitmap));
    }


    public Bitmap cropBitmap(Bitmap bmp) {
        int cropNum=0;
        boolean height=false;
        if(bmp.getHeight()>bmp.getWidth()){
            cropNum=bmp.getWidth();
            height=true;
        } else {
            cropNum=bmp.getHeight();
        }
        int diff = Math.abs(bmp.getHeight()-bmp.getWidth());
        return Bitmap.createBitmap(bmp, (height ? 0 : diff/2), (height ? diff/2 : 0), cropNum, cropNum);
    }

    private boolean dispatchPic(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mImageFile = this.createImageFile();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
            return false;
        }
        mImageUri = Uri.fromFile(mImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        return true;
    }

    public void deletePhoto(View view){
        if (mImageFile!=null){
            mImageFile.delete();
            mCurrentPhotoPath=null;
            finish();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void photoEvent(View view){
        if(this.mImageFile==null){
            dispatchPic();
        } else {
            CharSequence options[] = new CharSequence[] {"Retake", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Retake photo?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which){
                        case 0:
                            if (mImageFile!=null)
                                mImageFile.delete();
                            mImageFile=null;
                            mCurrentPhotoPath=null;
                            dispatchPic();
                        default:
                            dialog.cancel();
                    }
                }
            });
            builder.show();
        }
    }

    public void sellAction(View view){
        if (mImageFile==null){
            Toast.makeText(this,"Take a photo of your food.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mFoodName.getText().toString().equals("")){
            mFoodName.setError("Must provide food name");
            return;
        }
        Toast.makeText(this,mFoodName.getText().toString() + " added to the market!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
