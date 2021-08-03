package in.manager.loanchacha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by chhapoliya on 20/01/16.
 */
public class Reciever extends BroadcastReceiver {

    static DBHelper dbhelper;
    String jsonData;
    @Override
    public void onReceive(Context context, Intent intent) {
      //  super.onReceive(context, intent);
        Log.d("H", "Here: ");
        Toast.makeText(context, "HELLO", Toast.LENGTH_LONG).show();
        try {
        dbhelper = new DBHelper(context);
        dbhelper.openDataBase();
        dbhelper.addNotification("HELLO FROM CODE");




            jsonData = intent.getExtras().getString("com.parse.Data");
            JSONObject job = new JSONObject(jsonData);
            String ar = job.getString("alert").toString();
            dbhelper.addNotification(ar);

         /*   Intent launchActivity = new Intent(context, ActivityNotification.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, launchActivity, 0);
            Notification noti = new NotificationCompat.Builder(context)
                    .setContentTitle("PUSH RECEIVED")
                    .setContentText(ar)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0, noti); */
        } catch (Throwable ex) {
             Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();

        }
    }
}
