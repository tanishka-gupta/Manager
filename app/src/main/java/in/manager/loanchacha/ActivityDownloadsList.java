package in.manager.loanchacha;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class ActivityDownloadsList extends Activity {

    GridView listMenu;
    ProgressBar prgLoading;
    //TextView txtTitle;
    EditText edtKeyword;
    ImageButton btnSearch;
    ProgressDialog prgdialog;
    TextView txtAlert;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;

    // declare static variable to store tax and currency symbol
    static double Tax;
    static String Currency;
    long enq;
    boolean is_download = true;

    // declare adapter object to create custom menu list
    AdapterDownloadsList mla;

    // create arraylist variables to store data from server
    static ArrayList<Long> Menu_ID = new ArrayList<Long>();
    static ArrayList<String> Menu_name = new ArrayList<String>();
    static ArrayList<String> Menu_patient = new ArrayList<String>();

    static ArrayList<String> Menu_price = new ArrayList<String>();
    static ArrayList<String> Menu_image = new ArrayList<String>();
    static ArrayList<String> Menu_Date = new ArrayList<String>();

    String MenuAPI;
    String TaxCurrencyAPI;
    int IOConnect = 0;
    long Category_ID;
    String Category_name;
    String Keyword;

    // create price format
    DecimalFormat formatData = new DecimalFormat("#.##");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloads_list);

/*
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
*/


        prefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String phone = prefs.getString("Phone",null);
        final String password = prefs.getString("Password", null);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Loan Status");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listMenu = (GridView) findViewById(R.id.listMenu);
        //  edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        // btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        txtAlert = (TextView) findViewById(R.id.txtAlert);

        // menu API url
        MenuAPI = Constant.DownloadsAPI+"?accesskey="+Constant.AccessKey+"&phone="+phone+"&category_id=";
        // tax and currency API url
        TaxCurrencyAPI = Constant.TaxCurrencyAPI+"?accesskey="+Constant.AccessKey;

        // get category id and category name that sent from previous page
        Intent iGet = getIntent();
        Category_ID = iGet.getLongExtra("category_id",0);
        Category_name = iGet.getStringExtra("category_name");
        MenuAPI += Category_ID;

    //    Category_ID = 10;
      //  Category_name = "Photo Gallery";
        //MenuAPI += Category_ID;
       //   Toast.makeText(ActivityDownloadsList.this,MenuAPI,Toast.LENGTH_LONG).show();
        // set category name to textview
//        txtTitle.setText(Category_name);

        mla = new AdapterDownloadsList(ActivityDownloadsList.this);

        // call asynctask class to request tax and currency data from server
     //   new getTaxCurrency().execute();

        // event listener to handle search button when clicked
	/*	btnSearch.setOnClickListener(new OnClickListener() {
			
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
		}); */

        new getDataTask().execute();



        // event listener to handle list when clicked
        listMenu.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu detail page
              //  Intent iDetail = new Intent(ActivityDownloadsList.this, ActivityGalleryDetail.class);
               // iDetail.putExtra("menu_id", Menu_ID.get(position));
               // startActivity(iDetail);
               // overridePendingTransition(R.anim.open_next, R.anim.close_next);







            }
        });

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                    prgdialog.dismiss();
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enq);
                    DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //TODO : Use this local uri and launch intent to open file

//                            Toast.makeText(ActivityDownloadsList.this,uriString,Toast.LENGTH_LONG).show();

                         //   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
                         //   overridePendingTransition(R.anim.open_next, R.anim.close_next);

//                            Toast.makeText(ActivityDownloadsList.this,"File downloaded in your download folder",Toast.LENGTH_LONG).show();


                            Uri attachmentUri = Uri.parse(uriString);
                            if(ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                                File file = new File(attachmentUri.getPath());
                                attachmentUri = FileProvider.getUriForFile(ActivityDownloadsList.this,"in.manager.loanchacha.provider",file);
                            }


                            if(is_download) {

                                Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);

                                openAttachmentIntent.setData(attachmentUri);
                                openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                try {

                                    startActivity(openAttachmentIntent);
                                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                } catch (Exception ex) {
                                    Toast.makeText(ActivityDownloadsList.this,"File downloaded in your download folder",Toast.LENGTH_LONG).show();
                                }

                            } else {

                                Intent openAttachmentIntent = new Intent(Intent.ACTION_SEND);

                                openAttachmentIntent.setType("*/*");

                                openAttachmentIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);

                                //  openAttachmentIntent.putExtra(Intent.EXTRA_SUBJ, attachmentUri);

                                try {

                                    startActivity(openAttachmentIntent);
                                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                } catch (Exception ex) {
                                    Toast.makeText(ActivityDownloadsList.this,"File downloaded in your download folder",Toast.LENGTH_LONG).show();
                                }

                            }





                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
			/*Intent iMyOrder = new Intent(ActivityMenuList.this, ActivityCart.class);
			startActivity(iMyOrder);
			overridePendi ngTransition (R.anim.open_next, R.anim.close_next);
			return true; */
                String uri = "tel:" + Constant.Helpline ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                return true;

            case R.id.refresh:
                IOConnect = 0;
                listMenu.invalidateViews();
                clearData();
                new getDataTask().execute();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;




              //  return true;

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
        Menu_ID.clear();
        Menu_name.clear();
        Menu_patient.clear();
        Menu_price.clear();
        Menu_image.clear();
        Menu_Date.clear();
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
            if(Menu_ID.size() > 0){
                listMenu.setVisibility(0);
                listMenu.setAdapter(mla);
            }else{
                txtAlert.setVisibility(0);
            }

        }
    }

    // method to parse json data from server
    public void parseJSONData(){

        clearData();

        try {
            // request data from menu API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
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
                Menu_image.add(menu.getString("Menu_image"));
                Menu_patient.add(menu.getString("patient_name"));
                Menu_Date.add(menu.getString("date_time"));

              //  Toast.makeText(ActivityGalleryList.this,menu.getString("Menu_name").toString(),Toast.LENGTH_LONG).show();
              //  Menu_Date.add(menu.getString("Date"));
            }


        } /* catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */
        catch(Exception ex) {
           //Toast.makeText(ActivityGalleryList.this,ex.toString(),Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream

                String fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                        + fileName);



                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
               // Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            Toast.makeText(ActivityDownloadsList.this,"COMPLETED",Toast.LENGTH_LONG).show();

        }

    }

}
