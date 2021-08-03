package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



public class ActivityEnquiry extends FragmentActivity {

    Button btnSend, btnLocation;
    //	static Button btnDate;
//	static Button btnTime;
    EditText edtName, edtName2, edtPhone, edtOrderList, edtComment, edtAlamat, edtEmail, edtKota, edtProvinsi, edtDoctor, edtTestName;
    ScrollView sclDetail;
    String gpsLat, gpsLong, gpsCountry, gpsCity, gpsAddress, gpsPin;
    ProgressBar prgLoading;
    TextView txtAlert;
    Spinner spinner;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;


    // declare dbhelper object
    static DBHelper dbhelper;
    ArrayList<ArrayList<Object>> data;

    // declare string variables to store data
    String Name, Name2, Date, Time, Phone, Date_n_Time, Alamat, Email, Kota, Provinsi, Doctor, TestName;
    String OrderList = "";
    String Comment = "";

    // declare static int variables to store date and time
    private static int mYear;
    private static int mMonth;
    private static int mDay;
    private static int mHour;
    private static int mMinute;

    // declare static variables to store tax and currency data
    static double Tax;
    static double Delivery;
    static String Currency;

    GPSTracker gpsTracker;

    static final String TIME_DIALOG_ID = "timePicker";
    static final String DATE_DIALOG_ID = "datePicker";

    // create price format
    DecimalFormat formatData = new DecimalFormat("#.##");

    String Result;
    String TaxCurrencyAPI;
    int IOConnect = 0;
    String pkg_price, pkg_name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry);



        prefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String phone = prefs.getString("Phone",null);
        final String password = prefs.getString("Password", null);

        gpsTracker = new GPSTracker(this);


       /* Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */


        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Send Enquiry");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        Intent iGet = getIntent();
        String package_name = iGet.getStringExtra("package");
        if(package_name==null) {
            package_name = "";
        }

        pkg_name = iGet.getStringExtra("package");
        pkg_price = iGet.getStringExtra("price");


        edtName = (EditText) findViewById(R.id.edtName_enq);
      //  edtName2 = (EditText) findViewById(R.id.edtName2);
        edtEmail = (EditText) findViewById(R.id.edtEmail_enq);
        //   btnDate = (Button) findViewById(R.id.btnDate);
        //  btnTime = (Button) findViewById(R.id.btnTime);
        edtPhone = (EditText) findViewById(R.id.edtPhone_enq);
        edtPhone.setText(phone);
        edtPhone.setVisibility(View.GONE);
    //    edtOrderList = (EditText) findViewById(R.id.edtOrderList);
        edtComment = (EditText) findViewById(R.id.edtComment_enq);
        edtDoctor = (EditText) findViewById(R.id.edtDoctor_enq);
        edtTestName = (EditText) findViewById(R.id.edtTestName_enq);
        edtTestName.setText(""+package_name);

        btnSend = (Button) findViewById(R.id.btnSend_enq);
        btnLocation = (Button) findViewById(R.id.btnGPS);

   //     sclDetail = (ScrollView) findViewById(R.id.sclDetail);

        edtAlamat = (EditText) findViewById(R.id.edtAlamat_enq);
        edtKota = (EditText) findViewById(R.id.edtKota_enq);
        edtProvinsi = (EditText) findViewById(R.id.edtProvinsi_enq);


        //   btnDate.setVisibility(View.INVISIBLE);
        //   btnTime.setVisibility(View.INVISIBLE);

  //      Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
   //     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
      //          R.array.shipping_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
   //     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
   //     spinner.setAdapter(adapter);




        dbhelper = new DBHelper(this);
        // open database
        try{
            dbhelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

    //    prgLoading.setVisibility(0);
      //  txtAlert.setVisibility(8);
        // call asynctask class to request tax and currency data from server
      //  new getTaxCurrency().execute();

        // event listener to handle date button when pressed
        /*btnDate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show date picker dialog
				DialogFragment newFragment = new DatePickerFragment();
			    newFragment.show(getSupportFragmentManager(), DATE_DIALOG_ID);
			}
		});
        */
        // event listener to handle time button when pressed
      /*  btnTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show time picker dialog
				DialogFragment newFragment = new TimePickerFragment();
			    newFragment.show(getSupportFragmentManager(), TIME_DIALOG_ID);
			}
		});
*/
        // event listener to handle send button when pressed
        btnSend.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // get data from all forms and send to server
                Name = edtName.getText().toString();
                Alamat = edtAlamat.getText().toString();
                Kota = edtKota.getText().toString();
                Provinsi = edtProvinsi.getText().toString();
                Email = edtEmail.getText().toString();
                Doctor = edtDoctor.getText().toString();
                TestName = edtTestName.getText().toString();

                //Name2 = edtName2.getText().toString();
                //	Date = btnDate.getText().toString();
                //	Time = btnTime.getText().toString();
                Date = "";
                Time = "";
                Phone = edtPhone.getText().toString();
                Comment = edtComment.getText().toString();
                Date_n_Time = Date+" "+Time;
                if(Name.equalsIgnoreCase("") ||
                        //	Date.equalsIgnoreCase(getString(R.string.date)) ||
                        //	Time.equalsIgnoreCase(getString(R.string.time)) ||
                        Phone.equalsIgnoreCase("")){
                    Toast.makeText(ActivityEnquiry.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
                } else {
                    new sendData().execute();
                }
            }
        });

        btnLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (gpsTracker.getIsGPSTrackingEnabled())
                {
                    btnLocation.setText("Fetching Data...");
                    gpsLat = String.valueOf(gpsTracker.latitude);
                    gpsLong = String.valueOf(gpsTracker.longitude);
                    gpsCountry = gpsTracker.getCountryName( ActivityEnquiry.this);
                    gpsCity = gpsTracker.getLocality(ActivityEnquiry.this);
                    gpsPin = gpsTracker.getPostalCode(ActivityEnquiry.this);
                    gpsAddress = gpsTracker.getAddressLine(ActivityEnquiry.this);

                    edtAlamat.setText(gpsAddress);
                   // edtKota.setText(gpsCity);
                    btnLocation.setText("Pick My Location (GPS)");


                }
                else
                {

                    Toast.makeText(ActivityEnquiry.this,"Please Enable GPS",Toast.LENGTH_LONG).show();
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

            case R.id.share:
                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, "Download our app:\n\""+getString(R.string.app_name)+"\" \nhttps://play.google.com/store/apps/details?id="+getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));
                return true;

            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.Helpline));
                startActivity(intent);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // method to create date picker dialog
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // set default date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // get selected date
            mYear = year;
            mMonth = month;
            mDay = day;

            // show selected date to date button
		/*	btnDate.setText(new StringBuilder()
    		.append(mYear).append("-")
    		.append(mMonth + 1).append("-")
    		.append(mDay).append(" ")); */
        }
    }

    // method to create time picker dialog
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // set default time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // get selected time
            mHour = hourOfDay;
            mMinute = minute;

            // show selected time to time button
	/*		btnTime.setText(new StringBuilder()
            .append(pad(mHour)).append(":")
            .append(pad(mMinute)).append(":")
            .append("00"));  */
        }
    }

    // asynctask class to handle parsing json in background


    // method to parse json data from server

    // asynctask class to get data from database in background


    // asynctask class to send data to server in background
    public class sendData extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivityEnquiry.this, "",
                    getString(R.string.sending_alert), true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            Result = getRequest(Name, Alamat, Kota, Provinsi, Email, Phone, Doctor, TestName, Comment);
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
       // Toast.makeText(ActivityEnquiry.this,  HasilProses, Toast.LENGTH_SHORT).show();
        if(HasilProses.trim().equalsIgnoreCase("OK")){
            Toast.makeText(ActivityEnquiry.this, R.string.ok_enquiry, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ActivityEnquiry.this, ActivityConfirmMessage.class);
            i.putExtra("package", pkg_name);
            i.putExtra("price", pkg_price);
            startActivity(i);
            overridePendingTransition (R.anim.open_next, R.anim.close_next);
            finish();
        }else if(HasilProses.trim().equalsIgnoreCase("Failed")){
            Toast.makeText(ActivityEnquiry.this, R.string.failed_enquiry, Toast.LENGTH_SHORT).show();

        }else{
            Log.d("HasilProses", HasilProses);
        }
    }

    // method to post data to server
    public String getRequest(String name, String alamat, String kota, String provinsi, String email,  String phone,  String doctor, String test_name, String comment){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.SendEnquiryAPI);

        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("alamat", alamat));
            nameValuePairs.add(new BasicNameValuePair("kota", kota));
            nameValuePairs.add(new BasicNameValuePair("provinsi", provinsi));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("doctor", doctor));
            nameValuePairs.add(new BasicNameValuePair("gps_lat", gpsLat));
            nameValuePairs.add(new BasicNameValuePair("gps_long", gpsLong));
            nameValuePairs.add(new BasicNameValuePair("test_name", test_name));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
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
    public void getDataFromDatabase(){

        data = dbhelper.getAllData();

        double Order_price = 0;
        double Total_price = 0;
        double tax = 0;

        // store all data to variables
        for(int i=0;i<data.size();i++){
            ArrayList<Object> row = data.get(i);

            String Menu_name = row.get(1).toString();
            String Quantity = row.get(2).toString();
            double Sub_total_price = Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString())));
            Order_price += Sub_total_price;

            // calculate order price
            OrderList += (Quantity+" "+Menu_name+ " " + Currency + ""+Sub_total_price+" "+Currency+",\n");
        }

        if(OrderList.equalsIgnoreCase("")){
            OrderList += getString(R.string.no_order_menu);
        }

        tax = Double.parseDouble(formatData.format(Order_price *(Tax /100)));
        //Total_price = Double.parseDouble(formatData.format(Order_price - tax));
        if(Order_price<Constant.DeliveryLimit) {
            Total_price = Double.parseDouble(formatData.format(Order_price + Delivery));
        } else {
            Total_price = Double.parseDouble(formatData.format(Order_price));

        }
        OrderList += "\nOrder: "+Currency+Order_price+" ";
        if(Order_price<Constant.DeliveryLimit) {
            //"\nDelivery Charges: "+Delivery+": "+Currency+tax+" "+
            OrderList += "\nDelivery Charges: " + Currency + " " + Delivery;
        } else {
            OrderList += "\nDelivery Charges: FREE";
        }
        OrderList +=	"\nTotal: "+Currency+Total_price+" ";
        edtOrderList.setText(OrderList);
    }

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
