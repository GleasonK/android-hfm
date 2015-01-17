package me.kevingleason.halffoods.buy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import me.kevingleason.halffoods.adt.FoodItem;
import me.kevingleason.halffoods.R;

/**
 * Created by GleasonK on 1/17/15.
 */
public class BuyArrayAdapter extends ArrayAdapter<FoodItem> {
    public List<FoodItem> values;
    public Context context;

    public BuyArrayAdapter(Context context, List<FoodItem> values) {
        super(context, R.layout.buy_food_row_layout, android.R.id.text1, values);
        this.values=values;
        this.context=context;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.buy_food_row_layout, parent, false);
        ImageView foodImage = (ImageView) rowView.findViewById(R.id.sell_image);
        TextView foodTextView = (TextView) rowView.findViewById(R.id.buy_item_name);
        TextView usernameTextView = (TextView) rowView.findViewById(R.id.buy_item_user);
        TextView addressTextView = (TextView) rowView.findViewById(R.id.buy_item_address);
        TextView priceText = (TextView) rowView.findViewById(R.id.buy_item_price);

        FoodItem food = values.get(position);

        Picasso.with(getContext()).load(food.getImage()).into(foodImage);
//        ImageLoadTask loadTask = new ImageLoadTask(values.get(position).getImage(),foodImage);
//        try {
//            foodImage.setImageBitmap(loadTask.get());
//        }
//        catch (InterruptedException e){ e.printStackTrace(); }
//        catch (ExecutionException e){ e.printStackTrace();}
        foodTextView.setText(food.getFoodName());
        usernameTextView.setText(food.getSeller());
        addressTextView.setText(food.getLocation());
        priceText.setText("$"+String.format("%.2f", food.getPrice()));

        return rowView;
    }

    public static String formatDate(Timestamp tstamp){
        Date date = new Date(tstamp.getTime());

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(new Date());
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        boolean yesterday = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR)+1 == cal2.get(Calendar.DAY_OF_YEAR);
        String timeID = cal1.getTimeZone().getID();
        if (sameDay || yesterday){
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");

//            DateFormat dateFormat = DateFormat.getTimeInstance();
            dateFormat.setTimeZone(TimeZone.getDefault());
            if (sameDay)
                return "Today | " + dateFormat.format(date);
            return "Yesterday | " + dateFormat.format(date);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    @Override
    public boolean isEmpty(){
        return this.values.size()==0;
    }

}
