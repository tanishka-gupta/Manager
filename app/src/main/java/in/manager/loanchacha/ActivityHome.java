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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.slider.library.SliderLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityHome extends Fragment implements OnItemClickListener {
    GridView gridview;
    AdapaterGridView gridviewAdapter;
    ArrayList<GridViewItem> data = new ArrayList<GridViewItem>();
    ExpandableHeightGridView listCategory, listNewCategory;
    ProgressBar prgLoading;
    TextView txtAlert, txtHeading, txtThought, txtNoticeHeading, txtNotice, txtUser;
    static int level=1;
    WebView webSlider;
    ImageButton btnSearch;
    EditText edtKeyword;
    String MenuAPI;
    ExpandableHeightGridView listMenu;
    ImageView imgMap;
    String user_type, user_id;
    static String Currency = "Rs. ";

    DecimalFormat formatData = new DecimalFormat("#.##");

    // create arraylist variables to store data from server
    static ArrayList<Long> Menu_ID = new ArrayList<Long>();
    static ArrayList<String> Menu_name = new ArrayList<String>();
    static ArrayList<String> Menu_quantity = new ArrayList<String>();
    static ArrayList<Double> Menu_price = new ArrayList<Double>();
    static ArrayList<Double> Menu_max_price = new ArrayList<Double>();
    static ArrayList<String> Menu_image = new ArrayList<String>();

    AdapterHomeMenuList mla;


    String name, phone, password;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;


    // declare adapter object to create custom category list
    AdapterHomeCategoryList cla;
    AdapterHomeNewCategoryList new_cla;

    // create arraylist variables to store data from server
    static ArrayList<Long> Category_ID = new ArrayList<Long>();
    static ArrayList<String> Category_name = new ArrayList<String>();
    static ArrayList<String> Category_image = new ArrayList<String>();
    static ArrayList<String> IsLeaf = new ArrayList<String>();


    static ArrayList<Long> New_Category_ID = new ArrayList<Long>();
    static ArrayList<String> New_Category_name = new ArrayList<String>();
    static ArrayList<String> New_Category_image = new ArrayList<String>();
    static ArrayList<String> New_IsLeaf = new ArrayList<String>();


    private SliderLayout sliderLayout;

    String Keyword;
    String CategoryAPI;
    int IOConnect = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container,false);

        super.onCreate(savedInstanceState);

        if (!Constant.isNetworkAvailable(getActivity())) {
      //      Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ActivityNoInternet.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            //  Toast.makeText(MainActivity.this,"P:"+Constant.Payment, Toast.LENGTH_SHORT).show();
        }


        btnSearch = (ImageButton) v.findViewById(R.id.btnSearch);
        edtKeyword = (EditText) v.findViewById(R.id.edtKeyword);
        txtHeading = (TextView) v.findViewById(R.id.txtHeading);
        txtThought = (TextView) v.findViewById(R.id.thought);
        txtNotice = (TextView) v.findViewById(R.id.txtNotice);
        txtNoticeHeading = (TextView) v.findViewById(R.id.notice_heading);
        listMenu = (ExpandableHeightGridView) v.findViewById(R.id.listMenu);

        txtUser = (TextView) v.findViewById(R.id.txtUser);


        imgMap = (ImageView) v.findViewById(R.id.mapimg);

        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Locate)));

            }
        });


        MenuAPI = Constant.MenuAPI+"?accesskey="+Constant.AccessKey+"&category_id=-55";

       // Toast.makeText(getActivity(),MenuAPI,Toast.LENGTH_LONG).show();
        // tax and currency API url











        //	Toast.makeText(ActivityMenuList.this,MenuAPI,Toast.LENGTH_LONG).show();

        // set category name to textview
//        txtTitle.setText(Category_name);

        mla = new AdapterHomeMenuList(getActivity());

        new getDataTaskProducts().execute();
        new getDataTaskCategory().execute();


        listMenu.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu detail page
                Intent iDetail = new Intent(getActivity(), ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", Menu_ID.get(position));
                iDetail.putExtra("qty", Menu_quantity.get(position));

                startActivity(iDetail);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });






        txtHeading.setText(Constant.ThoughtHeading);
        //txtHeading.setText(Html.fromHtml(Constant.Welcome));
        txtThought.setText(Constant.ThoughtHeading+"\n\n"+ Html.fromHtml(Constant.Thought));
        txtNoticeHeading.setText(Constant.NoticeHeading);
        txtNotice.setText(Constant.Notice);


        //   v.setContentView(R.layout.category_list);

        prefs = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        phone = prefs.getString("Phone",null);
        password = prefs.getString("Password", null);
        name = prefs.getString("Name","User");
        user_type = prefs.getString("user_type","");
        user_id = prefs.getString("user_id","");

        if(name !=null || (name.equals("user") == false ) ) {
            txtUser.setText("Welcome "+ name + "!" + " - "+user_type);
        }


        webSlider = (WebView) v.findViewById(R.id.txtSlider);
        webSlider.setVisibility(View.VISIBLE);
        webSlider.getSettings().setJavaScriptEnabled(true);
        webSlider.loadUrl(Constant.PartnerSliderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
        webSlider.setVerticalScrollBarEnabled(false);
        webSlider.setHorizontalScrollBarEnabled(false);

        webSlider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return (motionEvent.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        webSlider.setWebViewClient(new WebViewClient() {
            // Make sure any url clicked is opened in webview
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ((url.contains("market://")
                        || url.contains("play.google.com")
                        || url.contains("plus.google.com")
                        || url.contains("mailto:")
                        || url.contains("tel:")
                        || url.contains("vid:")
                        || url.contains("geo:")
                        || url.contains("sms:")) == true) {
                    // Load new URL Don't override URL Link
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                }  else if(url.equals("http://nutricart.mlmappz.com/payu/success.php")) {
                    //   Intent i = new Intent(ActivityPayu.this, ActivityConfirmMessage.class);
                    //   view.getContext().startActivity(i);
                    //   overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    return true;

                }else   if (url.endsWith(".mp4") || url.endsWith(".avi")
                        || url.endsWith(".flv")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "video/mp4");
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        // error
                    }

                    return true;
                } else if (url.endsWith(".mp3") || url.endsWith(".wav")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "audio/mp3");
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        // error
                    }

                    return true;
                } else if(url.startsWith("orderdetails")) {
                    Intent i = new Intent(getActivity(), ActivityPayu.class);
                    String ar[] = url.split(":");
                    i.putExtra("URL",Constant.OrderDetailAPI+"?accesskey="+Constant.AccessKey+"&order_id="+ar[1]+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","Order Details");
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;






                } else if(url.startsWith("freelancerprofile")) {

                   /* Intent i = new Intent(getActivity(), ActivityPayu.class);
                    String ar[] = url.split(":");
                    i.putExtra("URL",Constant.ViewOrderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id+"&status="+ar[1]);
                    //i.putExtra("URL",Constant.OrderDetailAPI+"?accesskey="+Constant.AccessKey+"&order_id="+ar[1]+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","Leads");
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);*/
                    //finish();

                    Intent i = new Intent(getActivity(), ActivityPayu.class);
                    //String ar[] = url.split(":");
                    i.putExtra("URL",Constant.MyProfileAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","My Profile");
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);

                    return  true;
                } else if(url.startsWith("leads")) {
                    Intent i = new Intent(getActivity(), ActivityPayu.class);
                    String ar[] = url.split(":");
                    i.putExtra("URL",Constant.ViewOrderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id+"&status="+ar[1]);
                    //i.putExtra("URL",Constant.OrderDetailAPI+"?accesskey="+Constant.AccessKey+"&order_id="+ar[1]+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","Leads");
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                } else if(url.startsWith("newlead")) {





                    //getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);

                    Intent i = new Intent(getActivity(), ActivityPayu.class);


                    i.putExtra("URL",Constant.AddLeadsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    // Toast.makeText(getActivity(),Constant.AddLeadsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id,Toast.LENGTH_LONG).show();
                    i.putExtra("title","Add Leads");
                    startActivity(i);

                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                } else if(url.startsWith("alllead")) {

                    Intent i = new Intent(getActivity(), ActivityPayu.class);

                    i.putExtra("URL",Constant.ViewOrderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","View Orders");

                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                }
                else if(url.startsWith("notifications")) {

                    Intent i = new Intent(getActivity(), ActivityPayu.class);

                    i.putExtra("URL",Constant.NotificationsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","Notifications");

                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                }
                else if(url.startsWith("freelancers:all")) {

                    Intent i = new Intent(getActivity(), ActivityPayu.class);

                    i.putExtra("URL",Constant.AllFreelancersAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","All Freelancers");

                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                }
                else if(url.startsWith("newfreelancer")) {

                    Intent i = new Intent(getActivity(), ActivityPayu.class);

                    i.putExtra("URL",Constant.AddFreelancerAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                    i.putExtra("title","Add Freelancer");

                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    //finish();

                    return  true;
                }



                // Return true to override url loading (In this case do
                // nothing).
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // hide loading image
               /* Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        // hide splash image
                       *//* if (findViewById(R.id.imageLoading1).getVisibility() == View.VISIBLE){
                            findViewById(R.id.imageLoading1).setVisibility(
                                    View.GONE);
                            // show webview
                            findViewById(R.id.webkit).setVisibility(View.VISIBLE);
                        }*//*
                    }
                    // set a delay before splashscreen is hidden
                }, 0);*/
            }

            // handeling arrors
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(
                        ActivityPayu.this);
                builder.setMessage(description)
                        .setPositiveButton("Ok", null)
                        .setTitle("Whoops");
                builder.show();*/
            }

        });

       // webSlider.setVisibility(View.GONE);
        ActivityCompat.requestPermissions(ActivityHome.this.getActivity(),
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        /*ActivityCompat.requestPermissions(ActivityHome.this.getActivity(),
                new String[]{Manifest.permission.CALL_PHONE},
                55);*/












        ActionBar bar = getActivity().getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle("Hi "+ name + "");



        prgLoading = (ProgressBar) v.findViewById(R.id.prgLoading);
        listCategory = (ExpandableHeightGridView) v.findViewById(R.id.listCategory);
        listNewCategory = (ExpandableHeightGridView) v.findViewById(R.id.listNewCategory);
        txtAlert = (TextView) v.findViewById(R.id.txtAlert);

        cla = new AdapterHomeCategoryList(getActivity());
        new_cla = new AdapterHomeNewCategoryList(getActivity());

        // category API url
        CategoryAPI = Constant.CategoryAPI+"?accesskey="+Constant.AccessKey+"&category_id=-55";





        //anim
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.grid_item_anim);
        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
        //mGrid.setLayoutAnimation(controller);
        listCategory.setLayoutAnimation(controller);
        listNewCategory.setLayoutAnimation(controller);




     //   Toast.makeText(getActivity(),CategoryAPI,Toast.LENGTH_LONG).show();
        // call asynctask class to request data from server
     //   new getDataTask().execute();

  //      clearData();
//

        clearData();
        clearDataCategory();


        Category_ID.add(0L);
        Category_name.add("Videography");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("About Us");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("Contact Us");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("Contact Us");
        Category_image.add("");
        IsLeaf.add("0");





        // setGridViewHeightBasedOnChildren(listCategory,6);

        prgLoading.setVisibility(8);

        // if internet connection and data available show data on list
        // otherwise, show alert text
        //   if((Category_ID.size() > 0) && (IOConnect == 0)){
        listCategory.setVisibility(View.GONE);
        listCategory.setAdapter(cla);
        listCategory.setExpanded(true);

      //  listCategory.setNumColumns(6);
       // setGridViewHeightBasedOnChildren(listCategory,6);

        // event listener to handle list when clicked

        listCategory.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu page

                //  Toast.makeText(getActivity(),"Leaf: "+IsLeaf.get(position).toString(),Toast.LENGTH_LONG).show();
               if(position==0) {

                   /*  startActivity(new Intent(getActivity(), ActivityAddLead.class));
                     getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);*/

                  /* prefs = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                   phone = prefs.getString("Phone",null);


                   String url = "http://loanapp.uddanpromotions.com/contact/?service_id=168&user_mobile="+phone;

                   //   Toast.makeText(ActivityCheckout.this,url,Toast.LENGTH_LONG).show();
                   Intent i = new Intent(getActivity(), ActivityServiceForm.class);
                   i.putExtra("URL",url);
                   startActivity(i);
                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);*/

                   Intent i = new Intent(getActivity(), ActivityPayu.class);

                   i.putExtra("URL",Constant.AddLeadsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);

                  // Toast.makeText(getActivity(),Constant.AddLeadsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id,Toast.LENGTH_LONG).show();
                   i.putExtra("title","Add Leads");
                   startActivity(i);
                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);






                } else if(position==1) {

                    // startActivity(new Intent(getActivity(), ActivityDirectoryList.class));
                    // getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);


                   Intent i = new Intent(getActivity(), ActivityPayu.class);

                   i.putExtra("URL",Constant.AllLeadsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                   i.putExtra("title","All Leads");
                   startActivity(i);
                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                   //getActivity().finish();

                   //startActivity(new Intent(getActivity(), ActivityLeadsList.class));
                   //getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);

                   /*try {

                       final CharSequence[] items = {
                               "Photo Gallery", "Video Gallery"
                       };

                       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                       builder.setTitle("Choose Gallery");
                       builder.setItems(items, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int item) {
                               // Do something with the selection
                               if(item==0) {

                                   Intent iMenuList = new Intent(getActivity(), ActivityGalleryCategoryList.class);
                                   startActivity(iMenuList);
                                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);

                               } else {

                                   Intent iMenuList = new Intent(getActivity(), ActivityVideoGalleryCategoryList.class);
                                   startActivity(iMenuList);
                                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);

                               }
                           }
                       });
                       AlertDialog alert = builder.create();
                       alert.show();


                   } catch (Exception ex) {
                       Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_LONG).show();
                   }*/





                } else if(position==2) {

                   Intent i = new Intent(getActivity(), ActivityPayu.class);

                   i.putExtra("URL",Constant.CreateOrderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                   i.putExtra("title","Create Order");
                   startActivity(i);
                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);


                } else if(position ==3) {

                    //customer care

                   Intent i = new Intent(getActivity(), ActivityPayu.class);

                   i.putExtra("URL",Constant.ViewOrderAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                   i.putExtra("title","View Orders");
                   startActivity(i);
                   getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);



                } else if(position==5) {
                    startActivity(new Intent(getActivity(), ActivityBlogCategoryList.class));
                    getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);


                }
            }
        });



        // event listener to handle list when clicked
        listNewCategory.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu page
                //  Toast.makeText(ActivityCategoryList.this, "Leaf: " + IsLeaf.get(position).toString(), Toast.LENGTH_LONG).show();

                try {
                    if (New_IsLeaf.get(position).equals("1")) {
                        Intent iMenuList = new Intent(getActivity(), ActivityMenuBulk.class);
                        iMenuList.putExtra("category_id", New_Category_ID.get(position));
                        iMenuList.putExtra("category_name", New_Category_name.get(position));
                        startActivity(iMenuList);
                        getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                        level = 1;
                    } else if (New_IsLeaf.get(position).equals("0")) {

                        Intent iMenuList = new Intent(getActivity(), ActivitySubCategoryList.class);
                        iMenuList.putExtra("category_id", New_Category_ID.get(position).toString());

                        iMenuList.putExtra("category_name", New_Category_name.get(position));
                        startActivity(iMenuList);
                        getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                        level = 2;

                    } else {
                        Intent iMenuList = new Intent(getActivity(), ActivityMenuBulk.class);
                        iMenuList.putExtra("category_id", New_Category_ID.get(position));
                        iMenuList.putExtra("category_name", New_Category_name.get(position));
                        startActivity(iMenuList);
                        getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
                        level = 1;

                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(),ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // get keyword and send it to server

                try {
                    Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(),ActivityMenuBulk.class);
                intent.putExtra("keyword",Keyword);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);



            }
        });


	/*	gridview = (GridView) v.findViewById(R.id.gridView1);
		gridview.setOnItemClickListener(this);

		data.add(new GridViewItem(getResources().getString(R.string.menu_product), getResources().getDrawable(R.drawable.ic_product)));
		data.add(new GridViewItem(getResources().getString(R.string.menu_cart), getResources().getDrawable(R.drawable.ic_cart)));
		//data.add(new GridViewItem(getResources().getString(R.string.menu_checkout), getResources().getDrawable(R.drawable.ic_checkout)));
        data.add(new GridViewItem(getResources().getString(R.string.menu_enquiry), getResources().getDrawable(R.drawable.ic_contact)));
	//	data.add(new GridViewItem(getResources().getString(R.string.menu_profile), getResources().getDrawable(R.drawable.ic_profile)));
	//	data.add(new GridViewItem(getResources().getString(R.string.menu_info), getResources().getDrawable(R.drawable.ic_info)));
        data.add(new GridViewItem(getResources().getString(R.string.menu_contact), getResources().getDrawable(R.drawable.ic_profile)));
        data.add(new GridViewItem(getResources().getString(R.string.menu_notification), getResources().getDrawable(R.drawable.ic_notification)));
		data.add(new GridViewItem(getResources().getString(R.string.menu_share), getResources().getDrawable(R.drawable.ic_share)));


		setDataAdapter();
        */
        return v;
    }

    // asynctask class to handle parsing json in background
    public class getDataTaskProducts extends AsyncTask<Void, Void, Void>{

        // show progressbar first
        getDataTaskProducts(){
/*            if(!prgLoading.isShown()){
                prgLoading.setVisibility(0);
                txtAlert.setVisibility(0);
            } */
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            parseJSONDataProducts();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // when finish parsing, hide progressbar
         //   prgLoading.setVisibility(8);

            // if data available show data on list
            // otherwise, show alert text
            if(Menu_ID.size() > 0){
                listMenu.setVisibility(View.GONE);
                listMenu.setAdapter(mla);
                listMenu.setExpanded(true);
            }else{
           //     txtAlert.setVisibility(0);
            }

        }
    }

    // method to parse json data from server
    public void parseJSONDataProducts(){

        clearDataProducts();

        try {
            // request data from menu API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 50000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 50000);
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
                Menu_quantity.add(menu.getString("Quantity"));
                Menu_price.add(Double.valueOf(formatData.format(menu.getDouble("Price"))));
                Menu_max_price.add(Double.valueOf(formatData.format(menu.getDouble("MaxPrice"))));
                Menu_image.add(menu.getString("Menu_image"));
                Log.d("d",str);

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

    // Set the Data Adapter
    private void setDataAdapter() {
        gridviewAdapter = new AdapaterGridView(getActivity(), R.layout.fragment_list_item, data);
        gridview.setAdapter(gridviewAdapter);

      //  setGridViewHeightBasedOnChildren(listCategory,6);
        listCategory.setExpanded(true);
    }


    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }

    @Override
    public void onItemClick(final AdapterView<?> arg0, final View view, final int position, final long id) {
        if (position==0){
            startActivity(new Intent(getActivity(), ActivityCategoryList.class));
            getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
        }
        else if (position==1){
            startActivity(new Intent(getActivity(), ActivityCart.class));
            getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
        }
        else if (position==2){
            startActivity(new Intent(getActivity(), ActivityEnquiry.class));
            getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
        }
        else if (position==3){
            startActivity(new Intent(getActivity(), ActivityContact.class));
            getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
        }
	/*	else if (position==4){
			startActivity(new Intent(getActivity(), ActivityInformation.class));
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
		}
		else if (position==5){
			startActivity(new Intent(getActivity(), ActivityAbout.class));
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
		} */
        else if (position==5){
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, "Download our app:\n\""+getString(R.string.app_name)+"\" \nhttps://play.google.com/store/apps/details?id="+getActivity().getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        }
        else {
            startActivity(new Intent(getActivity(), ActivityNotification.class));
            getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_next);
        }

    }



    void clearData(){
        Category_ID.clear();
        Category_name.clear();
        Category_image.clear();
        IsLeaf.clear();
    }



    // asynctask class to handle parsing json in background
    public class getDataTaskCategory extends AsyncTask<Void, Void, Void>{

        // show progressbar first
        getDataTaskCategory(){

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
            if((New_Category_ID.size() > 0) && (IOConnect == 0)){
                listNewCategory.setVisibility(View.GONE);
                listNewCategory.setAdapter(new_cla);
                listNewCategory.setExpanded(true);

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
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 50000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 50000);
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

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject category = object.getJSONObject("Category");

                New_Category_ID.add(Long.parseLong(category.getString("Category_ID")));
                New_Category_name.add(category.getString("Category_name"));
                New_Category_image.add(category.getString("Category_image"));
                //New_IsLeaf.add(category.getString("is_leaf"));
                New_IsLeaf.add("1");
              //  Log.d("Category name", Category_name.get(i));

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
        New_Category_ID.clear();
        New_Category_name.clear();
        New_Category_image.clear();
        New_IsLeaf.clear();
    }

    void clearDataProducts(){

            Menu_ID.clear();
            Menu_name.clear();
            Menu_quantity.clear();
            Menu_price.clear();
            Menu_max_price.clear();
            Menu_image.clear();

    }

    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void> {

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

            // otherwise, show alert text
            if((Category_ID.size() > 0) && (IOConnect == 0)){
//                listCategory.setVisibility(View.VISIBLE);
                listCategory.setAdapter(cla);
            }else{
                txtAlert.setVisibility(View.INVISIBLE);
            }
        }
    }

    // method to parse json data from server
    public void parseJSONData(){

     //   clearData();

      /*  try {
            // request data from Category API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 50000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 50000);
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
        }*/

        clearData();

        Category_ID.add(0L);
        Category_name.add("Photography");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("Videography");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("About Us");
        Category_image.add("");
        IsLeaf.add("0");

        Category_ID.add(0L);
        Category_name.add("Contact Us");
        Category_image.add("");
        IsLeaf.add("0");





    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        cla.imageLoader.clearCache();

        super.onDestroy();
    }

}