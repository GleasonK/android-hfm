package me.kevingleason.halffoods.http;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import me.kevingleason.halffoods.adt.FoodItem;
import me.kevingleason.halffoods.util.HFConfig;

/**
 * Created by GleasonK on 1/17/15.
 */
public class FoodDataAsync extends AsyncTask<Void, Void, List<FoodItem>> {
    public Context mContext;
    public List<FoodItem> foodItems;
    public int foodId;

    public FoodDataAsync(int foodId){
        this.foodId=foodId;
    }

    public FoodDataAsync(Context context, List<FoodItem> foodItems){
        this.foodItems=foodItems;
        this.mContext=context;
    }

//    [{"id":1,"name":"Old Mac and Cheese!!!!!","price":0.0,"for_sale":true,"seller_id":null,"buyer_id":null,
// "created_at":"2015-01-17T10:07:15.428Z","updated_at":"2015-01-17T10:07:59.465Z",
// "image_file_name":"macaroniandcheese.jpg","image_content_type":"image/jpeg","image_file_size":17557,
// "image_updated_at":"2015-01-17T10:07:15.160Z"}]

    @Override
    public List<FoodItem> doInBackground(Void... params){
        ArrayList<NameValuePair> kvp= new ArrayList<NameValuePair>();
//        kvp.add(new BasicNameValuePair("username", username));
        List<FoodItem> foodItems = new ArrayList<FoodItem>();

        try {
            String response = RequestFunctions.executeHttpGet(HFConfig.GET_FOOD_DATA_URL);
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject foodJson = jsonArray.getJSONObject(i);
                FoodItem foodItem = new FoodItem(
                        foodJson.getString("name"),
                        "Kevin Gleason", //TODO Need seller ID and Seller name
                        0, //foodJson.getInt("seller_id"), //TODO Val cant be null
                        foodJson.getBoolean("for_sale"),
                        foodJson.getString("image_file_name"),
                        "Chestnut Hill, MA", //TODO Need the location
                        foodJson.getDouble("price")
                );
                foodItems.add(foodItem);
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (URISyntaxException e) { e.printStackTrace(); }
        catch (JSONException e) { e.printStackTrace(); }

        return foodItems;
    }

    @Override
    public void onPostExecute(List<FoodItem> foodItems){
        super.onPostExecute(foodItems);
        this.foodItems = foodItems;
        Toast.makeText(mContext, foodItems.toString(), Toast.LENGTH_LONG).show();
    }
}
