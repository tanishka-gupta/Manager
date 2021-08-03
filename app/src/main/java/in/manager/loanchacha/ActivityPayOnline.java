package in.manager.loanchacha;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class ActivityPayOnline extends Activity {


    WebView txtDescription;
    // declare dbhelper object

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.pay_online);


     /*   Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */



        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Pay Online");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        txtDescription = (WebView) findViewById(R.id.txtDescription);
      //  txtDescription.setWebChromeClient(new WebChromeClient());

       String url = getIntent().getStringExtra("URL");
      //  txtDescription.loadDataWithBaseURL("", Menu_description, "text/html", "UTF-8", "");

        txtDescription.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = txtDescription.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
       // txtDescription.setWebViewClient(new WebViewClient());

        txtDescription.loadUrl(url);

        // txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));

    }





    // method to show number of order form


    // asynctask class to handle parsing json in background





    // close database before back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
     //   super.onBackPressed();
     //   finish();
       // overridePendingTransition(R.anim.open_main, R.anim.close_next);
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
