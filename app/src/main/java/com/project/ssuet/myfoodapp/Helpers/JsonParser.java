package com.project.ssuet.myfoodapp.Helpers;


import com.project.ssuet.myfoodapp.Models.LoginModel;
import com.project.ssuet.myfoodapp.Models.SignupModel;
import com.project.ssuet.myfoodapp.Models.User;

import org.json.JSONObject;

/**
 * Created by abdul on 9/20/17.
 */

public class JsonParser {

    static JsonParser sharedInstance;

    private JsonParser() {

    }

    public static JsonParser getInstance()
    {
        if (sharedInstance == null) {
            sharedInstance = new JsonParser();
        }
        return sharedInstance;
    }

    public SignupModel ParseSignupResponse(String s) {

        try {
            JSONObject response = new JSONObject(s);
            int errorCode = response.getInt("error");
            switch (errorCode) {
                case 200:
                  return new SignupModel(200,response.getString("message"));
                case 500:
                    return new SignupModel(500,response.getString("message"));
                default:
                    return new SignupModel(errorCode,response.getString("message"));
            }
        }

        catch (Exception e)
        {
                return null;
        }

    }
 /*

    public UpdateUserModel ParseUserupdateResponse(String s) {

        try {
            JSONObject response = new JSONObject(s);
            int errorCode = response.getInt("error");
            switch (errorCode) {
                case 200:
                    return new UpdateUserModel(200,response.getString("message"));
                case 500:
                    return new UpdateUserModel(500,response.getString("message"));
                default:
                    return new UpdateUserModel(errorCode,response.getString("message"));
            }
        }

        catch (Exception e)
        {
            return null;
        }

    }*/

    public LoginModel parseLoginResponse(String s) {

        try {
            JSONObject response = new JSONObject(s);

            //User user = new User();

            //JSONObject responseArray = response.getJSONObject("user");

            int errorCode = response.getInt("error");
            String errorMessage = response.getString("message");

            switch (errorCode) {
                case 200:
                    User user = new User();

                    JSONObject responseArray = response.getJSONObject("user");

//                    int errorCode = response.getInt("error");
  //                  String errorMessage = response.getString("message");
                    String name = responseArray.getString("name");
                    String email = responseArray.getString("email");
                    String number = responseArray.getString("phone_number");
                    String password = responseArray.getString("password");
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhoneNumber(number);
                    user.setPassword(password);

                    return new LoginModel(200,errorMessage,user);
                case 500:
                    return new LoginModel(500,errorMessage);
                default:
                    return new LoginModel(errorCode,response.getString("message"));
            }
        }

        catch (Exception e)
        {
            return null;
        }

    }






}
