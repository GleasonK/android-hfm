package me.kevingleason.halffoods.sample;

import java.util.ArrayList;
import java.util.List;

import me.kevingleason.halffoods.adt.FoodItem;

/**
 * Created by GleasonK on 1/17/15.
 */
public class SampleData {
    static String[] foods = {"Spaghetti", "Pizza", "Hot Dog", "Salad", "Liver and Sauerkraut"};
    static String[] sellers = {"Kevin Gleason","Andrew Francl", "Aniket Saoji", "Jesse Mu", "Rootul Patel"};
    static String[] images = {"http://2.bp.blogspot.com/-lKHynQtF-8U/TcBcEOuP52I/AAAAAAAACNk/_YRIEk9XR00/s1600/asparrist+002.JPG",
                        "http://seasonedtotaste.files.wordpress.com/2010/01/img_5220.jpg",
                        "http://www.crbcsc.org/http://www.crbcsc.org/wp-content/uploads/2014/02/hot-dog-ftr.jpg",
                        "http://lizziee.files.wordpress.com/2011/02/salad-2.jpg",
                        "http://static.panoramio.com/photos/large/6331721.jpg"};
    static String[] locations = {"Chagrin Falls, OH", "Mormontown, UT", "Hella, CA", "Boston, MA", "Ireland, MA"};
    static double[]  prices = {2.30, 5.50, 6.99, 1.99, 10.00};

    public static List<FoodItem> getFoodItems(){
        List<FoodItem> foodItems = new ArrayList<FoodItem>();
        for (int i = 0; i < 5; i++) {
            FoodItem food = new FoodItem(
                    foods[i],
                    sellers[i],
                    1,
                    true,
                    images[i],
                    locations[i],
                    prices[i]
            );
            foodItems.add(food);
        }
        return foodItems;
    }
}
