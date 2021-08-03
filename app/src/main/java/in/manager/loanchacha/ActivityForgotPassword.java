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

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
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
import android.widget.TextView;
import android.widget.Toast;


//import android.widget.CheckBox;

public class ActivityForgotPassword extends FragmentActivity {

    Button btnReset;
    EditText edtPhone;
    ScrollView sclDetail;
    ProgressBar prgLoading;
    TextView txtAlert;
    TextView txtPassword;
    Spinner spinner;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;

    RadioButton radioFranchise, radioFreelancer;



    // declare dbhelper object
    String User;
    static DBHelper dbhelper;

    // declare string variables to store data
    String Phone;



    String Result;
    String TaxCurrencyAPI;
    int IOConnect = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Forgot Password");
        bar.hide();

        edtPhone = (EditText) findViewById(R.id.edtPhone);

        btnReset = (Button) findViewById(R.id.btnSend1);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        txtPassword = (TextView) findViewById(R.id.txtPassword);


        radioFranchise = (RadioButton) findViewById(R.id.radioFranchise);
        radioFreelancer = (RadioButton) findViewById(R.id.radioFreelancer);

        User = "FREELANCER";

        try {
            prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        }catch (Exception ex) {
            Toast.makeText(ActivityForgotPassword.this,ex.toString(),Toast.LENGTH_LONG).show();
        }


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
        btnReset.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    // get data from all forms and send to server
                   Phone = edtPhone.getText().toString();

                    if (Phone.equalsIgnoreCase("") ) {
                        Toast.makeText(ActivityForgotPassword.this, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
                    } else {
                        new sendData().execute();
                    }
                } catch (Exception ex) {
                    Toast.makeText(ActivityForgotPassword.this,ex.toString(),Toast.LENGTH_LONG).show();
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
            dialog= ProgressDialog.show(ActivityForgotPassword.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            Result = getRequest(Phone);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
            dialog.dismiss();
            try {

            } catch (Exception ex) {
                Toast.makeText(ActivityForgotPassword.this,ex.toString(),Toast.LENGTH_LONG).show();
            }
            resultAlert(Result);


        }
    }




    // method to show toast message
    public void resultAlert(String HasilProses){

//        Toast.makeText(ActivityForgotPassword.this, HasilProses, Toast.LENGTH_LONG).show();

        //  Toast.makeText(ActivitySponsor.this,HasilProses,Toast.LENGTH_LONG).show();
        if(HasilProses.trim().isEmpty() == false ){

            txtPassword.setText("Your have recieved your password to your registered mobile number via SMS.");
            finish();

        //    sendSMS(edtPhone.getText().toString(),"Your password is: "+HasilProses);

          //  Toast.makeText(ActivityForgotPassword.this, "Your password is: "+ HasilProses, Toast.LENGTH_SHORT).show();



        }else {

            Toast.makeText(ActivityForgotPassword.this, "Invalid Phone Number / User not registered.", Toast.LENGTH_SHORT).show();


        }
    }

    // method to post data to server
    public String getRequest(String phone){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.ForgotAPI+"?accesskey="+Constant.AccessKey);

        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("user_type", User));

            request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        }catch(Exception ex){
            result = "Unable to connect.";
        }
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


    public void sendSMS(String phoneNo, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
