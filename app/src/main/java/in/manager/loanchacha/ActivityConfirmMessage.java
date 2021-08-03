package in.manager.loanchacha;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityConfirmMessage extends Activity {
	
	// declare view objects
//	ImageButton imgNavBack;

	String pkg_name, pkg_price;
	TextView payText;

	ImageButton imgPaytm, imgBhim;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);

      /*  Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground(); */

        
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Thank You!");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        payText = (TextView) findViewById(R.id.payText);


		Intent iGet = getIntent();
		String package_name = iGet.getStringExtra("package");
		if(package_name==null) {
			package_name = "";
		}

		pkg_name = iGet.getStringExtra("package");
		pkg_price = iGet.getStringExtra("price");

		payText.setText("Service: "+pkg_name + "  \nAmount: ₹ "+pkg_price);


        imgBhim = (ImageButton) findViewById(R.id.imgbtnBhim);
		imgPaytm = (ImageButton) findViewById(R.id.imgbtnPaytm);

		imgBhim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				try {

					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Bhim)));

				} catch (Exception ex) {
					Toast.makeText(ActivityConfirmMessage.this,"App not installed or server error.",Toast.LENGTH_LONG).show();
				}

			}
		});

		imgPaytm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				try {

					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Paytm)));

				} catch (Exception ex) {
					Toast.makeText(ActivityConfirmMessage.this,"App not installed or server error.",Toast.LENGTH_LONG).show();
				}

			}
		});

        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			Intent intent = new Intent(ActivityConfirmMessage.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.open_main, R.anim.close_next);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	Intent intent = new Intent(ActivityConfirmMessage.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    
}
