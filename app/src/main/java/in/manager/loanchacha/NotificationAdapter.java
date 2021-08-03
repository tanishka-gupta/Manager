package in.manager.loanchacha;

/**
 * Created by chhapoliya on 21/01/16.
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationAdapter extends BaseAdapter implements OnClickListener {
    private Context context;

    private List<Notification> listNotification;

    public NotificationAdapter(Context context, List<Notification> listPhonebook) {
        this.context = context;
        this.listNotification = listPhonebook;
    }

    public int getCount() {
        return listNotification.size();
    }

    public Object getItem(int position) {
        return listNotification.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Notification entry = listNotification.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_listview, null);
        }
  //      TextView tvId = (TextView) convertView.findViewById(R.id.noti_id);
    //    tvId.setText(entry.getId());

        TextView tvMsg = (TextView) convertView.findViewById(R.id.noti_msg);
        tvMsg.setText(entry.getMsg());

    //    Date date = new Date(entry.getOntime());
      //  DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        String nDate="";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("IST"));

            Date date = format.parse(entry.getOntime());

            format = new SimpleDateFormat("dd MMMM, yyyy - hh:mm aa");
            nDate = format.format(date);


        } catch (Exception e) {
            nDate = entry.getOntime();
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }

        TextView tvOntime = (TextView) convertView.findViewById(R.id.noti_ontime);
        tvOntime.setText(nDate);

        // Set the onClick Listener on this button
    //    Button btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
     //   btnRemove.setFocusableInTouchMode(false);
    //    btnRemove.setFocusable(false);
      //  btnRemove.setOnClickListener(this);
        // Set the entry, so that you can capture which item was clicked and
        // then remove it
        // As an alternative, you can use the id/position of the item to capture
        // the item
        // that was clicked.
       // btnRemove.setTag(entry);

        // btnRemove.setId(position);


        return convertView;
    }

    @Override
    public void onClick(View view) {
        Notification entry = (Notification) view.getTag();
        listNotification.remove(entry);
        // listPhonebook.remove(view.getId());
        notifyDataSetChanged();

    }

    private void showDialog(Notification entry) {
        // Create and show your dialog
        // Depending on the Dialogs button clicks delete it or do nothing
    }

}