package in.manager.loanchacha;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;





import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Parcelable;

@SuppressLint("NewApi")

public class ActivityServiceForm extends Activity {
    private WebView browser;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressBar progressBar;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private static final int FILECHOOSER_RESULTCODE   = 2888;

    int MODE = 1;

    //file uploading
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;


    //CollapseHelper collapseHelper;

    //@SuppressWarnings("deprecation")
    //@SuppressLint({ "SetJavaScriptEnabled", "InlinedApi", "NewApi" })
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);


      /*  Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
       ParseAnalytics.trackAppOpened(getIntent());
       // ParseAnalytics.trackAppOpenedInBackground(getIntent());

        PushService.setDefaultPushCallback(this, MainActivity.class);
       // PushService.set
        ParseInstallation.getCurrentInstallation().saveInBackground();
*/



        setContentView(R.layout.wview);



        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Apply Loan");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);


        browser = (WebView) findViewById(R.id.webkit);

        // set javascript and zoom and some other settings
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(false);
        browser.getSettings().setGeolocationEnabled(true);
        browser.getSettings().setAppCacheEnabled(true);
        browser.getSettings().setDatabaseEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        // Below required for geolocation
        browser.getSettings().setGeolocationEnabled(true);



        // enable all plugins (flash)
        browser.getSettings().setPluginState(PluginState.ON);

        // application rating
        // showing the splash screen
        findViewById(R.id.imageLoading1).setVisibility(View.VISIBLE);
        findViewById(R.id.webkit).setVisibility(View.GONE);

        // do you want to download, dialog
        browser.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(final String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                // show an alert if the user would like to download the file
                Builder builder = new Builder(
                        ActivityServiceForm.this);

                // setting the dialog title with custom color theme
                TextView title = new TextView(ActivityServiceForm.this);
                title.setBackgroundColor(title.getContext().getResources()
                        .getColor(R.color.apptheme_accent));
                title.setTextColor(title.getContext().getResources()
                        .getColor(R.color.white));
                title.setText("Download");
                title.setTextSize(23f);
                // title.setLayoutParams(ViewGroup.MATCH_PARENT);
                title.setPadding(20, 15, 10, 15);

                builder.setCustomTitle(title);
                // getText(R.string.ok
                builder.setMessage("Download?");
                builder.setCancelable(false)
                        .setPositiveButton(getText(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // old method (new
                                        // DownloadAsyncTask()).execute(url);
                                        downloadFile(getBaseContext(), url,
                                                null, null);
                                    }

                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                builder.create().show();

            }

        });

        // checking if download is done
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                // setting the dialog title with custom color theme
                TextView title = new TextView(ActivityServiceForm.this);
                title.setBackgroundColor(title.getContext().getResources()
                        .getColor(R.color.apptheme_accent));
                title.setTextColor(title.getContext().getResources()
                        .getColor(R.color.apptheme_primary_dark));
                title.setText("Downlaod");
                title.setTextSize(23f);
                // title.setLayoutParams(ViewGroup.MATCH_PARENT);
                title.setPadding(20, 15, 10, 15);
                Builder builder = new Builder(
                        ActivityServiceForm.this);
                builder.setMessage("Download compelte")
                        .setPositiveButton("Ok", null)
                        .setCustomTitle(title);
                builder.show();
            }
        };

        registerReceiver(onComplete, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        browser.setWebViewClient(new WebViewClient() {
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
                }  else if (url.contains("paytmmp://")) {

                    try {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch(Exception ex) {
                        Toast.makeText(ActivityServiceForm.this,"Please Install Paytm App",Toast.LENGTH_LONG).show();
                    }

                    return  true;

                } else if (url.contains("upi://")) {

                    try {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch(Exception ex) {
                        Toast.makeText(ActivityServiceForm.this,"Please Install App",Toast.LENGTH_LONG).show();
                    }

                    return  true;

                }
                else if(url.equals("http://kumhar.chhapoliya.in/contact/success")) {
                    Intent i = new Intent(ActivityServiceForm.this, ActivityConfirmMessage.class);
                    view.getContext().startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    return true;

                } else if(url.equals("http://kumhar.chhapoliya.in/contact/failure")) {
                    Intent i = new Intent(ActivityServiceForm.this, ActivityFailureMessage.class);
                    view.getContext().startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);

                    return true;

                } else   if (url.endsWith(".mp4") || url.endsWith(".avi")
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
                }



                // Return true to override url loading (In this case do
                // nothing).
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // hide loading image
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        // hide splash image
                        if (findViewById(R.id.imageLoading1).getVisibility() == View.VISIBLE){
                            findViewById(R.id.imageLoading1).setVisibility(
                                    View.GONE);
                            // show webview
                            findViewById(R.id.webkit).setVisibility(View.VISIBLE);
                        }
                    }
                    // set a delay before splashscreen is hidden
                }, 0);
            }

            // handeling arrors
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Builder builder = new Builder(
                        ActivityServiceForm.this);
                builder.setMessage(description)
                        .setPositiveButton("Ok", null)
                        .setTitle("Whoops");
                builder.show();
            }

        });

        String url = getIntent().getStringExtra("URL");
        //    String url = "http://kumhar.chhapoliya.in/payu/success.php";
        // load url (if connection available
        if (checkConnectivity()) {
            browser.loadUrl(url);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        // has all to do with progress bar
        browser.setWebChromeClient(webChromeClient);


    }

    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            ActivityServiceForm.this.setProgress(progress * 1000);

            progressBar.incrementProgressBy(progress);

            if (progress == 100) {
                progressBar.setVisibility(View.GONE);
            }
        }

        // html5 geolocation
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){

            // Update message
            mUploadMessage = uploadMsg;

            try{

                // Create AndroidExampleFolder at sdcard

                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");

                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }

                // Create camera captured image file path and name
                File
                        file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg");

                mCapturedImageURI = Uri.fromFile(file);

                // Camera capture image intent
                final Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[] { captureIntent });

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

            }
            catch(Exception e){
                Toast.makeText(getBaseContext(), "Exception:"+e,
                        Toast.LENGTH_LONG).show();
            }

        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg){
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }

		 public boolean onShowFileChooser(
                 WebView webView, ValueCallback<Uri[]> filePathCallback,
                 FileChooserParams fileChooserParams) {
             if(mFilePathCallback != null) {
                 mFilePathCallback.onReceiveValue(null);
             }
             mFilePathCallback = filePathCallback;

             Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                 // Create the File where the photo should go
                 File photoFile = null;
                 try {
                     photoFile = createImageFile();
                     takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                 } catch (IOException ex) {
                     // Error occurred while creating the File
                     Log.e("INFO", "Unable to create Image File", ex);
                 }

                 // Continue only if the File was successfully created
                 if (photoFile != null) {
                     mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                             Uri.fromFile(photoFile));
                 } else {
                     takePictureIntent = null;
                 }
             }

             Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
             contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
             contentSelectionIntent.setType("image/*");

             Intent[] intentArray;
             if(takePictureIntent != null) {
                 intentArray = new Intent[]{takePictureIntent};
             } else {
                 intentArray = new Intent[0];
             }

             Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
             chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
             chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
             chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

             startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

             return true;
         }


        // Setting the title
        @Override
        public void onReceivedTitle(WebView view, String title) {
            //	MainActivity.this.setTitle(browser.getTitle());
            ActivityServiceForm.this.setTitle("Pay Online");


        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (ActivityServiceForm.this == null) {
                return null;
            }

            return BitmapFactory.decodeResource(ActivityServiceForm.this
                            .getApplicationContext().getResources(),
                    R.drawable.vert_loading);
        }

        @SuppressLint("InlinedApi")
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onShowCustomView(View view,
                                     CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                onHideCustomView();
                return;
            }

            // 1. Stash the current state
            mCustomView = view;
            mCustomView.setBackgroundColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mOriginalSystemUiVisibility = ActivityServiceForm.this.getWindow()
                        .getDecorView().getSystemUiVisibility();
            }
            mOriginalOrientation = ActivityServiceForm.this.getRequestedOrientation();

            // 2. Stash the custom view callback
            mCustomViewCallback = callback;

            // 3. Add the custom view to the view hierarchy
            FrameLayout decor = (FrameLayout) ActivityServiceForm.this.getWindow()
                    .getDecorView();
            decor.addView(mCustomView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // 4. Change the state of the window
		/*	MainActivity.this
					.getWindow()
					.getDecorView()
					.setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE); */
            ActivityServiceForm.this
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onHideCustomView() {
            // 1. Remove the custom view
            FrameLayout decor = (FrameLayout) ActivityServiceForm.this.getWindow()
                    .getDecorView();
            decor.removeView(mCustomView);
            mCustomView = null;

            // 2. Restore the state to it's original form
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ActivityServiceForm.this.getWindow().getDecorView()
                        .setSystemUiVisibility(mOriginalSystemUiVisibility);
            }
            ActivityServiceForm.this.setRequestedOrientation(mOriginalOrientation);

            // 3. Call the custom view callback
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;

        }

    };


    @Override
    public void onBackPressed() {
        /*
  */

        if(browser.getUrl().equals("http://kumhar.chhapoliya.in/payu/success.php") || browser.getUrl().equals("http://kumhar.chhapoliya.in/payu/failure.php") ) {

            startActivity(new Intent(ActivityServiceForm.this, MainActivity.class));
            overridePendingTransition(R.anim.open_next, R.anim.close_next);


        } else {

          /* Builder builder = new Builder(ActivityServiceForm.this);
           builder.setMessage("Are you sure you want to cancel?").setTitle("Cancel Operation");
           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
*/
            // Code that is executed when clicking YES

            //   startActivity(new Intent(ActivityServiceForm.this, MainActivity.class));
            // overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();


                /*   dialog.dismiss();
               }

           });
           builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                   // Code that is executed when clicking YES

                   dialog.dismiss();
               }

           });


           builder.show();*/
        }
    }

//	@Override
	/* protected void onPause() {
		super.onPause(); // To change body of overridden methods use File |
							// Settings | File Templates.
		browser.onPause();
	} */

    //@Override
	/* protected void onResume() {
		super.onResume(); // To change body of overridden methods use File |
							// Settings | File Templates.
		browser.onResume();

		if (MODE == 3){
			findViewById(R.id.slide_down_view_include).setVisibility(View.VISIBLE);
			collapseHelper = new CollapseHelper(findViewById(R.id.slide_down_view_include), browser, this);

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.slide_down_view_include);
		}
	}
 */
    @Override
    protected void onStop() {
        super.onStop(); // To change body of overridden methods use File |
        // Settings | File Templates.
        // EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }

    // Tracking with google analytics
    @Override
    public void onStart() {
        super.onStart();
        // EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //  inflater.inflate(R.menu.menu, menu);
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


    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        if(requestCode==FILECHOOSER_RESULTCODE)
        {

            Toast.makeText(getApplicationContext(), "Here :",
                    Toast.LENGTH_LONG).show();
            if (null == this.mUploadMessage) {
                return;

            }

            Uri result=null;

            try{
                if (resultCode != RESULT_OK) {

                    result = null;
                    //    Toast.makeText(getApplicationContext(), "NOT OK :",
                    //  Toast.LENGTH_LONG).show();

                } else {

                    //   Toast.makeText(getApplicationContext(), "Here :",
                    //    Toast.LENGTH_LONG).show();
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                    //    Toast.makeText(getApplicationContext(), "success :",
                    //  Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            //mUploadMessage = null;

        }

        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // If there is not data, then we may have taken a photo
                if(mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    // new Download
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void downloadFile(final Context context, final String url,
                                    final String contentDisposition, final String mimetype) {
        try {
            DownloadManager download = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Uri nice = Uri.parse(url);
            DownloadManager.Request it = new DownloadManager.Request(nice);
            String fileName = URLUtil.guessFileName(url, contentDisposition,
                    mimetype);
            it.setTitle(fileName);
            it.setDescription(url);
            if (Build.VERSION.SDK_INT >= 11) {
                it.allowScanningByMediaScanner();
                it.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            String location = context.getSharedPreferences("settings", 0)
                    .getString("download", Environment.DIRECTORY_DOWNLOADS);
            it.setDestinationInExternalPublicDir(location, fileName);
            Log.i("Barebones", "Downloading" + fileName);
            download.enqueue(it);

        } catch (NullPointerException e) {
            Log.e("Barebones", "Problem downloading");
            Toast.makeText(context, "Error Downloading File",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Log.e("Barebones", "Problem downloading");
            Toast.makeText(context, "Error Downloading File",
                    Toast.LENGTH_SHORT).show();
        } catch (SecurityException ignored) {

        }
    }

    // Checking for an internet connection
    private boolean checkConnectivity() {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {

            // setting the dialog title with custom color theme
            TextView title = new TextView(this);
            title.setBackgroundColor(title.getContext().getResources()
                    .getColor(R.color.apptheme_accent));
            title.setTextColor(title.getContext().getResources()
                    .getColor(R.color.apptheme_primary_dark));
            title.setText("Error in connection");
            title.setTextSize(23f);
            // title.setLayoutParams(ViewGroup.MATCH_PARENT);
            title.setPadding(20, 15, 10, 15);

            enabled = false;
            Builder builder = new Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("No Internet connection");
            builder.setCancelable(false);
            builder.setNeutralButton("OK", null);
            builder.setCustomTitle(title);
            builder.create().show();
        }
        return enabled;
    }



    // showing about dialog


    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

}
