package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityMenuDetail extends Activity {
	
	ImageView imgPreview;
	TextView txtText, txtSubText, txtSubTextnaik, txtSelected;
	LinearLayout lytOptionLayout;
	WebView txtDescription;
	ArrayList<ArrayList<Object>> data;
	WebView webSlider;
	TextView tv;
	Button btnAdd, btnBuyNow, btnChooseOption;
	ScrollView sclDetail;
	ProgressBar prgLoading;
	TextView txtAlert;
    String Currency="Rs. ";
	ImageView imgAdd, imgRemove;
	String[] options;
	String[] sz;
	String[] pr;
	
	// declare dbhelper object
	static DBHelper dbhelper;
	
	// declare ImageLoader object
	ImageLoader imageLoader;
	
	// declare variables to store menu data
	String Menu_image, Menu_name, Menu_serve, Menu_description, Sizes, Prices, Selected_Menu_name;
	double Menu_price, Menu_maxPrice, Selected_Menu_price;
	int Menu_quantity, in_stock, selected_Menu_quantity;
	long Menu_ID, selected_Menu_ID;
	String MenuDetailAPI;
	int IOConnect = 0;
    EditText txtQty;
	TextView inStock, outStock;
	static long Category_ID;
	static String Category_name;
	static String Keyword;
	static String qty;
	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");
	LinearLayout lytCart;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail);


     /*   Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */



        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Service Details");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtText = (TextView) findViewById(R.id.txtText);
        txtSubText = (TextView) findViewById(R.id.txtSubText);
        txtSubTextnaik = (TextView) findViewById(R.id.txtSubTextnaik);
        txtDescription = (WebView) findViewById(R.id.txtDescription);
        txtSelected = (TextView) findViewById(R.id.txtSubTextSelected);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        txtQty = (EditText) findViewById(R.id.txt_qty);
		imgAdd = (ImageView) findViewById(R.id.add_qty);
		imgRemove = (ImageView) findViewById(R.id.remove_qty);
        //btnShare = (Button) findViewById(R.id.btnShare);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
		inStock = (TextView) findViewById(R.id.txtInStock);
		outStock = (TextView) findViewById(R.id.txtOutStock);
		inStock.setVisibility(View.GONE);
		outStock.setVisibility(View.GONE);
		lytCart = (LinearLayout) findViewById(R.id.lytCart);
		btnBuyNow = (Button) findViewById(R.id.btnBuyNow);
		btnChooseOption = (Button) findViewById(R.id.btnChooseOption);
		lytOptionLayout = (LinearLayout) findViewById(R.id.optionLayout);


		webSlider = (WebView) findViewById(R.id.txtSlider);


		btnChooseOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				inputDialog2();
			}
		});


		in_stock = 0;

        // get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	//	int wPix = dm.widthPixels;
	//	wPix = wPix - 120;
	//	int hPix = wPix;
        int hPix = dm.heightPixels;
		hPix = hPix / 2;
        int wPix = dm.widthPixels;
		wPix = wPix / 2;
		
		// change menu image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
		lp.leftMargin = dm.widthPixels / 2;
		lp.leftMargin = lp.leftMargin / 2;

        imgPreview.setLayoutParams(lp);
        
        imageLoader = new ImageLoader(ActivityMenuDetail.this);
        dbhelper = new DBHelper(this);
		
		// get menu id that sent from previous page
        Intent iGet = getIntent();
        Menu_ID = iGet.getLongExtra("menu_id", 0);
        selected_Menu_ID = Menu_ID;


		Category_ID = iGet.getLongExtra("category_id",10);
		Category_name = iGet.getStringExtra("category_name");
		Keyword = iGet.getStringExtra("keyword");
		qty = iGet.getStringExtra("qty");


		this.webSlider.setVisibility(0);
		this.webSlider.getSettings().setJavaScriptEnabled(true);
		this.webSlider.loadUrl(Constant.MenuSliderAPI + "?accesskey=" + Constant.AccessKey + "&id=" + this.Menu_ID);
		this.webSlider.setVerticalScrollBarEnabled(false);
		this.webSlider.setHorizontalScrollBarEnabled(false);
		this.webSlider.loadUrl(Constant.MenuSliderAPI + "?accesskey=" + Constant.AccessKey + "&id=" + this.Menu_ID);


		// Menu detail API url
        MenuDetailAPI = Constant.MenuDetailAPI+"?accesskey="+Constant.AccessKey+"&menu_id="+Menu_ID;
        
        // call asynctask class to request data from server
        new getDataTask().execute();

		txtQty.setText(qty.toString());

		txtQty.setCursorVisible(false);
		txtQty.setLongClickable(false);
		txtQty.setFocusable(false);
		txtQty.setSelected(false);
		txtQty.setKeyListener(null);
		txtQty.setBackgroundResource(android.R.color.transparent);

        
        // event listener to handle add button when clicked
        btnAdd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// show input dialog
			//	inputDialog();
				//startActivity(new Intent(ActivityMenuDetail.this, ActivityEnquiry.class));
				//overridePendingTransition(R.anim.open_next, R.anim.close_next);
			//	Intent iDetail = new Intent(ActivityMenuDetail.this, ActivityEnquiry.class);
			//	iDetail.putExtra("package", Menu_name);
			//	startActivity(iDetail);
			//	overridePendingTransition(R.anim.open_next, R.anim.close_next);
			//	Intent iMyOrder = new Intent(ActivityMenuDetail.this, ActivityCart.class);
			//	startActivity(iMyOrder);
				Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
				invalidateOptionsMenu();
			}
		});


		btnBuyNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				/*try{
					dbhelper.openDataBase();
				}catch(SQLException sqle){
					throw sqle;
				}


				String temp = txtQty.getText().toString();
				int quantity = 0;

				// when add button clicked add menu to order table in database
				if(!temp.equalsIgnoreCase("")){
					quantity = Integer.parseInt(temp);
					if(quantity==0) {
						quantity = 1;
					}

					txtQty.setText(String.valueOf(quantity));
					if(dbhelper.isDataExist(selected_Menu_ID)){
						if(quantity==0) {
							dbhelper.deleteData(selected_Menu_ID);
						} else {
							dbhelper.updateData(selected_Menu_ID, quantity, (Selected_Menu_price*quantity));
						}
						//   Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
					}else{
						dbhelper.addData(selected_Menu_ID, Selected_Menu_name, quantity, (Selected_Menu_price*quantity));
						// Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
					}
					Intent iReservation = new Intent(ActivityMenuDetail.this, ActivityCheckoutAsk.class);
					startActivity(iReservation);
					overridePendingTransition(R.anim.open_next, R.anim.close_next);

				}else{
					// dialog.cancel();
				}

				invalidateOptionsMenu();*/


					Intent iDetail = new Intent(ActivityMenuDetail.this, ActivityEnquiry.class);
					iDetail.putExtra("package", Menu_name);
					iDetail.putExtra("price", String.valueOf(Menu_price));
					startActivity(iDetail);
					overridePendingTransition(R.anim.open_next, R.anim.close_next);

			}
		});


        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    dbhelper.openDataBase();
                }catch(SQLException sqle){
                    throw sqle;
                }


                String temp = txtQty.getText().toString();
                        int quantity = 0;

                        // when add button clicked add menu to order table in database
                        if(!temp.equalsIgnoreCase("")){
                            quantity = Integer.parseInt(temp);
                            quantity++;
                            txtQty.setText(String.valueOf(quantity));
                            if(dbhelper.isDataExist(selected_Menu_ID)){
								if(quantity==0) {
									dbhelper.deleteData(selected_Menu_ID);
								} else {
									dbhelper.updateData(selected_Menu_ID, quantity, (Selected_Menu_price*quantity));
								}
                             //   Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            }else{
                                dbhelper.addData(selected_Menu_ID, Selected_Menu_name, quantity, (Selected_Menu_price*quantity));
                               // Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            }
                        }else{
                           // dialog.cancel();
                        }

				invalidateOptionsMenu();


            }
        });

       imgRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                    try{
                    dbhelper.openDataBase();
                    }catch(SQLException sqle){
                    throw sqle;
                    }


                    String temp = txtQty.getText().toString();
                    int quantity = 0;

        // when add button clicked add menu to order table in database
                    if(!temp.equalsIgnoreCase("")){
                          quantity = Integer.parseInt(temp);
                          if(quantity!=0) {
                                     quantity--;
                             }

                    txtQty.setText(String.valueOf(quantity));
                    if(dbhelper.isDataExist(selected_Menu_ID)){
						if(quantity==1) {
							dbhelper.deleteData(selected_Menu_ID);
						} else {
							dbhelper.updateData(selected_Menu_ID, quantity, (Selected_Menu_price*quantity));
						}

                 //   Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                    }else{
                    dbhelper.addData(selected_Menu_ID, Selected_Menu_name, quantity, (Selected_Menu_price*quantity));
                   // Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                    }
                    }else{
                    // dialog.cancel();
                    }
				invalidateOptionsMenu();
                    }
       });

        
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detail, menu);

	/*	tv = new TextView(this);
		tv.setText("999");
		tv.setTextColor(Color.parseColor("#ffffff"));

		tv.setPadding(3, 3, 3, 3);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0,0,10,0);
		tv.setLayoutParams(params);


		tv.setTypeface(null, Typeface.BOLD);
		tv.setTextSize(18);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
		tv.setGravity(Gravity.CENTER);

		menu.add(Menu.NONE, 122, 400, "C").setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		update_cart();*/
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
			Intent iMyOrder = new Intent(ActivityMenuDetail.this, ActivityCart.class);
			startActivity(iMyOrder);
			overridePendingTransition (R.anim.open_next, R.anim.close_next);
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
			dbhelper.close();
			Intent iDetail = new Intent(ActivityMenuDetail.this, ActivityMenuBulk.class);
			iDetail.putExtra("category_id",Category_ID);
			iDetail.putExtra("keyword",Keyword);
			iDetail.putExtra("category_name",Category_name);
			startActivity(iDetail);

			finish();
			overridePendingTransition(R.anim.open_main, R.anim.close_next);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	void inputDialog2(){



		AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMenuDetail.this);
		builder.setTitle("Choose Options");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection

				final int id = item;
				/*try{
					dbhelper.openDataBase();
				}catch(SQLException sqle){
					throw sqle;
				}*/

				txtSelected.setText(sz[id]+" : Rs. "+pr[id]);
				selected_Menu_ID = Menu_ID+Long.parseLong(String.valueOf(id));
				Selected_Menu_name = Menu_name + " - " + sz[id];
				Selected_Menu_price = Long.parseLong(pr[id]);



			/*	AlertDialog.Builder alert2 = new AlertDialog.Builder(ActivityMenuDetail.this);

				alert2.setTitle(R.string.order);
				alert2.setMessage(R.string.number_order);
				alert2.setCancelable(false);
				final EditText edtQuantity = new EditText(ActivityMenuDetail.this);
				int lfcLength = 3;
				edtQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(lfcLength)});
				edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
				alert2.setView(edtQuantity);

				alert2.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String temp = edtQuantity.getText().toString();
						int quantity = 0;

						// when add button clicked add menu to order table in database
						if(!temp.equalsIgnoreCase("")){
							quantity = Integer.parseInt(temp);
							if(dbhelper.isDataExist(Menu_ID+Long.parseLong(String.valueOf(id)))){
								dbhelper.updateData(Menu_ID+Long.parseLong(String.valueOf(id)), quantity, (Long.parseLong(pr[id])*quantity));
								Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
							}else{
								dbhelper.addData(Menu_ID+Long.parseLong(String.valueOf(id)), Menu_name + " - " + sz[id] , quantity, (Long.parseLong(pr[id])*quantity));
								Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
							}
						}else{
							dialog.cancel();
						}
					}
				});

				alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						// when cancel button clicked close dialog
						dialog.cancel();
					}
				});

				alert2.show();
*/

			}
		});
		AlertDialog alert = builder.create();
		alert.show();





	}
    
    // method to show number of order form
    void inputDialog(){
    	
    	// open database first
		// open database first
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.order);
		alert.setMessage(R.string.number_order);
		alert.setCancelable(false);
		final EditText edtQuantity = new EditText(this);
		int naikLength = 3;
		edtQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(naikLength)});
		edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(edtQuantity);

		alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String temp = edtQuantity.getText().toString();
				int quantity = 0;

				// when add button clicked add menu to order table in database
				if(!temp.equalsIgnoreCase("")){
					quantity = Integer.parseInt(temp);
					if(dbhelper.isDataExist(Menu_ID)){
						dbhelper.updateData(Menu_ID, quantity, (Menu_price*quantity));
				//		Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
					}else{
						dbhelper.addData(Menu_ID, Menu_name, quantity, (Menu_price*quantity));

					}
				}else{
					dialog.cancel();
				}
			}
		});



		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				// when cancel button clicked close dialog
				dialog.cancel();
			}
		});

		alert.show();


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
			// if internet connection and data available show data
			// otherwise, show alert text
			if((Menu_name != null) && IOConnect == 0){
				sclDetail.setVisibility(0);
			
				imageLoader.DisplayImage(Constant.AdminPageURL+Menu_image, imgPreview);
				
				txtText.setText(Menu_name);

                if(Menu_maxPrice > Menu_price) {

                    String str="<font color='#A80000'>" + Currency + " "+ Menu_maxPrice+"</font>";

                    txtSubTextnaik.setText(Html.fromHtml(str));

                    txtSubTextnaik.setPaintFlags(txtSubTextnaik.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    float per = 0;
                    float naik_price = Float.parseFloat(String.valueOf(Menu_maxPrice));
                    float price = Float.parseFloat(String.valueOf(Menu_price));
                    float disc = naik_price - price;


                    try {

                        per = disc / naik_price * 100;
                    } catch (Exception ex) {

                    }

                    if(per==100) {
                        per = 0;
                    }

                    if(per>0) {

                        txtSubText.setText(Html.fromHtml("" + Currency + Menu_price+" " + "<b>(" + String.format("%.0f", per) + "% OFF" + ")</b>"));


                    } else {
                        txtSubText.setText("" + Currency + Menu_price+" "+"\n");

                    }



                } else {
                    txtSubText.setText("" + Currency + Menu_price+" "+"\n");
                    txtSubTextnaik.setVisibility(View.GONE);
                }

				if(Menu_price==0) {
					txtSubText.setText("Enquire Now");
					txtSubTextnaik.setVisibility(View.GONE);
				}

				sz = Sizes.split(",");

				pr = Prices.split(",");

				options = new String[sz.length];
				String md = Menu_description;





				if(in_stock==0) {
					outStock.setVisibility(View.VISIBLE);
					inStock.setVisibility(View.GONE);
					lytCart.setVisibility(View.GONE);
				} else {
					outStock.setVisibility(View.GONE);
					inStock.setVisibility(View.VISIBLE);
					lytCart.setVisibility(View.VISIBLE);
				}

				inStock.setVisibility(View.GONE);
				outStock.setVisibility(View.GONE);
            //    txtSubText.setVisibility(View.INVISIBLE);
              //  txtDescription.loadDataWithBaseURL("", Menu_description, "text/html", "UTF-8", "");

		       // txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));


					Menu_description = "";

					if(Sizes.length() > 0) {

						txtSelected.setText(sz[0]+" : Rs. "+pr[0]);

					

						selected_Menu_ID = Menu_ID+Long.parseLong(String.valueOf(0));
						Selected_Menu_name = Menu_name + " - " + sz[0];
						Selected_Menu_price = Long.parseLong(pr[0]);

						Menu_description = "<table style='width: 100%;'>";
						Menu_description += "<tr><th style='text-align:left;'>Options</th><td style='text-align:right;'>Price</td></tr>";
						Menu_description += "<tr><td colspan='2' style='text-align:left;'><hr /></td></tr>";
						for (int i = 0; i < sz.length; i++) {
							Menu_description += "<tr>";
							Menu_description += "<th style='text-align:left;'>" + sz[i] + "</th><td style='text-align:right;'>Rs. " + pr[i] + "/-</td>";
							options[i] = sz[i] + " - Rs. " + pr[i];
							Menu_description += "</tr>";
						}
						Menu_description += "</table><hr />";

					}
				Menu_description += md;
				Menu_description += "<br /><br /><br />";
				txtDescription.loadDataWithBaseURL("", Menu_description, "text/html", "UTF-8", "");

				txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));


			}else{
				txtAlert.setVisibility(0);
			}
		}
    }

    // method to parse json data from server
    public void parseJSONData(){
    	
    	try {
    		// request data from menu detail API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        HttpUriRequest request = new HttpGet(MenuDetailAPI);
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
				
			for (int i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject menu = object.getJSONObject("Menu_detail");
			    
			    Menu_image = menu.getString("Menu_image");
			    Menu_name = menu.getString("Menu_name");
			    Selected_Menu_name = menu.getString("Menu_name");
			    Menu_price = Double.valueOf(formatData.format(menu.getDouble("Price")));
			    Selected_Menu_price = Menu_price;
                Menu_maxPrice = Double.valueOf(formatData.format(menu.getDouble("MaxPrice")));
			    Menu_serve = menu.getString("Serve_for");
			    Menu_description = Constant.DefaultDetails+"<br /><br />"+menu.getString("Description");
			    Menu_quantity = menu.getInt("Quantity");
                Sizes = menu.getString("size_heading");
                Prices = menu.getString("size_price");

				in_stock = menu.getInt("is_stock");

				if(Sizes.length()==0) {
					lytOptionLayout.setVisibility(View.GONE);
				} else {
					lytOptionLayout.setVisibility(View.VISIBLE);

				}
			//	in_stock = 1;
				    
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

	
    // close database before back to previous page
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    //	super.onBackPressed();
    	dbhelper.close();
	/*	Intent iDetail = new Intent(ActivityMenuDetail.this, ActivityMenuBulk.class);
		iDetail.putExtra("category_id",Category_ID);
		iDetail.putExtra("keyword",Keyword);
		iDetail.putExtra("category_name",Category_name);
		startActivity(iDetail);
		*/
		finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
	

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//imageLoader.clearCache();
    	super.onDestroy();
    }
	 
    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}
    
    
}
