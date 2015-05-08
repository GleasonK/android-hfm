package me.kevingleason.halffoods.util;

import android.content.Context;
import android.content.SharedPreferences;

import me.kevingleason.halffoods.R;
import me.kevingleason.halffoods.adt.User;

/**
 * Created by GleasonK on 1/17/15.
 */
public class Auth {

    public static User getUserInfo(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.half_food_preference_file),context.MODE_PRIVATE);
        String email = sp.getString(context.getString(R.string.half_food_preference_username), "");
        String pass = sp.getString(context.getString(R.string.half_food_preference_password), "");
        String authToken = sp.getString(context.getString(R.string.half_food_preference_auth), "");
        int uid = sp.getInt(context.getString(R.string.half_food_preference_id), 0);
        return new User(email, pass, authToken, uid);
    }

}
