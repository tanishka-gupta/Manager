package in.manager.loanchacha;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


// adapter class fro custom menu list
class AdapterDirectoryList extends BaseAdapter {

    private Activity activity;
    public ImageLoader imageLoader;


    public AdapterDirectoryList(Activity act) {
        this.activity = act;
        imageLoader = new ImageLoader(act);

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ActivityDirectoryList.Resume_ID.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.directory_list_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
        holder.txtSubText = (TextView) convertView.findViewById(R.id.txtSubText);
        holder.imgThumbnail = (ImageView) convertView.findViewById(R.id.imgThumb);



        Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.close_main);


      //  String mainText =  ActivityDirectoryList.Resume_Job.get(position) + " (" + ActivityDirectoryList.Resume_Name.get(position)+")" ;

        String mainText =   ActivityDirectoryList.Resume_Name.get(position) ;
        holder.txtText.setText(Html.fromHtml(mainText));

        String subText="<b>Phone: </b>"+" ";

        subText+=""+ActivityDirectoryList.Resume_Qualification.get(position)+" ";

     //    subText += "<b>City: </b>"+"";

       // subText+=""+ActivityDirectoryList.Resume_Salary.get(position)+"";




    //    subText += " <b>Membership: </b>"+"";
      //  subText+=""+ActivityDirectoryList.Resume_Experience.get(position)+"";


       // subText+=" | <b>Time</b>: "+ActivityDirectoryList.Job_Timing.get(position)+" ";
       // subText+="  |  <b>Qualification</b>: "+ActivityDirectoryList.Resume_Qualification.get(position)+"";



        holder.txtSubText.setText(Html.fromHtml(subText));

   //     holder.txtText.startAnimation(animation);
     //   holder.txtSubText.startAnimation(animation);


      //  imageLoader.DisplayImage();

        imageLoader.DisplayImage(Constant.AdminPageURL+ActivityDirectoryList.Resume_Image.get(position), holder.imgThumbnail);
        return convertView;
    }

    static class ViewHolder {
        TextView txtText, txtSubText;
        ImageView imgThumbnail;

    }




}