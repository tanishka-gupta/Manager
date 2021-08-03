package in.manager.loanchacha;

import java.io.IOException;
import java.util.ArrayList;



import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;

@SuppressLint("NewApi")
@SuppressWarnings("ResourceType")
public class MainActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    private SliderLayout sliderLayout;

    String str_earn;
    String user_type, user_id, fr_phone;

    // static DBHelper dbhelper;
    ArrayList<ArrayList<Object>> data;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private AdapterNavDrawerList adapter;

	// declare dbhelper and adapter object
	static DBHelper dbhelper;
	AdapterMainMenu mma;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nav_drawer_main);



		// Parse push notification
		/* Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
		ParseAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, ActivityNotification.class);

		ParseInstallation.getCurrentInstallation().saveInBackground();
        */

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user_type = prefs.getString("user_type","");
        user_id = prefs.getString("user_id","");
		fr_phone = prefs.getString("Phone","");




		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerLayout.setDrawerShadow(R.drawable.navigation_drawer_shadow, GravityCompat.START);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem("About Us", navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem("Notifications", navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem("My Profile", navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem("Visiting Card", navMenuIcons.getResourceId(5, -1)));

		if(user_type.equals("FREELANCER")) {
			str_earn = "My Earnings";
		} else  {
			str_earn = "Commission";
		}
		navDrawerItems.add(new NavDrawerItem(str_earn, navMenuIcons.getResourceId(5, -1)));


		navDrawerItems.add(new NavDrawerItem("Greetings", navMenuIcons.getResourceId(5, -1)));
	//	navDrawerItems.add(new NavDrawerItem("Products", navMenuIcons.getResourceId(5, -1)));
	//	navDrawerItems.add(new NavDrawerItem("Pay Online", navMenuIcons.getResourceId(5, -1)));
	//	navDrawerItems.add(new NavDrawerItem("Photo Gallery", navMenuIcons.getResourceId(10, -1)));
	//	navDrawerItems.add(new NavDrawerItem("Video Gallery", navMenuIcons.getResourceId(10, -1)));
      //  navDrawerItems.add(new NavDrawerItem("Notifications", navMenuIcons.getResourceId(11, -1)));
		navDrawerItems.add(new NavDrawerItem("Facebook", navMenuIcons.getResourceId(10, -1)));
		navDrawerItems.add(new NavDrawerItem("Instagram", navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem("Youtube", navMenuIcons.getResourceId(10, -1)));
		navDrawerItems.add(new NavDrawerItem("Locate Us", navMenuIcons.getResourceId(10, -1)));
		navDrawerItems.add(new NavDrawerItem("Contact Us", navMenuIcons.getResourceId(5, -1)));
		//navDrawerItems.add(new NavDrawerItem("Credits", navMenuIcons.getResourceId(10, -1)));
		navDrawerItems.add(new NavDrawerItem("Logout", navMenuIcons.getResourceId(10, -1)));
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));

        // Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new AdapterNavDrawerList(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));

		// get screen device width and height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// checking internet connection
		if (!Constant.isNetworkAvailable(MainActivity.this)) {
			Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getBaseContext(), ActivityNoInternet.class);
			startActivity(intent);
			finish();
		} else {
          //  Toast.makeText(MainActivity.this,"P:"+Constant.Payment, Toast.LENGTH_SHORT).show();
        }

		/*if (!Constant.isNetworkAvailable(ActivitySplash.this)) {
			//Toast.makeText(ActivitySplash.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

		}*/

		mma = new AdapterMainMenu(this);
		dbhelper = new DBHelper(this);

		// create database
		try {
			dbhelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		// then, the database will be open to use
		try {
			dbhelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		// if user has already ordered food previously then show confirm dialog
		if (dbhelper.isPreviousDataExist()) {
			//showAlertDialog();
            dbhelper.deleteAllData();
            dbhelper.close();
		}

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
			//	getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
			//	getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	// show confirm dialog to ask user to delete previous order or not
	/*void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		builder.setMessage(getString(R.string.db_exist_alert));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// delete order data when yes button clicked
				dbhelper.deleteAllData();
				dbhelper.close();

			}
		});

		builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// close dialog when no button clicked
				dbhelper.close();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}
 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//dbhelper.deleteAllData();
	//	dbhelper.close();
//		finish();
      //  System.exit();
//		overridePendingTransition(R.anim.open_main, R.anim.close_next);

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						dbhelper.deleteAllData();
						dbhelper.close();
						finish();
						//  System.exit();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
						dialog.dismiss();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						dialog.dismiss();
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).setTitle("Exit App?").show();


	}

	/**
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
        switch(item.getItemId()) {
            case R.id.share:

            	if(user_type.equals("FREELANCER")) {

				Intent sendInt = new Intent(Intent.ACTION_SEND);
				//sendInt.putExtra(Intent.EXTRA_SUBJECT, "Loan Chacha App");
				sendInt.putExtra(Intent.EXTRA_TEXT, "Loan Requirements? Download our app: \""+"Loan Chacha"+"\" https://play.google.com/store/apps/details?id=in.app.loanchacha\nUse Referral Id "+fr_phone+" when signup.");
				sendInt.setType("text/plain");
				startActivity(Intent.createChooser(sendInt, "Share"));
				return true;

				} else {

				Intent sendInt = new Intent(Intent.ACTION_SEND);
				//sendInt.putExtra(Intent.EXTRA_SUBJECT, "");
				sendInt.putExtra(Intent.EXTRA_TEXT, "Download our app: \""+getString(R.string.app_name)+"\" https://play.google.com/store/apps/details?id="+getPackageName());
				sendInt.setType("text/plain");
				startActivity(Intent.createChooser(sendInt, "Share"));
				return true;

				}


			case R.id.call:
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.Helpline));
				startActivity(intent);
				return true;

			case R.id.whatsapp:

				/* intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Paytm));
				startActivity(intent); */
				try {
					Uri uri = Uri.parse("smsto:" + Constant.Paytm);
					Intent i = new Intent(Intent.ACTION_SENDTO, uri);
					i.setPackage("com.whatsapp");
					startActivity(Intent.createChooser(i, ""));
					return true;
				} catch (Exception ex) {
					Toast.makeText(MainActivity.this,"Please Install WhatsApp",Toast.LENGTH_LONG).show();
				}



			case R.id.cart:
				// refresh action
				Intent iMyOrder = new Intent(MainActivity.this, ActivityCart.class);
				startActivity(iMyOrder);
				overridePendingTransition (R.anim.open_next, R.anim.close_next);
				return true;




		}
		// Handle action bar actions click
	/*	switch (item.getItemId()) {
		case R.id.rate_app:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
			}
			return true;
		case R.id.more_app:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps))));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		} */
        return super.onOptionsItemSelected(item);
	}

	/*
	 * * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	//	menu.findItem(R.id.ic_menu).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new ActivityHome();
			break;

		case 1:
			startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;


			case 2:

                Intent i = new Intent(getApplicationContext(), ActivityPayu.class);
                //String ar[] = url.split(":");
                i.putExtra("URL",Constant.NotificationsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                i.putExtra("title","Notifications");
                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);


                //startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;


            case 3:

                 i = new Intent(getApplicationContext(), ActivityPayu.class);
                //String ar[] = url.split(":");
                i.putExtra("URL",Constant.MyProfileAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
                i.putExtra("title","My Profile");
                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);


                //startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;

			case 4:

				i = new Intent(getApplicationContext(), ActivityPayu.class);
				//String ar[] = url.split(":");
				i.putExtra("URL",Constant.MyVisitingCardAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
				i.putExtra("title","My Visiting Card");
				startActivity(i);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);


				//startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
				//overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;

		 case 5:
			/* startActivity(new Intent(getApplicationContext(), ActivityForms.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next); */

			 i = new Intent(getApplicationContext(), ActivityPayu.class);
			 //String ar[] = url.split(":");
			 i.putExtra("URL",Constant.MyEarningsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
			 i.putExtra("title",str_earn);
			 startActivity(i);
			 overridePendingTransition(R.anim.open_next, R.anim.close_next);


			 //startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
			 //overridePendingTransition(R.anim.open_next, R.anim.close_next);
			 break;

			case 6:
			 startActivity(new Intent(getApplicationContext(), ActivityEntList.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);

		/*		i = new Intent(getApplicationContext(), ActivityPayu.class);
				//String ar[] = url.split(":");
				i.putExtra("URL",Constant.GreetingsAPI+"?accesskey="+Constant.AccessKey+"&user_type="+user_type+"&user_id="+user_id);
				i.putExtra("title","Greetings");
				startActivity(i);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);*/


				//startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
				//overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;


/*
			case 3:

				// payonline

				try {

					final CharSequence[] items = {
							"Paytm", "BHIM"
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Pay Online");
					builder.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// Do something with the selection
							if(item==0) {


								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Paytm)));
								overridePendingTransition(R.anim.open_next, R.anim.close_next);


							} else {



								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Bhim)));
								overridePendingTransition(R.anim.open_next, R.anim.close_next);

							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();


				} catch (Exception ex) {
					Toast.makeText(MainActivity.this,ex.toString(),Toast.LENGTH_LONG).show();
				}

				break;


			case 4:
				startActivity(new Intent(getApplicationContext(), ActivityGalleryCategoryList.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;
			case 5:
				startActivity(new Intent(getApplicationContext(), ActivityVideoGalleryCategoryList.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;



			case 6:
				startActivity(new Intent(getApplicationContext(), ActivityNotification.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);

				break;*/



			case 7:

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Facebook)));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;


			case 8:

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Instagram)));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;


			case 9:

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Youtube)));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;


			case 10:

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Locate)));

				break;
















			case 11:
				startActivity(new Intent(getApplicationContext(), ActivityContact.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;

















		/*	case 12:
				startActivity(new Intent(getApplicationContext(), ActivityDevelopers.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				break;*/

			case 12: //logout
				prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();


				editor.remove("Phone");
				editor.remove("Password");
				editor.apply();


				//  editor.clear();
				//  editor.commit();
				dbhelper.deleteAllData();
				dbhelper.close();

				startActivity(new Intent(getApplicationContext(), ActivitySignup.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
				MainActivity.this.finish();

				break;

        case 14:

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							//Yes button clicked

							dbhelper.deleteAllData();
							dbhelper.close();
							MainActivity.this.finish();
							overridePendingTransition(R.anim.open_next, R.anim.close_next);

							dialog.dismiss();
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							//No button clicked
							dialog.dismiss();
							break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).setTitle("Exit App?").show();


			break;



		default:
			break;
		}

		if (fragment != null) {
			android.app.FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
		//  super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case 1: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}
			case 55: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(MainActivity.this, "Permission denied to make call", Toast.LENGTH_SHORT).show();
				}
				return;
			}




			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

    public void callNow(View view) {
        //Toast.makeText(this,"Hello",Toast.LENGTH_LONG).show();

        String uri = "tel:" + Constant.Helpline ;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);

    }

}
