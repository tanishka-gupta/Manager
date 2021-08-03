package in.manager.loanchacha;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityNotification extends Activity {


    private ProgressBar progressBar;
    static DBHelper dbhelper;
    private ListView listView;
    private String jsonData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);


     /*   Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, ActivityNotification.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

*/

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Notification");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        dbhelper = new DBHelper(this);
        dbhelper.openDataBase();

        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            jsonData = extras.getString("com.parse.Data");
            JSONObject job = new JSONObject(jsonData);
            String ar = job.getString("alert");
            dbhelper.addNotification(ar);
        } catch (Throwable ex) {
           // Toast.makeText(ActivityNotification.this,ex.toString(),Toast.LENGTH_LONG).show();

        }

        listView = (ListView) findViewById(R.id.ListViewNotification);

       try {







           final List<Notification> allNotifications = new ArrayList<Notification>();

           ArrayList<ArrayList<Object>> data = dbhelper.getAllNotification();
           for(int i=0;i<data.size();i++){
               ArrayList<Object> row = data.get(i);

               String msg = row.get(1).toString();
               String date = row.get(2).toString();
               String id = row.get(0).toString();
               // Toast.makeText(ActivityNotification.this, "ARR "+id+" "+date+" "+msg,Toast.LENGTH_LONG).show();
               allNotifications.add(new Notification(id,msg,date));



           }



           final NotificationAdapter notificationAdapter = new NotificationAdapter(this,  allNotifications);

           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                   Toast.makeText(ActivityNotification.this, "Touch and hold to delete a message.", Toast.LENGTH_SHORT).show();

               }
           });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final int pos = i;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNotification.this);
                    builder.setMessage("Delete this notification?").setTitle("Delete Notification");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Code that is executed when clicking YES
                            dbhelper.deleteNotification(Long.parseLong(allNotifications.get(pos).getId()));
                            allNotifications.clear();
                            ArrayList<ArrayList<Object>> dataX = dbhelper.getAllNotification();
                            for (int i = 0; i < dataX.size(); i++) {
                                ArrayList<Object> row = dataX.get(i);

                                String msg = row.get(1).toString();
                                String date = row.get(2).toString();
                                String id = row.get(0).toString();
                                // Toast.makeText(ActivityNotification.this, "ARR "+id+" "+date+" "+msg,Toast.LENGTH_LONG).show();
                                allNotifications.add(new Notification(id, msg, date));


                            }

                            // notificationAdapter.notifyDataSetChanged();
                            listView.setAdapter(notificationAdapter);

                            Toast.makeText(ActivityNotification.this, "Notification Deleted", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                        }

                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Code that is executed when clicking YES

                            dialog.dismiss();
                        }

                    });


                    builder.show();
                return true;
                }
            });

           listView.setAdapter(notificationAdapter);

           } catch (Exception e) {

               Toast.makeText(ActivityNotification.this, "DBER "+e.toString(),Toast.LENGTH_LONG).show();
           }









    }

   /* private void refresh(List<Notification> allNotifications){
        allNotifications.removeAll();
        ArrayList<ArrayList<Object>> data = dbhelper.getAllNotification();
        for(int i=0;i<data.size();i++){
            ArrayList<Object> row = data.get(i);

            String msg = row.get(1).toString();
            String date = row.get(2).toString();
            String id = row.get(0).toString();
            // Toast.makeText(ActivityNotification.this, "ARR "+id+" "+date+" "+msg,Toast.LENGTH_LONG).show();
            allNotifications.add(new Notification(id,msg,date));



        }
    } */

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
                Intent intent = new Intent(ActivityNotification.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;

            case R.id.share:
                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, "Download our app:\n\""+getString(R.string.app_name)+"\" \nhttps://play.google.com/store/apps/details?id="+getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));
                return true;

            case R.id.call:
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.Helpline));
                startActivity(intent2);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent = new Intent(ActivityNotification.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


}
