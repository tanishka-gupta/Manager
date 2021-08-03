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
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityLogin extends FragmentActivity {

    Button btnSend;
    //	static Button btnDate;
//	static Button btnTime;
    EditText edtPhone, edtPassword;
    ScrollView sclDetail;
    ProgressBar prgLoading;
    TextView txtAlert;
    Spinner spinner;

    // declare dbhelper object
    static DBHelper dbhelper;
  //  ArrayList<ArrayList<Object>> data;

    // declare string variables to store data
    String  Phone, Password;

    // declare static int variables to store date and time

    String Result;
    int IOConnect = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

/*   Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */


        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Login");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        //   btnDate = (Button) findViewById(R.id.btnDate);
        //  btnTime = (Button) findViewById(R.id.btnTime);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSend = (Button) findViewById(R.id.btnSend);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        edtPassword = (EditText) findViewById(R.id.edtPassword);


        dbhelper = new DBHelper(this);
        // open database
        try{
            dbhelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        // event listener to handle send button when pressed
        btnSend.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                Phone = edtPhone.getText().toString();
               // Toast.makeText(ActivityLogin.this,Phone,Toast.LENGTH_LONG).show();



                Password = edtPassword.getText().toString();
                //Toast.makeText(ActivityLogin.this,Password,Toast.LENGTH_LONG).show();

                if(Password.equalsIgnoreCase("") ||  Phone.equalsIgnoreCase("")){
                    Toast.makeText(ActivityLogin.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
              }else{

                    new sendData().execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // method to create date picker dialog


    // method to parse json data from server


    // asynctask class to get data from database in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            //getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // hide progressbar and show reservation form
            prgLoading.setVisibility(8);
            sclDetail.setVisibility(0);

        }
    }

    // asynctask class to send data to server in background
    public class sendData extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivityLogin.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            Result = getRequest(Phone, Password);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
            dialog.dismiss();
            resultAlert(Result);


        }
    }

    // method to show toast message
    public void resultAlert(String HasilProses){

      //  Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();

        if(HasilProses.trim().equalsIgnoreCase("")){
            Toast.makeText(ActivityLogin.this, "Invalid Mobile or Password", Toast.LENGTH_SHORT).show();
          //  Intent i = new Intent(ActivityLogin.this, ActivityConfirmMessage.class);
           // startActivity(i);
           // overridePendingTransition (R.anim.open_next, R.anim.close_next);
           // finish();
        }else if(HasilProses.trim().equalsIgnoreCase("-1")){
            Toast.makeText(ActivityLogin.this, "Invalid Key", Toast.LENGTH_SHORT).show();
            //Toast.makeText(ActivityLogin.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
        }

        else {
            //Toast.makeText(ActivityLogin.this, HasilProses, Toast.LENGTH_SHORT).show();
            Intent myintent=new Intent(ActivityLogin.this, ActivityCheckout.class).putExtra("data", HasilProses);
            startActivity(myintent);
            overridePendingTransition (R.anim.open_next, R.anim.close_next);
            finish();

        }
    }

    // method to post data to server
    public String getRequest( String phone, String password){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant
                .LoginDataAPI+"?accesskey="+Constant.AccessKey);

        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);

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
        dbhelper.close();
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
