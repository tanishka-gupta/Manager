package in.manager.loanchacha;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// adapter class fro custom menu list
class AdapterBlogList extends BaseAdapter {

		private Activity activity;
		public ImageLoader imageLoader;
		
		
		public AdapterBlogList(Activity act) {
			this.activity = act;
			imageLoader = new ImageLoader(act);

		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return ActivityBlogList.Menu_ID.size();
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
				convertView = inflater.inflate(R.layout.blog_list_item, null);
				holder = new ViewHolder();
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.txtText = (TextView) convertView.findViewById(R.id.txtText);


            float per = 0;

			holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
			
			holder.txtText.setText(ActivityBlogList.Menu_name.get(position));

            String str="";
			if(ActivityBlogList.Menu_price.get(position)==0L) {
//                holder.txtSubText.setVisibility(View.VISIBLE);
			//	holder.txtSubText.setText("");
            //    str =  "<font color='#A80000' >Enquire Now</font>"  ;
              //  holder.txtSubTextmax.setText(Html.fromHtml(str));
              //  holder.txtSubTextmax.setPaintFlags(holder.txtSubTextmax.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
				if (ActivityBlogList.Menu_price.get(position) < ActivityBlogList.Menu_max_price.get(position)) {
				//	holder.txtSubText.setVisibility(View.VISIBLE);


				} else {

//					holder.txtSubTextmax.setVisibility(View.GONE);
				}


				float max_price = Float.parseFloat(ActivityBlogList.Menu_max_price.get(position).toString());
				float price = Float.parseFloat(ActivityBlogList.Menu_price.get(position).toString());
				float disc = max_price - price;

				try {

					per = disc / max_price * 100;
				} catch (Exception ex) {

				}

				if (per == 100) {
					per = 0;
				}

				if (per == 0) {

				} else {

				}

				str = ActivityBlogList.Currency + " " + ActivityBlogList.Menu_price.get(position);

			}


            imageLoader.DisplayImage(Constant.AdminPageURL+ActivityBlogList.Menu_image.get(position), holder.imgThumb);
			
			return convertView;
		}
		
		static class ViewHolder {
			TextView txtText;
			ImageView imgThumb;
		}
		
		
	}