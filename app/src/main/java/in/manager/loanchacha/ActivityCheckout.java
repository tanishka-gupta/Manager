package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class ActivityCheckout extends FragmentActivity {
	
	Button btnSend;
	GPSTracker gpsTracker;
    double Total_price = 0;
//	static Button btnDate;
//	static Button btnTime;
	EditText edtName, edtPassword, edtName2, edtPhone, edtOrderList, edtComment, edtAlamat, edtEmail, edtKota, edtProvinsi;
	ScrollView sclDetail;
	ProgressBar prgLoading;
	TextView txtAlert;
    String data_json;
	Spinner spinner;
	String gpsLat, gpsLong, gpsCountry, gpsCity, gpsAddress, gpsPin;
	
	// declare dbhelper object
	static DBHelper dbhelper;
	ArrayList<ArrayList<Object>> data;
	
	// declare string variables to store data
	String Name, Name2, Date, Time, Phone, Date_n_Time, Alamat, Email, Kota, Provinsi, Password;
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
	
	static final String TIME_DIALOG_ID = "timePicker";
	static final String DATE_DIALOG_ID = "datePicker";

	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");

	String Result;
	String TaxCurrencyAPI;
	int IOConnect = 0;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

/*   Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */

        
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Checkout");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

//		gpsTracker = new GPSTracker(this);

/*		if (gpsTracker.getIsGPSTrackingEnabled())
		{
			gpsLat = String.valueOf(gpsTracker.latitude);
			gpsLong = String.valueOf(gpsTracker.longitude);
			gpsCountry = gpsTracker.getCountryName(this);
			gpsCity = gpsTracker.getLocality(this);
			gpsPin = gpsTracker.getPostalCode(this);
			gpsAddress = gpsTracker.getAddressLine(this);

		}
		else
		{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
	//		gpsTracker.showSettingsAlert();
	//		gpsLat = "0";
	//		gpsLong = "0";
	//		gpsCountry = "0";
	//		gpsCity = "0";
	//		gpsPin = "0";
	//		gpsAddress = "0";
		} */

        edtName = (EditText) findViewById(R.id.edtName);
        edtName2 = (EditText) findViewById(R.id.edtName2);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
     //   btnDate = (Button) findViewById(R.id.btnDate);
      //  btnTime = (Button) findViewById(R.id.btnTime);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtOrderList = (EditText) findViewById(R.id.edtOrderList);
        edtComment = (EditText) findViewById(R.id.edtComment);
        btnSend = (Button) findViewById(R.id.btnSend);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);       
        edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        edtKota = (EditText) findViewById(R.id.edtKota);
        edtProvinsi = (EditText) findViewById(R.id.edtProvinsi);
        edtPassword = (EditText) findViewById(R.id.edtPassword);



        try {
            data_json = getIntent().getStringExtra("data");


          //  Toast.makeText(ActivityCheckout.this,data_json,Toast.LENGTH_LONG).show();
            JSONObject mainObject = new JSONObject(data_json);
            JSONObject uniObject = mainObject.getJSONObject("data");
            edtName.setText(uniObject.getString("Name").trim());
            edtPhone.setText(uniObject.getString("Phone_number").trim());
            edtPassword.setText(uniObject.getString("Password").trim());
            edtAlamat.setText(uniObject.getString("Address").trim());
            edtKota.setText(uniObject.getString("City").trim());
            edtProvinsi.setText(uniObject.getString("State").trim());
            edtEmail.setText(uniObject.getString("Email").trim());
            edtComment.setText(uniObject.getString("Comment").trim());

            edtPhone.setVisibility(View.GONE);
            edtPassword.setVisibility(View.GONE);

            // Toast.makeText(ActivityCheckout.this,uniName,Toast.LENGTH_LONG).show();


        }  catch (Exception ex) {

        }
       // prefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
      //  edtPassword.setVisibility(View.GONE);
       // final String phone = prefs.getString("Phone",null);

      //  final String name = prefs.getString("Name", null);
       // edtPhone.setText(phone);
       // edtName.setText(name);


     //   btnDate.setVisibility(View.INVISIBLE);
     //   btnTime.setVisibility(View.INVISIBLE);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	             R.array.shipping_array, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     spinner.setAdapter(adapter);
	     
	     spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
					switch(arg2) {
					
						case 0 :
							edtName2.setText("Cash On Delivery"); //COD
							break;

                        case 1 :
                            edtName2.setText("Pay Online"); //COD
                            break;

						default :
							edtName2.setText("Cash On Delivery"); //COD
							break;
					}				
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});

        // tax and currency API url
		TaxCurrencyAPI = Constant.TaxCurrencyAPI+"?accesskey="+Constant.AccessKey;
        
        dbhelper = new DBHelper(this);
        // open database
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		
		// call asynctask class to request tax and currency data from server
        new getTaxCurrency().execute();        
        
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
				Name2 = edtName2.getText().toString();
                Password = edtPassword.getText().toString();
			//	Date = btnDate.getText().toString();
			//	Time = btnTime.getText().toString();
                Date = "";
                Time = "";
				Phone = edtPhone.getText().toString();
				Comment = edtComment.getText().toString();
			//	Comment = Comment + "Geolocation: \n\r"+gpsLong+" , "+gpsLat+ " ";
				Date_n_Time = Date+" "+Time;
				if(Name.equalsIgnoreCase("") || Email.equalsIgnoreCase("") ||   Alamat.equalsIgnoreCase("") || Kota.equalsIgnoreCase("") || Provinsi.equalsIgnoreCase("") ||
					//	Date.equalsIgnoreCase(getString(R.string.date)) ||
					//	Time.equalsIgnoreCase(getString(R.string.time)) ||
						Phone.equalsIgnoreCase("")){
					Toast.makeText(ActivityCheckout.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
				}else if((data.size() == 0)){
					Toast.makeText(ActivityCheckout.this, R.string.order_alert, Toast.LENGTH_SHORT).show();
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
    public class getTaxCurrency extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getTaxCurrency(){
	 		if(!prgLoading.isShown()){
	 			prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
	 		}
	 	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			parseJSONDataTax();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
 			prgLoading.setVisibility(8);
 			// if internet connection and data available request menu data from server
 			// otherwise, show alert text
			if(IOConnect == 0){
				new getDataTask().execute();
			}else{
				txtAlert.setVisibility(0);
			}
		}
    }
    
    // method to parse json data from server
	public void parseJSONDataTax(){
	
		try {
			// request data from tax and currency API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        HttpUriRequest request = new HttpGet(TaxCurrencyAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();
	
			
			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		        
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }
	    
	        // parse json data and store into tax and currency variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part
				
				
			JSONObject object_tax = data.getJSONObject(0); 
			JSONObject tax = object_tax.getJSONObject("tax_n_currency");
			    
			//Tax = Double.parseDouble(tax.getString("Value"));
            Delivery = Double.parseDouble(tax.getString("Value"));

			JSONObject object_currency = data.getJSONObject(1); 
			JSONObject currency = object_currency.getJSONObject("tax_n_currency");
				    
			Currency = currency.getString("Value");
					
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			IOConnect = 1;
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}	
	}
	
	// asynctask class to get data from database in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			getDataFromDatabase();
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
			 dialog= ProgressDialog.show(ActivityCheckout.this, "", 
	                 getString(R.string.sending_alert), true);
		  	
		 }

		 @Override
		 protected Void doInBackground(Void... params) {
		  // TODO Auto-generated method stub
			 // send data to server and store result to variable
			 Result = getRequest(Name, Alamat, Kota, Provinsi, Email, Name2, Date_n_Time, Phone, OrderList, Comment, Password);
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
		//Toast.makeText(ActivityCheckout.this,HasilProses,Toast.LENGTH_LONG).show();
        if(HasilProses.trim().equalsIgnoreCase("OK")){

//            Toast.makeText(ActivityCheckout.this, edtName2.getText(), Toast.LENGTH_SHORT).show();

            if(edtName2.getText().toString().equals("Pay Online")) {

               // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://chamber3.uddanpromotions.in/payu/?amount="+Total_price+"&phone="+Phone+"&firstname="+Name+"&email="+Email));
               /// startActivity(browserIntent);

            } else {
                Toast.makeText(ActivityCheckout.this, R.string.ok_alert, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ActivityCheckout.this, ActivityConfirmMessage.class);
                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();
            }
		}else if(HasilProses.trim().equalsIgnoreCase("Failed")){
			Toast.makeText(ActivityCheckout.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
		}else{
			Log.d("HasilProses", HasilProses);
		}
	}
	
    // method to post data to server
	public String getRequest(String name, String alamat, String kota, String provinsi, String email, String name2, String date_n_time, String phone, String orderlist, String comment, String password){
		String result = "";
		
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.SendDataAPI);
        
        try{
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        	nameValuePairs.add(new BasicNameValuePair("name", name));
        	nameValuePairs.add(new BasicNameValuePair("alamat", alamat));
        	nameValuePairs.add(new BasicNameValuePair("kota", kota));
        	nameValuePairs.add(new BasicNameValuePair("provinsi", provinsi));
        	nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("name2", name2));
            nameValuePairs.add(new BasicNameValuePair("date_n_time", date_n_time));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("order_list", orderlist));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
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
    public void getDataFromDatabase(){
    	
    	data = dbhelper.getAllData();


    	double Order_price = 0;

    	double tax = 0;
    	
    	// store all data to variables
    	for(int i=0;i<data.size();i++){
    		ArrayList<Object> row = data.get(i);
    		
    		String Menu_name = row.get(1).toString();
    		String Quantity = row.get(2).toString();
    		double Sub_total_price = Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString())));
    		Order_price += Sub_total_price;
    		
    		// calculate order price
    		OrderList += (Quantity+" x "+Menu_name+ " - " + Currency + ""+Sub_total_price+"\n");
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
