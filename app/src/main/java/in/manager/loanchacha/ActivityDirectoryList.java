package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityDirectoryList extends Activity {
	
	GridView listMenu;
	ProgressBar prgLoading;
	Button btnPrevious, btnNext, btnCallNow, btnSendEnquiry, btnView;
	//TextView txtTitle;
	EditText edtKeyword;
	ImageButton btnSearch;
	TextView txtAlert;
    static DBHelper dbhelper;

	int total_records=0;

	// declare static variable to store tax and currency symbol
	static double Tax;
	static String Currency;
	int total =0;
	int num_of_pages = 1;
	int start, end, page;

	
	// declare adapter object to create custom menu list
	AdapterDirectoryList mla;
	
	// create arraylist variables to store data from server
	static ArrayList<Integer> Resume_ID = new ArrayList<Integer>();
    static ArrayList<String> Resume_Name = new ArrayList<String>();
    static ArrayList<String> Resume_Qualification = new ArrayList<String>();
    static ArrayList<String> Resume_Age = new ArrayList<String>();
    static ArrayList<String> Resume_Gender = new ArrayList<String>();
    static ArrayList<String> Resume_Experience = new ArrayList<String>();
    static ArrayList<String> Resume_Salary = new ArrayList<String>();
    static ArrayList<String> Resume_Job = new ArrayList<String>();
    static ArrayList<String> Resume_Image = new ArrayList<String>();

	int last_record, pre_last_record;



	
	String MenuAPI, CountAPI;
	String TaxCurrencyAPI;
	int IOConnect = 0;
	long Category_ID;
    String cat_id, location_id, job_id;

	String Category_name;
	String Keyword, City, Gotra, Name, State, FormNuber, BloodGroup;
	
	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_list);
        dbhelper = new DBHelper(this);

/*
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
*/

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Faculties / Teachers");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

		btnNext = (Button) findViewById(R.id.btnNextJob);
		btnPrevious = (Button) findViewById(R.id.btnPrevJob);


        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listMenu = (GridView) findViewById(R.id.listMenu);
        edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        txtAlert = (TextView) findViewById(R.id.txtAlert);

        // menu API url
        MenuAPI = Constant.DirectoryMenuAPI+"?accesskey="+Constant.AccessKey+"&cat_id=";
		CountAPI = Constant.DirectoryCountAPI+"?accesskey="+Constant.AccessKey+"&cat_id=";
      //  Toast.makeText(ActivityMenuList.this,MenuAPI,Toast.LENGTH_LONG).show();
        // tax and currency API url
        TaxCurrencyAPI = Constant.TaxCurrencyAPI+"?accesskey="+Constant.AccessKey;
        
        // get category id and category name that sent from previous page
        Intent iGet = getIntent();
        Category_ID = iGet.getLongExtra("category_id",43);
        Category_name = iGet.getStringExtra("category_name");
		start = iGet.getIntExtra("start",1);
		end = iGet.getIntExtra("end",25);
		page = iGet.getIntExtra("page",1);
		last_record = iGet.getIntExtra("last_record",-1);
		pre_last_record = iGet.getIntExtra("pre_last_record",-1);



        cat_id = iGet.getStringExtra("cat_id");
        location_id=iGet.getStringExtra("location_id");
		job_id=iGet.getStringExtra("job_id");
        Keyword = iGet.getStringExtra("keyword");
		City = iGet.getStringExtra("city");
		Gotra = iGet.getStringExtra("gotra");
		Name = iGet.getStringExtra("name");
		State = iGet.getStringExtra("state");
		FormNuber = iGet.getStringExtra("id");
		BloodGroup = iGet.getStringExtra("blood");

        MenuAPI += cat_id;
        MenuAPI += "&keyword="+Keyword;
		MenuAPI += "&name="+Name;
		MenuAPI += "&gotra="+Gotra;
		MenuAPI += "&city="+City;
		MenuAPI += "&state="+State;
        MenuAPI += "&location_id="+location_id;
		MenuAPI += "&job_id="+job_id;
		MenuAPI += "&form="+FormNuber;
		MenuAPI += "&blood="+BloodGroup;
		MenuAPI += "&start="+start;
		MenuAPI += "&end="+end;



		CountAPI += cat_id;
		CountAPI += "&keyword="+Keyword;
		CountAPI += "&name="+Name;
		CountAPI += "&gotra="+Gotra;
		CountAPI += "&city="+City;
		CountAPI += "&state="+State;
		CountAPI += "&location_id="+location_id;
		CountAPI += "&job_id="+job_id;
		CountAPI += "&form="+FormNuber;
		CountAPI += "&blood="+BloodGroup;
		CountAPI += "&start="+start;
		CountAPI += "&end="+end;


      //  Toast.makeText(ActivityDirectoryList.this,MenuAPI,Toast.LENGTH_LONG).show();

        mla = new AdapterDirectoryList(ActivityDirectoryList.this);
       
        // call asynctask class to request tax and currency data from server
        new getTaxCurrency().execute();
		
        // event listener to handle search button when clicked
		btnSearch.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// get keyword and send it to server
				try {
					Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MenuAPI += "&keyword="+Keyword;
				IOConnect = 0;
    			listMenu.invalidateViews();
    			clearData();
				new getDataTask().execute();
			}
		});

        // event listener to handle list when clicked
        listMenu.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu detail page
                Intent iDetail = new Intent(ActivityDirectoryList.this, ActivityDirectoryDetail.class);
                iDetail.putExtra("resume_id", Resume_ID.get(position));
                iDetail.putIntegerArrayListExtra("resume_array",Resume_ID);
                startActivity(iDetail);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        // event listener to handle list when clicked

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {


				try {
				//	if(pre_last_record!=last_record && total_records >= (start+25)) {
					if(page < num_of_pages) {
						Intent iDetail = new Intent(ActivityDirectoryList.this, ActivityDirectoryList.class);
						iDetail.putExtra("category_id", Category_ID);
						iDetail.putExtra("category_name", Category_name);
						iDetail.putExtra("start", start + 25);
						iDetail.putExtra("end", 25);
						iDetail.putExtra("page", page + 1);
						iDetail.putExtra("cat_id", cat_id);
						iDetail.putExtra("location_id", location_id);
						iDetail.putExtra("job_id", job_id);
						iDetail.putExtra("keyword", Keyword);
						iDetail.putExtra("city", City);
						iDetail.putExtra("gotra", Gotra);
						iDetail.putExtra("name", Name);
						iDetail.putExtra("state", State);
						iDetail.putExtra("id", FormNuber);
						iDetail.putExtra("blood", BloodGroup);

						iDetail.putExtra("pre_last_record", last_record);
						iDetail.putExtra("blood", BloodGroup);


						startActivity(iDetail);
						overridePendingTransition(R.anim.open_next, R.anim.close_next);
						finish();
					} else {
						Toast.makeText(ActivityDirectoryList.this,"No More Data", Toast.LENGTH_LONG).show();
						/* Intent iDetail = new Intent(ActivityDirectoryList.this, ActivityDirectoryList.class);
						iDetail.putExtra("category_id", Category_ID);
						iDetail.putExtra("category_name", Category_name);
						iDetail.putExtra("start", start);
						iDetail.putExtra("end", 25);
						iDetail.putExtra("page", page);
						iDetail.putExtra("cat_id", cat_id);
						iDetail.putExtra("location_id", location_id);
						iDetail.putExtra("job_id", job_id);
						iDetail.putExtra("keyword", Keyword);
						iDetail.putExtra("city", City);
						iDetail.putExtra("gotra", Gotra);
						iDetail.putExtra("name", Name);
						iDetail.putExtra("state", State);
						iDetail.putExtra("id", FormNuber);
						iDetail.putExtra("blood", BloodGroup);

						iDetail.putExtra("pre_last_record", last_record);
						iDetail.putExtra("blood", BloodGroup);


						startActivity(iDetail);
						overridePendingTransition(R.anim.open_next, R.anim.close_next);
						finish(); */
					}
				} catch(Exception ex) {
					Toast.makeText(ActivityDirectoryList.this,"No More Data", Toast.LENGTH_LONG).show();
				}

			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				try {
					if((start-25) >= 1) {
						Intent iDetail = new Intent(ActivityDirectoryList.this, ActivityDirectoryList.class);
						iDetail.putExtra("category_id", Category_ID);
						iDetail.putExtra("category_name", Category_name);
						iDetail.putExtra("start", start - 25);
						iDetail.putExtra("end", 25);
						iDetail.putExtra("page",page-1);
						iDetail.putExtra("cat_id", cat_id);
						iDetail.putExtra("location_id", location_id);
						iDetail.putExtra("job_id", job_id);
						iDetail.putExtra("keyword", Keyword);
						iDetail.putExtra("city", City);
						iDetail.putExtra("gotra", Gotra);
						iDetail.putExtra("name", Name);
						iDetail.putExtra("state", State);
						iDetail.putExtra("id", FormNuber);
						iDetail.putExtra("blood", BloodGroup);


						startActivity(iDetail);
						overridePendingTransition(R.anim.open_next, R.anim.close_next);
						finish();
					} else {
						Toast.makeText(ActivityDirectoryList.this,"Not available",Toast.LENGTH_LONG).show();
					}
				} catch(Exception ex) {
					Toast.makeText(ActivityDirectoryList.this,"Not available", Toast.LENGTH_LONG).show();
				}

			}
		});



        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_category, menu);
		
//		final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//        	
//        	@Override
//            public boolean onQueryTextChange(String newText) {         	                
//                return true;
//            }  
//        	
//        	@Override
//            public boolean onQueryTextSubmit(String query) {
//        		try {
//					Keyword = URLEncoder.encode(query.toString(), "utf-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();	
//				}
//        		
//            	MenuAPI += "&keyword="+Keyword;
// 				IOConnect = 0;
//     			listMenu.invalidateViews();
//     			clearData();
// 				new getDataTask().execute();  
//                
//                return true;
//            }          
//        });
//        
//        searchView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
//
//            @Override
//            public void onViewDetachedFromWindow(View arg0) {
//            	IOConnect = 0;
//    			listMenu.invalidateViews();
//    			clearData();
//    			new getDataTask().execute();
//            }
//
//            @Override
//            public void onViewAttachedToWindow(View arg0) {
//                // search was opened
//            }
//        });
        
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

			case R.id.cart:
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.Helpline));
				startActivity(intent);
				return  true;

			case R.id.refresh:
				IOConnect = 0;
				listMenu.invalidateViews();
				clearData();
				new getDataTask().execute();
				return true;
			
		default:
			return super.onOptionsItemSelected(item);
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
			if((Currency != null) && IOConnect == 0){
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
			    
			Tax = Double.parseDouble(tax.getString("Value"));
			
			JSONObject object_currency = data.getJSONObject(1); 
			JSONObject currency = object_currency.getJSONObject("tax_n_currency");
			    
			Currency = currency.getString("Value");
			
			
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
			IOConnect = 1;
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}	
	}
    
	// clear arraylist variables before used
    void clearData(){
    	Resume_ID.clear();
        Resume_Age.clear();
        Resume_Name.clear();
        Resume_Experience.clear();
        Resume_Qualification.clear();
        Resume_Salary.clear();
        Resume_Job.clear();
        Resume_Image.clear();
        Resume_Gender.clear();

    }
    
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getDataTask(){
    		if(!prgLoading.isShown()){
    			prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
    		}
    	}
    	
    	@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
    		// parse json data from server in background
			parseJSONData();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(8);
			
			// if data available show data on list
			// otherwise, show alert text
			if(Resume_ID.size() > 0){
				listMenu.setVisibility(0);
				listMenu.setAdapter(mla);
			}else{
				txtAlert.setVisibility(0);
			}

//			Toast.makeText(ActivityDirectoryList.this,total_records,Toast.LENGTH_LONG).show();
			getActionBar().setTitle("All Members");
		}
    }
    
    // method to parse json data from server
    public void parseJSONData(){
    	
    	clearData();
    	
    	try {
	        // request data from menu API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 350000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 350000);
			MenuAPI = MenuAPI.replace("+","%2B");
			MenuAPI = MenuAPI.replace("-","%2D");
			MenuAPI = MenuAPI.replace(" ","+");


			CountAPI = CountAPI.replace("+","%2B");
			CountAPI = CountAPI.replace("-","%2D");
			CountAPI = CountAPI.replace(" ","+");

	        HttpUriRequest request = new HttpGet(MenuAPI);

			//request.addHeader(new BasicHeader("X-Custom-Header", "Some Value\r\n"));


			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();

			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		        
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }



        
			// parse json data and store into arraylist variables

//            Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part
			int i;
			for (i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject menu = object.getJSONObject("Menu");
			    
			    Resume_ID.add(Integer.parseInt(menu.getString("id")));
				Resume_Image.add(menu.getString("image"));
			    Resume_Name.add(menu.getString("name_shop"));
                Resume_Job.add(menu.getString("name_shop"));
                //Resume_Age.add(menu.getString("age"));
                Resume_Gender.add(menu.getString("size"));
              //  Resume_Experience.add(menu.getString("is_verified"));
                Resume_Qualification.add(menu.getString("phone_tenant"));
                Resume_Salary.add(menu.getString("meter_no"));

				Resume_Age.add(menu.getString("shop_number"));

				String shop_status = menu.getString("is_verified");

				if(shop_status.equals("0")) {
					shop_status = "Lifetime";
				} else if(shop_status.equals("1")) {
					shop_status = "Yearly";
				} else  {
					shop_status = "";
				}
				Resume_Experience.add(shop_status);


				/*String[] dd   = menu.getString("birth_date").split("-");
				String birth_date = dd[2] + "-" + dd[1] + "-" + dd[0];

				if(birth_date.equals("00-00-0000")==false) {
					Resume_Age.add(getAge(Integer.parseInt(dd[0]), Integer.parseInt(dd[1]), Integer.parseInt(dd[2])));
				} else {
					Resume_Age.add(menu.getString("age"));
				} */

				total++;


				    
			}

			HttpClient client2 = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client2.getParams(), 350000);
			HttpConnectionParams.setSoTimeout(client2.getParams(), 350000);

			HttpUriRequest request2 = new HttpGet(CountAPI);
			HttpResponse response2 = client2.execute(request2);
			InputStream atomInputStream2 = response2.getEntity().getContent();

			BufferedReader in2 = new BufferedReader(new InputStreamReader(atomInputStream2));

			String line2;
			String str2 = "";
			while ((line2 = in2.readLine()) != null){
				str2 += line2;
			}

			JSONObject json2 = new JSONObject(str2);
			JSONArray data2 = json2.getJSONArray("data2"); // this is the "items: [ ] part

			for (i = 0; i < data2.length(); i++) {
				JSONObject object2 = data2.getJSONObject(i);

				JSONObject menu2 = object2.getJSONObject("Menu2");

				total_records = menu2.getInt("C");

				double nop  = (double) total_records / 25;
				num_of_pages = (int) Math.ceil(nop);



			}
			last_record = Resume_ID.get(i-1);
			//getActionBar().setTitle("Search Results ("+total+")");
				
				
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IndexOutOfBoundsException r) {
			last_record = -1;
		}
    }


    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//mla.imageLoader.clearCache();
    	listMenu.setAdapter(null);
    	super.onDestroy();
    }
	 
    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}

	private String getAge(int year, int month, int day){
		Calendar dob = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		dob.set(year, month, day);

		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
			age--;
		}

		Integer ageInt = new Integer(age);
		String ageS = ageInt.toString();

		return ageS;
	}
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
        dbhelper.close();
    	finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    
}
