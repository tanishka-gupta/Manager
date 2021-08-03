package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


//import android.widget.CheckBox;

public class ActivitySignup extends FragmentActivity {

    Button btnSend, btnLogin, btnForgot, btnGuest;
    EditText edtName, edtPhone,  edtPassword, edtOTP;
    TextView txtJoinFreelancer, linkForgot;
    ScrollView sclDetail;
    ProgressBar prgLoading;
    TextView txtAlert;
    Spinner spinner;
    Switch switchUser;
    RadioButton radioFranchise, radioFreelancer;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;


    // declare dbhelper object
    static DBHelper dbhelper;

    // declare string variables to store data
    String Name, Phone, Password, User;
    String OrderList = "";
    String Comment = "";

    String otp="";


    String Result;
    String TaxCurrencyAPI;
    int IOConnect = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Login / Register for Free");
        bar.hide();

        edtName = (EditText) findViewById(R.id.edtName1);
        edtPhone = (EditText) findViewById(R.id.edtPhone1);
        edtPassword = (EditText) findViewById(R.id.edtPassword1);
        edtOTP = (EditText) findViewById(R.id.edtOtp);
        btnSend = (Button) findViewById(R.id.btnSend1);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGuest = (Button) findViewById(R.id.btnGuestLogin);
        btnForgot = (Button) findViewById(R.id.btnForgot);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        switchUser = (Switch) findViewById(R.id.switchUser);
        txtJoinFreelancer = (TextView) findViewById(R.id.joinFreelancer);
        linkForgot = (TextView) findViewById(R.id.linkForgot);

        radioFranchise = (RadioButton) findViewById(R.id.radioFranchise);
        radioFreelancer = (RadioButton) findViewById(R.id.radioFreelancer);


        User = "FREELANCER";


        txtJoinFreelancer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ActivitySignup.this, ActivityPayu.class);

                i.putExtra("URL",Constant.JoinFreelancerAPI+"?accesskey="+Constant.AccessKey);
                i.putExtra("title","Join As Loan Mitra");

                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);

            }
        });

        try {

            prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        }catch (Exception ex) {
            Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
        }

        switchUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    User = "FREELANCER";
                } else {
                    User = "FRANCHISE";
                }

              //  Toast.makeText(ActivitySignup.this,User,Toast.LENGTH_LONG).show();
            }
        });

        radioFreelancer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true) {
                    User = "FREELANCER";
                }
            }
        });

        radioFranchise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true) {
                    User = "FRANCHISE";
                }
            }
        });


        prgLoading.setVisibility(View.INVISIBLE);



        // event listener to handle send button when pressed
        btnSend.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    // get data from all forms and send to server
                    Name = edtName.getText().toString();
                    Phone = edtPhone.getText().toString();
                    Password = edtPassword.getText().toString();
                  //  Toast.makeText(ActivitySignup.this, Name, Toast.LENGTH_SHORT).show();
                  //  Toast.makeText(ActivitySignup.this, Phone, Toast.LENGTH_SHORT).show();
                   // Toast.makeText(ActivitySignup.this, Password, Toast.LENGTH_SHORT).show();
                    if (Name.equalsIgnoreCase("") ||  Password.equalsIgnoreCase("") || Phone.equalsIgnoreCase("")
                            ) {
                        Toast.makeText(ActivitySignup.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
                    } else {

                       // if(otp.equals("")) {
                         //   new sendDataOtp().execute();
                       // } else {
                         //   if(otp.equals(edtOTP.getText().toString())) {
                                new sendData().execute();
                           // }  else {
                             //   Toast.makeText(ActivitySignup.this,"Invalid OTP",Toast.LENGTH_LONG).show();
                           // }
                    //    }
                    }
                } catch (Exception ex) {
                    Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

        btnGuest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // get data from all forms and send to server
                    Name = "Guest";
                    Phone = "0000000000";
                    Password = "0000000000";
                    if ( Password.equalsIgnoreCase("") || Phone.equalsIgnoreCase("")
                            ) {
                        Toast.makeText(ActivitySignup.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
                    } else {
                      //  new sendDataGuestLogin().execute();
                        try {
                            prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("Name", "Guest");
                            editor.putString("Phone", Phone);
                            editor.putString("Password", Password);
                            editor.putString("user_id", "-9");

//                Toast.makeText(ActivitySignup.this,id,Toast.LENGTH_LONG).show();

                            editor.commit();
                        } catch (Exception ex) {
                            Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
                        }

                        Intent myintent=new Intent(ActivitySignup.this, MainActivity.class);
                        startActivity(myintent);
                        overridePendingTransition (R.anim.open_next, R.anim.close_next);
                        finish();

                    }
                } catch (Exception ex) {
                    Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

        btnForgot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActivityForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        linkForgot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActivityForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // get data from all forms and send to server
                    Name = edtName.getText().toString();
                    Phone = edtPhone.getText().toString();
                    Password = edtPassword.getText().toString();
                    //  Toast.makeText(ActivitySignup.this, Name, Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(ActivitySignup.this, Phone, Toast.LENGTH_SHORT).show();
                    // Toast.makeText(ActivitySignup.this, Password, Toast.LENGTH_SHORT).show();
                    if ( Password.equalsIgnoreCase("") || Phone.equalsIgnoreCase("")
                            ) {
                        Toast.makeText(ActivitySignup.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
                    } else {
                        new sendDataLogin().execute();
                    }
                } catch (Exception ex) {
                    Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                return super.onOptionsItemSelected(item);

    }



    // asynctask class to send data to server in background
    public class sendData extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivitySignup.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            Result = getRequest(Name, Phone, Password);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
            dialog.dismiss();
            try {
                // get data from all forms and send to server
                Name = edtName.getText().toString();
                Phone = edtPhone.getText().toString();
                Password = edtPassword.getText().toString();
                //  Toast.makeText(ActivitySignup.this, Name, Toast.LENGTH_SHORT).show();
                //  Toast.makeText(ActivitySignup.this, Phone, Toast.LENGTH_SHORT).show();
                // Toast.makeText(ActivitySignup.this, Password, Toast.LENGTH_SHORT).show();
                if (Name.equalsIgnoreCase("") ||  Password.equalsIgnoreCase("") || Phone.equalsIgnoreCase("")
                        ) {
                    Toast.makeText(ActivitySignup.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
                } else {
                    new sendDataLogin().execute();
                  //  Toast.makeText(ActivitySignup.this, "Thank you for signup. You can login after approval from admin.", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception ex) {
                Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
            }
            //resultAlert(Result);


        }
    }


    // asynctask class to send data to server in background
    public class sendDataLogin extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivitySignup.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            Result = getRequestLogin(Name, Phone, Password);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
            dialog.dismiss();
            resultAlertLogin(Result);


        }
    }




    public class sendDataGuestLogin extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivitySignup.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
           // Result = getRequestGuestLogin(Name, Phone, Password);
            Result = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
           // dialog.dismiss();
          //  resultAlertGuestLogin(Result);


        }
    }

    // method to show toast message
    public void resultAlert(String HasilProses){
        if(HasilProses.trim().equalsIgnoreCase("OK")){
            //Toast.makeText(ActivitySignup.this, R.string.ok_alert, Toast.LENGTH_SHORT).show();

         /*   SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Name",Name);
            editor.putString("Phone",Phone);
            editor.putString("Password",Password);
            editor.commit(); */

          //  Intent i = new Intent(ActivitySignup.this, MainActivity.class);
          //  startActivity(i);
            Toast.makeText(ActivitySignup.this,"Your profile has been submitted and will be activated after admin confirmation",Toast.LENGTH_LONG).show();
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
           // finish();
        }else if(HasilProses.trim().equalsIgnoreCase("Failed")){
            Toast.makeText(ActivitySignup.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
        }else{
            Log.d("HasilProses", HasilProses);
        }
    }

    // method to post data to server
    public String getRequest(String name,  String phone, String password){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.SendSignUpAPI);

        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("accesskey", Constant.AccessKey));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        }catch(Exception ex){
            result = "Unable to connect.";
        }
        return result;
    }


    // method to show toast message
    public void resultAlertGuestLogin(String HasilProses){
        // Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();


                try {
                    prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("Name", "Guest");
                    editor.putString("Phone", Phone);
                    editor.putString("Password", Password);
                    editor.putString("user_id", "-9");

//                Toast.makeText(ActivitySignup.this,id,Toast.LENGTH_LONG).show();

                    editor.commit();
                } catch (Exception ex) {
                    Toast.makeText(ActivitySignup.this,ex.toString(),Toast.LENGTH_LONG).show();
                }

            Intent myintent=new Intent(ActivitySignup.this, MainActivity.class);
            startActivity(myintent);
            overridePendingTransition (R.anim.open_next, R.anim.close_next);
          //  finish();


    }

    // method to show toast message
    public void resultAlertLogin(String HasilProses){
       //  Toast.makeText(ActivitySignup.this, HasilProses, Toast.LENGTH_SHORT).show();

        if(HasilProses.trim().equalsIgnoreCase("")){
            Toast.makeText(ActivitySignup.this, "Invalid Mobile or Password or ID not activated.", Toast.LENGTH_SHORT).show();
            //  Intent i = new Intent(ActivityLogin.this, ActivityConfirmMessage.class);
            // startActivity(i);
            // overridePendingTransition (R.anim.open_next, R.anim.close_next);
            // finish();
        }else if(HasilProses.trim().equalsIgnoreCase("-1")){
            Toast.makeText(ActivitySignup.this, "Invalid Key", Toast.LENGTH_SHORT).show();
            //Toast.makeText(ActivityLogin.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
        }

        else {
            //Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();
            try {
                JSONObject reader = new JSONObject(HasilProses);
                JSONObject jObj =  reader.getJSONObject("data");
                String id = jObj.getString("id");
                String client_name = jObj.getString("name");

               // prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Name", client_name);
                editor.putString("Phone", Phone);
                editor.putString("Password", Password);
                editor.putString("user_type",User);
                editor.putString("user_id", id);

//                Toast.makeText(ActivitySignup.this,id,Toast.LENGTH_LONG).show();

                editor.commit();

                Intent myintent=new Intent(ActivitySignup.this, MainActivity.class);
                startActivity(myintent);
                overridePendingTransition (R.anim.open_next, R.anim.close_next);
                finish();

            }

            catch (Exception ex) {
                Toast.makeText(ActivitySignup.this,"Invalid Login",Toast.LENGTH_LONG).show();
            }



        }
    }


    public void resultAlertOtp(String HasilProses){
        // Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();

        if(HasilProses.trim().equalsIgnoreCase("-1")) {
            Toast.makeText(ActivitySignup.this, "Invalid Mobile or OTP not valid", Toast.LENGTH_SHORT).show();
            //  Intent i = new Intent(ActivityLogin.this, ActivityConfirmMessage.class);
            // startActivity(i);
            // overridePendingTransition (R.anim.open_next, R.anim.close_next);
            // finish();
        }

        else {
            //Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();
            otp = HasilProses.trim();

            edtName.setVisibility(View.GONE);
            edtPassword.setVisibility(View.GONE);
            edtPhone.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnForgot.setVisibility(View.GONE);
            btnSend.setText("Submit");
            edtOTP.setVisibility(View.VISIBLE);
            Toast.makeText(ActivitySignup.this, "Please enter the OTP to continue.", Toast.LENGTH_SHORT).show();


        }
    }

    // method to post data to server
    public String getRequestLogin(String name,  String phone, String password){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant
                .LoginClientAPI+"?accesskey="+Constant.AccessKey);

        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);

            nameValuePairs.add(new BasicNameValuePair("accesskey", Constant.AccessKey));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("user", User));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        }catch(Exception ex){
            result = "Unable to connect.";
        }
        return result;
    }





    public String getRequestGuestLogin(String name,  String phone, String password){
        String result = "";

        return result;
    }

    public static String request(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }
        return result;
    }

    // method to get data from database


    // method to format date
    private static String pad(int c) {
        if (c >= 10){
            return String.valueOf(c);
        }else{
            return "0" + String.valueOf(c);
        }
    }

    // when back button pressed close database and back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    //    dbhelper.close();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }
}
