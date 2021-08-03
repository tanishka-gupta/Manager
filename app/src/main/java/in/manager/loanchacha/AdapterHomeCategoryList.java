package in.manager.loanchacha;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// adapter class for custom category list
class AdapterHomeCategoryList extends BaseAdapter {

    private Activity activity;
    public ImageLoader imageLoader;

    public AdapterHomeCategoryList(Activity act) {
        this.activity = act;
        imageLoader = new ImageLoader(act);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ActivityHome.Category_ID.size();
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
            convertView = inflater.inflate(R.layout.fragment_list_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
        holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
        holder.lytGridItem = (LinearLayout) convertView.findViewById(R.id.lytGridItem);

     /*   holder.txtText.setText(ActivityHome.Category_name.get(position));
        imageLoader.DisplayImage(Constant.AdminPageURL+ActivityHome.Category_image.get(position), holder.imgThumb);*/
        if(position%2 ==0) {
           // convertView.setBackgroundColor(Color.parseColor("#ff0000"));
       //     holder.lytGridItem.setBackgroundResource(R.color.header);
//            imageView.setColorFilter(ContextCompat.getColor(context, R.color.COLOR_YOUR_COLOR), android.graphics.PorterDuff.Mode.MULTIPLY);
         //   holder.imgThumb.setColorFilter(Color.argb(255, 255, 255, 255));
          //  holder.txtText.setTextColor(Color.argb(255, 255, 255, 255));



        }

       if(position==0) {
           holder.txtText.setText("New Lead");
           holder.imgThumb.setImageResource(R.drawable.ic_pay);


        }
        else if(position==1) {


           holder.txtText.setText("All Leads");
           holder.imgThumb.setImageResource(R.drawable.ic_assignment);

        }
        else if(position==2) {
            holder.txtText.setText("Create Order");
            holder.imgThumb.setImageResource(R.drawable.ic_abt2);
        }
        else if(position==3) {
            holder.txtText.setText("View Orders");
            holder.imgThumb.setImageResource(R.drawable.ic_enq);
        }

      /*  else if(position==5) {
            holder.txtText.setText("Blog");
            holder.imgThumb.setImageResource(R.drawable.ic_faculties);
        }*/

        return convertView;


    }

    static class ViewHolder {
        TextView txtText;
        ImageView imgThumb;
        LinearLayout lytGridItem;
    }



}