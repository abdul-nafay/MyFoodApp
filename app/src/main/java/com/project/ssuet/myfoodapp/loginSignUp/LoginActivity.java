package com.project.ssuet.myfoodapp.loginSignUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.ssuet.myfoodapp.Helpers.Session;
import com.project.ssuet.myfoodapp.Helpers.JsonParser;
import com.project.ssuet.myfoodapp.Models.LoginModel;
import com.project.ssuet.myfoodapp.Models.User;
import com.project.ssuet.myfoodapp.MyDrawer.MyDrawerActivity;
import com.project.ssuet.myfoodapp.Network.ConnectionDetector;
import com.project.ssuet.myfoodapp.Network.HttpHandler;
import com.project.ssuet.myfoodapp.R;
import com.project.ssuet.myfoodapp.Utility.AppConstants;
import com.project.ssuet.myfoodapp.Utility.MemorizerUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    ProgressDialog progressDialog;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    //@BindView(R.id.link_signup_sp) TextView _signupLinkSP;
    @BindView(R.id.loginLayout) LinearLayout loginLayout;
    @BindView(R.id.radioCheck) RadioGroup radioCheck;
    @BindView(R.id.userRadioBtn) RadioButton userRadioBtn;
    @BindView(R.id.serviceProviderRadioBtn) RadioButton spRadioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton = (Button) findViewById(R.id.btn_login);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        //_signupLinkSP = (TextView) findViewById(R.id.link_signup_sp);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        radioCheck = (RadioGroup) findViewById(R.id.radioCheck);
        userRadioBtn = (RadioButton) findViewById(R.id.userRadioBtn);
        spRadioBtn = (RadioButton) findViewById(R.id.serviceProviderRadioBtn);

        _loginButton.setOnClickListener(this);
        _signupLink.setOnClickListener(this);
       //  _signupLinkSP.setOnClickListener(this);


        loginLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MemorizerUtil.hideSoftInput(v,getApplicationContext());

                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {

        if (_loginButton.getId() == view.getId()){
           login();

        }
        else if (_signupLink.getId() == view.getId()){
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);

            //finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ConnectionDetector connnectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connnectionDetector.isConnectingToInternet();


        if(userRadioBtn.isChecked()){
            // Hit API for User Login
            if (isInternetPresent) {
                new LoginRequest(email, password).execute();
            }
            else {
                MemorizerUtil.displayToast(getApplicationContext(),"No Internet Connection");
            }

        }

        // TODO: Implement your own authentication logic here.
        onLoginSuccess();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid e mail address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();

        _emailText.setText("");
        _passwordText.setText("");

    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        //onBackPressed();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        // finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }






    public class LoginRequest extends AsyncTask<String, Void, String> {

        private String email, password;

        public LoginRequest(String email, String password) {
            // this.id = id;
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Verifying details...");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                response = login_api(email,password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            LoginModel model = JsonParser.getInstance().parseLoginResponse(s);

            if (model != null) {
                switch (model.getErrorCode()) {
                    case 200:
                        //Yahan Khulwa de Activity
                        User user = model.getUser();

                        ////////
                        Session session = Session.getInstance();

                        session.setUser(user);

                       /* User userDB = DatabaseManager.getInstance(getApplicationContext()).getUser(user.getEmail());
                        if (userDB == null) {// Entry in DB

                            DatabaseManager.getInstance(getApplicationContext()).addUser(user);

                        }
                        else { // Need Nothing to do

                        }
                        /////
                        */
                        String email = user.getEmail();

                        HashMap<String,String> map = new HashMap<>();
                        map.put("Email",email);
                        map.put("Type","0");

                        Gson gson = new Gson();
                        String mapString = gson.toJson(map);

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("HashString",mapString).apply();
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), MyDrawerActivity.class);

                        MemorizerUtil.displayToast(getApplicationContext(),model.getMessage());
                        startActivity(intent);
                        finish();
                        break;

                    case 500:

                        MemorizerUtil.displayToast(getApplicationContext(),model.getMessage());

                        break;

                    default:
                        //Error Message
                        MemorizerUtil.displayToast(getApplicationContext(),"Something went wrong");
                        break;
                }
            }
            else {

            }

            if(progressDialog != null){
                progressDialog.dismiss();
            }



        }

    }

    public String login_api(String email, String password) {
        String response = "";
        try {

            HttpHandler httpHandler = new HttpHandler();
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);
            if (userRadioBtn.isChecked()) {
                response = httpHandler.performPostCall(AppConstants.API_LOGIN, params);
            }
            else {
                //response = httpHandler.performPostCall(APISPLogin,params);
            }

            // response = httpHandler.performPostCall("https://androidfyp.000webhostapp.com/signup.php", params);

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        return response;

    }
}

