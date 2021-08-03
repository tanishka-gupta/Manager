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
import android.database.SQLException;
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



public class ActivityMenuBulk  extends Activity {

    GridView listMenu;
    ProgressBar prgLoading;
    //TextView txtTitle;
    EditText edtKeyword;
    ArrayList<ArrayList<Object>> data;
    ImageButton btnSearch;
    TextView txtAlert;
    TextView tv;
    Button btnPlaceOrder;
    static DBHelper dbhelper;
    ArrayList<ArrayList<Object>> data_cart;

    // declare static variable to store tax and currency symbol
    static double Tax = 0;
    static String Currency = "Rs. ";

    // declare adapter object to create custom menu list
    AdapterMenuBulk mla;

    // create arraylist variables to store data from server
    static ArrayList<Long> Menu_ID = new ArrayList<Long>();
    static ArrayList<Long> Selected_Menu_ID = new ArrayList<Long>();
    static ArrayList<String> Menu_name = new ArrayList<String>();
    static ArrayList<String> Selected_Menu_name = new ArrayList<String>();
    static ArrayList<String> Selected_Menu_option = new ArrayList<String>();
    static ArrayList<String> Menu_name_hindi = new ArrayList<String>();
    static ArrayList<Double> Menu_price = new ArrayList<Double>();
    static ArrayList<Double> Selected_Menu_price = new ArrayList<Double>();
    static ArrayList<String> Menu_image = new ArrayList<String>();
    static ArrayList<Integer> Menu_quantity = new ArrayList<Integer>();
    static ArrayList<Integer> Menu_pos = new ArrayList<Integer>();
    static ArrayList<Double> Menu_max_price = new ArrayList<Double>();
    static ArrayList<Long> Menu_Stock = new ArrayList<Long>();
    static ArrayList<String> Menu_Sizes = new ArrayList<String>();
    static ArrayList<String> Menu_Prices = new ArrayList<String>();


    String MenuAPI;
    String TaxCurrencyAPI;
    int IOConnect = 0;
    static long Category_ID;
    static String Category_name;
    static String Keyword;

    // create price format
    DecimalFormat formatData = new DecimalFormat("#.##");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list_bulk);
        dbhelper = new DBHelper(this);

/*
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
*/

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Loan Packages");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listMenu = (GridView) findViewById(R.id.listMenu);
        edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        btnPlaceOrder = (Button) findViewById(R.id.place_order);
        btnPlaceOrder.setVisibility(View.GONE);

        // menu API url
        MenuAPI = Constant.MenuAPI+"?accesskey="+Constant.AccessKey+"&category_id=";
        // tax and currency API url
        TaxCurrencyAPI = Constant.TaxCurrencyAPI+"?accesskey="+Constant.AccessKey;

        // get category id and category name that sent from previous page
        Intent iGet = getIntent();
        Category_ID = iGet.getLongExtra("category_id",0);
        Category_name = iGet.getStringExtra("category_name");
        Keyword = iGet.getStringExtra("keyword");
        MenuAPI += Category_ID;
        if(Keyword!=null && !Keyword.isEmpty() ) {
            MenuAPI += "&keyword="+Keyword;
        }

       Toast.makeText(ActivityMenuBulk.this,MenuAPI.toString(),Toast.LENGTH_LONG).show();
        // set category name to textview
//        txtTitle.setText(Category_name);

        mla = new AdapterMenuBulk(ActivityMenuBulk.this);

        // call asynctask class to request tax and currency data from server
      //  new getTaxCurrency().execute();
        new getDataTask().execute();

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
		//		Intent iDetail = new Intent(ActivityMenuBulk.this, ActivityMenuDetail.class);
		//		iDetail.putExtra("menu_id", Menu_ID.get(position));
		//		startActivity(iDetail);
		//		overridePendingTransition(R.anim.open_next, R.anim.close_next);

                Intent iDetail = new Intent(ActivityMenuBulk.this, ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", ActivityMenuBulk.Menu_ID.get(position));
                iDetail.putExtra("qty", String.valueOf(ActivityMenuBulk.Menu_quantity.get(position)));
                iDetail.putExtra("category_id",ActivityMenuBulk.Category_ID);
                iDetail.putExtra("keyword",ActivityMenuBulk.Keyword);
                iDetail.putExtra("category_name",ActivityMenuBulk.Category_name);

                startActivity(iDetail);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);



			}
		});

        btnPlaceOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //    Toast.makeText(ActivityMenuList.this,String.valueOf(Menu_quantity.size()),Toast.LENGTH_LONG).show();
                try{
                    dbhelper.openDataBase();
                }catch(SQLException sqle){
                    throw sqle;
                }
                try {
                    boolean isZero = true;
                    for (int i = 0; i < Menu_quantity.size(); i++) {
                        //  Toast.makeText(ActivityMenuList.this, Menu_quantity.get(i).toString(), Toast.LENGTH_SHORT).show();

                        // add cart
                        if (Menu_quantity.get(i) != 0) {
                            isZero = false;
                            if (dbhelper.isDataExist(Menu_ID.get(i))) {
                                dbhelper.updateData(Menu_ID.get(i), Menu_quantity.get(i), (Menu_price.get(i) * Menu_quantity.get(i)));
                                // Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            } else {
                                dbhelper.addData(Menu_ID.get(i), Menu_name.get(i), Menu_quantity.get(i), (Menu_price.get(i) * Menu_quantity.get(i)));
                                //Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            }
                        }


                    }
                    if (isZero == true) {
                      //  Toast.makeText(ActivityMenuBulk.this, "Change the quantity of product using +/- or direct input.", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(ActivityMenuBulk.this, ActivityCart.class));
                        overridePendingTransition(R.anim.open_next, R.anim.close_next);

                    }
                    mla.notifyDataSetChanged();
                } catch(Exception ex) {
                    Toast.makeText(ActivityMenuBulk.this,ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    public void update_cart() {
        try {

            dbhelper.openDataBase();
        } catch (Exception ex) {

        }
        data = dbhelper.getAllData();
        tv.setText(String.valueOf(data.size()));
        //	tv.setText("99");
        if(data.size()==0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }

        try {

            dbhelper.close();
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.cart:
                // refresh action
                Intent iMyOrder = new Intent(ActivityMenuBulk.this, ActivityCart.class);
                startActivity(iMyOrder);
                overridePendingTransition (R.anim.open_next, R.anim.close_next);
                return true;

            case R.id.refresh:
                IOConnect = 0;
                listMenu.invalidateViews();
                clearData();
                new getDataTask().execute();
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


            //Tax = Double.parseDouble(tax.getString("Value"));
            String[] separated = tax.getString("Value").split("::");
            Tax = Double.parseDouble(separated[0]);

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
        Menu_name_hindi.clear();
        Menu_price.clear();
        Menu_image.clear();
        Menu_max_price.clear();
        Menu_quantity.clear();
        Menu_Stock.clear();
        Menu_Prices.clear();
        Menu_Sizes.clear();
        Selected_Menu_ID.clear();
        Selected_Menu_name.clear();
        Selected_Menu_price.clear();
        Selected_Menu_option.clear();
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



//            Toast.makeText(ActivityMenuBulk.this,"Input Quantity or Touch +/- button to change quantity of products",Toast.LENGTH_LONG).show();

        }
    }

    // method to parse json data from server
    public void parseJSONData(){

        clearData();

        try {
            // request data from menu API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 3500000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 3500000);
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
            try {
                dbhelper.openDataBase();
                data_cart = dbhelper.getAllData();
            } catch(Exception ex) {

            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject menu = object.getJSONObject("Menu");

                Menu_ID.add(Long.parseLong(menu.getString("Menu_ID")));

                Menu_Stock.add(1L);
                Menu_name.add(menu.getString("Menu_name"));


                Menu_name_hindi.add(menu.getString("Menu_name"));
                Menu_price.add(0D);


                Menu_image.add(menu.getString("Menu_image"));
                Menu_Sizes.add("");
                Menu_Prices.add("");

                Menu_max_price.add(0D);
                // if(Menu_ID.get(i).)
                Menu_quantity.add(0);

                Menu_pos.add(i);


               if(menu.getString("size_heading").length() > 0) {

                   final String[] sz = menu.getString("size_heading").split(",");

                   final String[] pr = menu.getString("size_price").split(",");


                   Selected_Menu_price.add(Double.valueOf(formatData.format(Double.parseDouble(pr[0]))));



                   Selected_Menu_name.add(menu.getString("Menu_name") + " - " + sz[0]);
                   Selected_Menu_option.add(sz[0]);
                   Selected_Menu_ID.add(Long.parseLong(menu.getString("Menu_ID")) +  Long.parseLong(String.valueOf(0)));



               } else {

                   Selected_Menu_price.add(0D);
                   Selected_Menu_name.add(menu.getString("Menu_name"));
                   Selected_Menu_option.add("");
                   Selected_Menu_ID.add(Long.parseLong(menu.getString("Menu_ID")));

               }



                // store data to arraylist variables



            }
            try {
            for(int kj2 = 0 ; kj2<Menu_ID.size(); kj2++) {

                for (int ij = 0; ij < data_cart.size(); ij++) {

                    ArrayList<Object> row = data_cart.get(ij);
                    try {




                            if (Menu_ID.get(kj2).toString().equals(row.get(0).toString())) {
                                Menu_quantity.set(kj2,Integer.parseInt(row.get(2).toString()));
                                break;
                            } else {


                                Menu_quantity.set(kj2,0);
                            }



                    } catch (Exception ex) {

                    }


                }

            } } catch(Exception ex) {

            }
            /*if(data_cart.size()!=0) {
                for (int ij = 0; ij < data_cart.size(); ij++) {

                    ArrayList<Object> row = data_cart.get(ij);
                    try {

                        for(int kj2 = 0 ; kj2<Menu_ID.size(); kj2++) {


                            if (Menu_ID.get(kj2).toString().equals(row.get(0).toString())) {
                                Menu_quantity.set(kj2,Integer.parseInt(row.get(2).toString()));
                                break;
                            } else {


                                Menu_quantity.set(kj2,0);
                            }
                        }

                    } catch(Exception ex) {
                      //  Menu_quantity.add(0);
                    }

                }
            } else {
           //     Menu_quantity.add(0);
            } */

            try {
                dbhelper.close();

            } catch(Exception ex) {

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


    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        IOConnect = 0;
        listMenu.invalidateViews();
        clearData();

        new getDataTask().execute();
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
