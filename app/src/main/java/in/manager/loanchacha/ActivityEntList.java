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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityEntList extends Activity {
	
	GridView listMenu, listCategory;
	ProgressBar prgLoading;
	//TextView txtTitle;
	EditText edtKeyword;
	ImageButton btnSearch;
	TextView txtAlert;
	
	// declare static variable to store tax and currency symbol
	static double Tax;
	static String Currency;
	
	// declare adapter object to create custom menu list
	AdapterEntList mla;

	AdapterEntCategoryList cla;

	// create arraylist variables to store data from server
	static ArrayList<Long> Category_ID = new ArrayList<Long>();
	static ArrayList<String> Category_name = new ArrayList<String>();
	static ArrayList<String> Category_image = new ArrayList<String>();
	static ArrayList<String> IsLeaf = new ArrayList<String>();

	String CategoryAPI;
	
	// create arraylist variables to store data from server
	static ArrayList<Long> Menu_ID = new ArrayList<Long>();
	static ArrayList<String> Menu_name = new ArrayList<String>();
	static ArrayList<Double> Menu_price = new ArrayList<Double>();
    static ArrayList<Double> Menu_max_price = new ArrayList<Double>();
	static ArrayList<String> Menu_image = new ArrayList<String>();
	
	String MenuAPI;
	String TaxCurrencyAPI;
	int IOConnect = 0;
	long Category_Menu_ID;
	String Category_Menu_name;
	String Keyword;
	
	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ent_menu_list);

/*
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
*/

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Greetings");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listMenu = (GridView) findViewById(R.id.listMenu);
        edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
		listCategory = (GridView) findViewById(R.id.listCategory);
        
        // menu API url

      //  Toast.makeText(ActivityEntList.this,"HELLO",Toast.LENGTH_LONG).show();
        MenuAPI = Constant.EntMenuAPI+"?accesskey="+Constant.AccessKey+"&category_id=";
        // tax and currency API url
        TaxCurrencyAPI = Constant.TaxCurrencyAPI+"?accesskey="+Constant.AccessKey;
        
        // get category id and category name that sent from previous page
        Intent iGet = getIntent();
        Category_Menu_ID = iGet.getLongExtra("category_id",0);
        Category_Menu_name = iGet.getStringExtra("category_name");

    //    bar.setTitle(Category_Menu_name);

        MenuAPI += Category_Menu_ID;


		cla = new AdapterEntCategoryList(ActivityEntList.this);

		CategoryAPI = Constant.EntCategoryAPI+"?accesskey="+Constant.AccessKey;


	//	Toast.makeText(ActivityEntList.this,MenuAPI,Toast.LENGTH_LONG).show();
        
        // set category name to textview
//        txtTitle.setText(Category_name);
        
        mla = new AdapterEntList(ActivityEntList.this);
       
        // call asynctask class to request tax and currency data from server




        new getDataTask().execute();
        new getDataTaskCategory().execute();
		
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
                Intent intent = new Intent(ActivityEntList.this,ActivityMenuSearchList.class);
                intent.putExtra("keyword",Keyword);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);



			}
		});

		listCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				if(i==0) {

					IOConnect = 0;
					listMenu.invalidateViews();
					MenuAPI = Constant.EntMenuAPI+"?accesskey="+Constant.AccessKey+"&category_id=0";
					clearData();
					new getDataTask().execute();

				} else {
					IOConnect = 0;
					listMenu.invalidateViews();
					MenuAPI = Constant.EntMenuAPI+"?accesskey="+Constant.AccessKey+"&category_id="+Category_ID.get(i);
					clearData();
					new getDataTask().execute();

				}


			}
		});
		
		// event listener to handle list when clicked
		listMenu.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				// go to menu detail page
				Intent iDetail = new Intent(ActivityEntList.this, ActivityEntDetail.class);
				iDetail.putExtra("menu_id", Menu_ID.get(position));
				iDetail.putExtra("menu_name", Menu_name.get(position));
				startActivity(iDetail);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
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
		case R.id.cart:
			// refresh action
			Intent iMyOrder = new Intent(ActivityEntList.this, ActivityCart.class);
			startActivity(iMyOrder);
			overridePendingTransition (R.anim.open_next, R.anim.close_next);
			return true;
			
		case R.id.refresh:
			IOConnect = 0;
			MenuAPI = Constant.EntMenuAPI+"?accesskey="+Constant.AccessKey+"&category_id=0";
			listMenu.invalidateViews();
			clearData();
			new getDataTask().execute();
			return true;			
			
		case android.R.id.home:
            // app icon in action bar clicked; go home
        	this.finish();
        	overridePendingTransition(R.anim.open_main, R.anim.close_next);
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
				txtAlert.setVisibility(0);
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
			prgLoading.setVisibility(0);
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

			    
			Tax = 0;
			

			    
			Currency = "Rs. ";
			
			

	}
    
	// clear arraylist variables before used
    void clearData(){
    	Menu_ID.clear();
    	Menu_name.clear();
    	Menu_price.clear();
        Menu_max_price.clear();
    	Menu_image.clear();
    }
    
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getDataTask(){
    		if(!prgLoading.isShown()){
    			prgLoading.setVisibility(0);
				txtAlert.setVisibility(0);
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
			if(Menu_ID.size() > 0){
				listMenu.setVisibility(0);
				listMenu.setAdapter(mla);
			}else{
				txtAlert.setVisibility(0);
				Toast.makeText(ActivityEntList.this,"No Article Found. Try Again Later",Toast.LENGTH_LONG).show();
			}
			
		}
    }
    
    // method to parse json data from server
    public void parseJSONData(){
    	
    	clearData();
    	
    	try {
	        // request data from menu API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 35000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 35000);
	        HttpUriRequest request = new HttpGet(MenuAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();

			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		        
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }
        
			// parse json data and store into arraylist variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part
			
			for (int i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject menu = object.getJSONObject("Menu");
			    
			    Menu_ID.add(Long.parseLong(menu.getString("Menu_ID")));
			    Menu_name.add(menu.getString("Menu_name"));
			  //  Menu_price.add(0D);
              //  Menu_max_price.add(0D);
			    Menu_image.add(menu.getString("Menu_image"));
				    
			}
				
				
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}	
    }


    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//mla.imageLoader.clearCache();
    	listMenu.setAdapter(null);
    	super.onDestroy();
    }

	public class getDataTaskCategory extends AsyncTask<Void, Void, Void>{

		// show progressbar first
		getDataTaskCategory(){
			if(!prgLoading.isShown()){
				prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			parseJSONDataCategory();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(8);

			// if internet connection and data available show data on list
			// otherwise, show alert text
			if((Category_ID.size() > 0) && (IOConnect == 0)){
				listCategory.setVisibility(0);

			/*	if(Category_ID.size()<=3) {
				//	listCategory.setNumColumns(8);
                    listCategory.setHorizontalScrollBarEnabled(false);
                    //listCategory.sc

				} else {
				//	listCategory.setNumColumns(Category_ID.size());
				}*/

				listCategory.setNumColumns(8);

				listCategory.setAdapter(cla);
			}else{
				txtAlert.setVisibility(0);
			}
		}
	}

	// method to parse json data from server
	public void parseJSONDataCategory(){

		clearDataCategory();

		try {
			// request data from Category API
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
			HttpUriRequest request = new HttpGet(CategoryAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

			String line;
			String str = "";
			while ((line = in.readLine()) != null){
				str += line;
			}

			// parse json data and store into arraylist variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data");

			Category_ID.add(0L);
			Category_name.add("All");
			Category_image.add("");
			IsLeaf.add("1");

			for (int i = 0; i < data.length(); i++) {
				JSONObject object = data.getJSONObject(i);

				JSONObject category = object.getJSONObject("Category");

				Category_ID.add(Long.parseLong(category.getString("Category_ID")));
				Category_name.add(category.getString("Category_name"));
				Category_image.add(category.getString("Category_image"));
				IsLeaf.add(category.getString("is_leaf"));
				Log.d("Category name", Category_name.get(i));

			}


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


	void clearDataCategory(){
		Category_ID.clear();
		Category_name.clear();
		Category_image.clear();
		IsLeaf.clear();
	}
    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    
}
