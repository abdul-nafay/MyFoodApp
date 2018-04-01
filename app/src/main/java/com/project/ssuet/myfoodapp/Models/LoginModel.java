package com.project.ssuet.myfoodapp.Models;

/**
 * Created by Apple on 4/2/18.
 */

public class LoginModel {

    private int errorCode;
    private String message;
    private User user;

    public LoginModel(int errorCode, String message, User user) {
        this.errorCode = errorCode;
        this.message = message;
        this.user = user;
    }

    public LoginModel(int errorCode, String errorMessage) {

        this.errorCode = errorCode;
        this.message = errorMessage;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
