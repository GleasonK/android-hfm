package me.kevingleason.halffoods.adt;

/**
 * Created by GleasonK on 1/17/15.
 */
public class User {
    private String email;
    private String password;
    private String authToken;
    private int id;

    public User(String email, String password, String authToken, int id){
        this.email=email;
        this.password=password;
        this.authToken=authToken;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getId() {
        return id;
    }
}
