package com.project.ssuet.myfoodapp.Helpers;


import com.project.ssuet.myfoodapp.Models.User;

/**
 * Created by abdul on 10/22/17.
 */

public class Session {
    public static Session sharedInstance;
    private User user;




    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private Session(){

    }

    public static Session getInstance()
    {
        if (sharedInstance == null) {
            sharedInstance = new Session();
        }
        return sharedInstance;
    }
}
