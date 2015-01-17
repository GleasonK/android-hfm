package me.kevingleason.halffoods.util;

/**
 * Created by GleasonK on 1/17/15.
 */
public class HFConfig {
    public static final int HTTP_TIMEOUT = 15 * 10000; // milliseconds
    public static final String SERVER_HOST="halffoodsmarket.com";
    public static final String BASE_URL = "http://api."+SERVER_HOST;
    public static final String GET_FOOD_DATA_URL = BASE_URL + "/foods";
    public static final String POST_SIGN_IN_URL = BASE_URL + "/users/sign_in";
}
