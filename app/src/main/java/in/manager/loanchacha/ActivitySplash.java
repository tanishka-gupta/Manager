package in.manager.loanchacha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.manager.loanchacha.R;

public class ActivitySplash extends Activity {

    ImageLoader imageLoader;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        Pushbots.sharedInstance().init(this);
  //      Pushbots.sharedInstance().setAlias("loanchacha");
        //   Pushbots.sharedInstance().






        //menyembunyikan title bar di layar acitivy
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash);
        prefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String phone = prefs.getString("Phone",null);
        final String password = prefs.getString("Password", null);




        try {


            Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/in.manager.loanchacha/"+R.string.app_name + "splash.jpg");
            Bitmap emptyBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
            if (bmp.sameAs(emptyBitmap)) {
                Toast.makeText(ActivitySplash.this,"here",Toast.LENGTH_LONG).show();
            } else {
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.splash_layout);
                Drawable dr = new BitmapDrawable(getResources(), bmp);
                relativeLayout.setBackgroundDrawable(dr);
            }



        }catch (Exception ex) {
            // Toast.makeText(ActivitySplash.this,ex.toString(),Toast.LENGTH_LONG).show();
        }

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute();


        /** Creates a count down timer, which will be expired after 5000 milliseconds */
        new CountDownTimer(1000,1000) {

            /** This method will be invoked on finishing or expiring the timer */
            @Override
            public void onFinish() {

                if (!Constant.isNetworkAvailable(ActivitySplash.this)) {
                    //Toast.makeText(ActivitySplash.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), ActivityNoInternet.class);
                    startActivity(intent);
                    finish();
                } else {
                    //  Toast.makeText(MainActivity.this,"P:"+Constant.Payment, Toast.LENGTH_SHORT).show();

                    //  Toast.makeText(ActivitySplash.this, "INTERNET WORKING", Toast.LENGTH_SHORT).show();

                    if(phone==null || password==null) {
                        Intent intent = new Intent(getBaseContext(), ActivitySignup.class);
                        startActivity(intent);
                        // Toast.makeText(getBaseContext(),"Signup",Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        //Toast.makeText(getBaseContext(),"Main",Toast.LENGTH_LONG).show();
                    }



                    //menutup layar activity
                    finish();

                }



            }

            /** This method will be invoked in every 1000 milli seconds until
             * this timer is expired.Because we specified 1000 as tick time
             * while creating this CountDownTimer
             */
            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();


    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


class DownloadTask extends AsyncTask<Void, Void, Void> {

    DownloadTask()
    {
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    protected Void doInBackground(Void... arg0) {
        try {
            URL url = new URL(Constant.AdminPageURL+"upload/images/splash.jpg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();


            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/in.manager.loanchacha/");
            File SDCardRoot = null;
            try{
                if(dir.isDirectory()) {
                    SDCardRoot = dir;

                } else {
                    if (dir.mkdir()) {
                        System.out.println("Directory created");
                        SDCardRoot = dir;
                    } else {
                        SDCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        System.out.println("Directory is not created");
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }


            File file = new File(SDCardRoot, R.string.app_name+"splash.jpg");
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int kocharmandalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

            }
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

