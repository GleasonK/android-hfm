package me.kevingleason.halffoods.adt;

/**
 * Created by GleasonK on 1/17/15.
 */
public class FoodItem {
    private String foodName;
    private String seller;
    private int sellerID;
    private boolean forSale;
    private String image;
    private String location;
    private double price;
//    private int percentage;

    public FoodItem(String foodName, String seller, int sellerID,
            boolean forSale, String image,  String location, double price){
        this.foodName=foodName;
        this.seller=seller;
        this.sellerID=sellerID;
        this.forSale=forSale;
        this.image=image;
        this.location=location;
        this.price=price;
    }

    public double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public boolean isForSale() {
        return forSale;
    }

    public int getSellerID() {
        return sellerID;
    }

    public String getSeller() {
        return seller;
    }

    public String getFoodName() {
        return foodName;
    }

    public String toString(){
        return this.foodName + " " + this.price;
    }
}
