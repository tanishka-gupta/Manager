package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityDirectoryDetail extends Activity implements SimpleGestureFilter.SimpleGestureListener {
	
	ImageView imgPreview;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;
	TextView txtText, txtSubText;
	WebView txtDescription, webSlider;
	ImageButton btnAdd;
	ScrollView sclDetail;
	ProgressBar prgLoading;
	TextView txtAlert;
    Button btnPrevious, btnNext, btnCallNow, btnSendEnquiry, btnView;
    private SimpleGestureFilter detector;
    LinearLayout detailLayout;
    float x1,x2;
    float y1, y2;
    ActionBar bar;
	
	// declare dbhelper object
	static DBHelper dbhelper;
	
	// declare ImageLoader object
	ImageLoader imageLoader;
	
	// declare variables to store menu data
	String Resume_Name, Shop_number, Name_owner, Name_tenant, Name_shop, Resume_Image, Phone_owner, Phone_tenant, Phone_shop, shop_size, meter_no, electric_load;
    String email, whatsapp, yearly_fee, maintain_fee, shop_status, paid_amount, balance_amount, prev_fee;
	Integer Resume_ID;
	String MenuDetailAPI;
	int IOConnect = 0;
    static ArrayList<Integer> Resume_ID_List = new ArrayList<Integer>();



    // create price format
	DecimalFormat formatData = new DecimalFormat("#.##");
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_detail);

        detector = new SimpleGestureFilter(this,this);



        bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Faculty Details");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtText = (TextView) findViewById(R.id.txtText);
        txtSubText = (TextView) findViewById(R.id.txtSubText);
        txtDescription = (WebView) findViewById(R.id.txtDescription);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.GONE);
        //btnShare = (Button) findViewById(R.id.btnShare);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        btnNext = (Button) findViewById(R.id.btnNextJob);
        btnNext.setVisibility(View.GONE);
        btnPrevious = (Button) findViewById(R.id.btnPrevJob);
        btnPrevious.setVisibility(View.GONE);
        btnCallNow = (Button) findViewById(R.id.btnCallNow);
        btnView = (Button) findViewById(R.id.btnView);

        webSlider = (WebView) findViewById(R.id.webAd);




        //btnSendEnquiry = (Button) findViewById(R.id.btnSendEnquiry);

        detailLayout = (LinearLayout) findViewById(R.id.detailLayout);
        detailLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
            //    Toast.makeText(ActivityMenuDetail.this,"touched",Toast.LENGTH_LONG).show();


               // Toast.makeText()
                return false;
            }
        });

        btnCallNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDirectoryDetail.this);
                builder.setMessage("Have you selected this candidate for job?").setTitle("Remove CV?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                      //  Toast.makeText(ActivityMenuDetail.this, "Selected", Toast.LENGTH_LONG).show();

                        // go to menu detail page
                        Intent iDetail = new Intent(ActivityDirectoryDetail.this, ActivityEnquiry.class);
                        iDetail.putExtra("resume_id", Resume_ID);

                        prefs = ActivityDirectoryDetail.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        final String company_id = prefs.getString("user_id",null);
                     //   Toast.makeText(ActivityMenuDetail.this,company_id,Toast.LENGTH_LONG).show();


                        iDetail.putExtra("company_id",company_id);
                        startActivity(iDetail);
                        overridePendingTransition(R.anim.open_next, R.anim.close_next);

                        dialog.dismiss();
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Code that is executed when clicking YES


                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDirectoryDetail.this);
                        builder.setMessage("Please select this candidate then send remove request").setTitle("Remove CV?")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                });


                builder.show();

            }
        });



        // get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix;
    //    int hPix = dm.heightPixels;
      //  int wPix = hPix / 2 + 50;
		
		// change menu image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
        imgPreview.setLayoutParams(lp);
        
        imageLoader = new ImageLoader(ActivityDirectoryDetail.this);
        dbhelper = new DBHelper(this);
		
		// get menu id that sent from previous page
        Intent iGet = getIntent();
        Resume_ID = iGet.getIntExtra("resume_id", 403);
        Resume_ID_List = iGet.getIntegerArrayListExtra("resume_array");
        
        // Menu detail API url
        MenuDetailAPI = Constant.DirectoryDetailAPI+"?accesskey="+Constant.AccessKey+"&resume_id="+Resume_ID;

     //   Toast.makeText(ActivityMenuDetail.this,MenuDetailAPI,Toast.LENGTH_LONG).show();
        // call asynctask class to request data from server
        new getDataTask().execute();      
        
        // event listener to handle add button when clicked
        btnAdd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// show input dialog
				inputDialog();
			}
		});



        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                int current = Resume_ID_List.indexOf(Resume_ID);

                try {
                    Intent iDetail = new Intent(ActivityDirectoryDetail.this, ActivityDirectoryDetail.class);
                    iDetail.putExtra("resume_id", Resume_ID_List.get(current + 1));
                    iDetail.putIntegerArrayListExtra("resume_array", Resume_ID_List);
                    startActivity(iDetail);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                } catch(Exception ex) {
                    Toast.makeText(ActivityDirectoryDetail.this,"No More Data", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                int current = Resume_ID_List.indexOf(Resume_ID);

                try {

                    Intent iDetail = new Intent(ActivityDirectoryDetail.this, ActivityDirectoryDetail.class);

                    iDetail.putExtra("resume_id", Resume_ID_List.get(current - 1));

                    iDetail.putIntegerArrayListExtra("resume_array", Resume_ID_List);
                    startActivity(iDetail);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                     finish();
                    } catch(Exception ex) {
                        Toast.makeText(ActivityDirectoryDetail.this,"No More Data", Toast.LENGTH_LONG).show();
                    }

            }
        });
        
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT :

                //str = "Swipe Right";
                try {

                    int current = Resume_ID_List.indexOf(Resume_ID);

                    Intent iDetail = new Intent(ActivityDirectoryDetail.this, ActivityDirectoryDetail.class);


                    iDetail.putExtra("resume_id", Resume_ID_List.get(current - 1));

                    iDetail.putIntegerArrayListExtra("resume_array", Resume_ID_List);
                    startActivity(iDetail);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                    finish();
                } catch(Exception ex) {
                    Toast.makeText(ActivityDirectoryDetail.this,"No More Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case SimpleGestureFilter.SWIPE_LEFT :

                //str = "Swipe Left";
                int current = Resume_ID_List.indexOf(Resume_ID);

                try {
                    Intent iDetail = new Intent(ActivityDirectoryDetail.this, ActivityDirectoryList.class);
                    iDetail.putExtra("resume_id", Resume_ID_List.get(current + 1));
                    iDetail.putIntegerArrayListExtra("resume_array", Resume_ID_List);
                    startActivity(iDetail);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                } catch(Exception ex) {
                    Toast.makeText(ActivityDirectoryDetail.this,"No More Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case SimpleGestureFilter.SWIPE_DOWN :

                //str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :

                //str = "Swipe Up";
                break;

        }
       // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

       // Toast.makeText(this,"Touched",Toast.LENGTH_LONG).show();
        return super.onTouchEvent(event);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.menu_detail, menu);
		return true;
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
    
    // method to show number of order form
    void inputDialog(){
    	
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
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(edtQuantity);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();
                int quantity = 0;

                // when add button clicked add menu to order table in database

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
			if((Resume_Name != null) && IOConnect == 0){
				sclDetail.setVisibility(0);
			

				txtText.setText(Resume_Name);
                bar.setTitle(Resume_Name);
			//	txtSubText.setText("Salary Rs. " + Job_Salary+" "+"\n");
                txtSubText.setText("");
                txtText.setVisibility(View.GONE);
                txtSubText.setVisibility(View.GONE);


             /*   String details="<table style='width: 100%; '>" +
                        "<tr><td style='text-transform:uppercase; padding:5px; font-weight: bold font-size: 18px;' valign='center'><img src='"+Constant.AdminPageURL + image + "' style='width: 100px; margin-bottom:20px ' /><br /><b>"+Resume_Name+"</b></td><td></td></tr>" +

                        "<tr><td  valign='top' style='padding:5px'><b>Qualification</b>: <br />"+Resume_Qualification+"<br /></td></tr>" +
                        "<tr><td valign='top' style='padding:5px'><strong>Experience:</strong><br />"; */


                String image="";
                if(Resume_Image.equals("") || Resume_Image.isEmpty() || Resume_Image==null) {
                    image="avatar.jpg";
                } else {
                    image=Resume_Image;
                }


                String details="<head><style type='text/css'> @font-face {\n" +
                        "    font-family: MyFont;\n" +
                        "    src: url(\"file:///android_asset/fonts/ave.ttf\")\n" +
                        "}* { font-family:MyFont; } tr:nth-child(even) {background: #ffebd4}\n" +
                        "tr:nth-child(odd) {background: #FFF} table { border: solid 1px #cacaca; } td { border: none; padding: 2px; }</style></head><table cellspacing='0' cellpadding='0' style='width: 100%; table-layout: fixed;'>" +"<tr><td colspan='2'><img src='"+Constant.AdminPageURL + image + "' style='width: 100%; float:left;' /></td></tr>";



                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Name:</strong></td><td style='word-wrap: break-word'>"+Name_shop+"</td></tr>";
                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Education:</strong></td><td style='word-wrap: break-word'>"+Name_owner+"</td></tr>";
                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Subject:</strong></td><td style='word-wrap: break-word'>"+Name_tenant+"</td></tr>";
                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Experience:</strong></td><td style='word-wrap: break-word'>"+meter_no+"</td></tr>";
                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Mobile Number:</strong></td><td style='word-wrap: break-word'>"+Phone_tenant+"</td></tr>";
                details += "<tr><td  valign='top' style='padding:5px; word-wrap: break-word' ><strong>Classes:</strong></td><td style='word-wrap: break-word'>"+email+"</td></tr>";











                 details+= "" +
                    "</table>\n";
               txtDescription.loadDataWithBaseURL("", details, "text/html", "UTF-8", "");


                txtDescription.setWebViewClient(new WebViewClient() {

                    // Make sure any url clicked is opened in webview
                    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // Toast.makeText(MainActivity.this,url,Toast.LENGTH_LONG).show();
                         if (url.startsWith("rel:")) {
                            try {



                            } catch (Exception e) {
                                // error
                            }

                            return true;
                        }






                        // Return true to override url loading (In this case do
                        // nothing).
                        return super.shouldOverrideUrlLoading(view, url);
                    }


                    @Override
                    public void onLoadResource(WebView view, String url) {
                        super.onLoadResource(view, url);




                    }





                    @Override
                    public void onPageFinished(WebView view, String url) {











                    }

                    // handeling arrors
                    @Override
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        //AlertDialog.Builder builder = new AlertDialog.Builder(
                        //			MainActivity.this);
                        //	builder.setMessage(description)
                        //			.setPositiveButton(getText(R.string.ok), null)
                        //			.setTitle("Whoops");
                        //	builder.show();
                        // return false;
                        // startActivity(new Intent(MainActivity.this,ActivityNotFound.class));
                    }

                });



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
			    
			    JSONObject menu = object.getJSONObject("Job_detail");

			    Resume_Name = menu.getString("name_shop");
                Shop_number = menu.getString("shop_number");

                Name_owner = menu.getString("name_owner");
                Name_tenant = menu.getString("name_tenant");
                Name_shop = menu.getString("name_shop");
                Phone_owner = menu.getString("phone_owner");
                Phone_tenant = menu.getString("phone_tenant");
                Phone_shop = menu.getString("phone_shop");
                shop_size = menu.getString("size");
                meter_no = menu.getString("meter_no");
                electric_load = menu.getString("electric_load");
                email = menu.getString("email");
                whatsapp = menu.getString("whatsapp");
                yearly_fee = menu.getString("yearly_fee");
                Resume_Image = menu.getString("image");

                maintain_fee = menu.getString("maintain_fee");
                prev_fee = menu.getString("prev_fee");
                paid_amount = menu.getString("paid_amount");
                balance_amount = menu.getString("balance_amount");

                shop_status = menu.getString("is_verified");

                if(shop_status.equals("0")) {
                        shop_status = "Lifetime";
                } else if(shop_status.equals("1")) {
                    shop_status = "Yearly";
                } else  {
                    shop_status = "";
                }






				    
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
    	super.onBackPressed();
    	dbhelper.close();
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
