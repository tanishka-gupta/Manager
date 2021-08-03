package in.manager.loanchacha;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

// adapter class fro custom menu list
class AdapterMenuBulk extends BaseAdapter {

    private Activity activity;
    public ImageLoader imageLoader;
    static DBHelper dbhelper;


    public AdapterMenuBulk(Activity act) {
        this.activity = act;
        imageLoader = new ImageLoader(act);
        dbhelper = new DBHelper(act);

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ActivityMenuBulk.Menu_ID.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position,  View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        final View cView = convertView;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menu_list_item_bulk, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
        holder.txtSelectedOption = (TextView) convertView.findViewById(R.id.txtSelectedOption);
        holder.lytOptions = (LinearLayout) convertView.findViewById(R.id.lytOptionLayout);

        holder.btnChooseOption = (Button) convertView.findViewById(R.id.btnChooseOption);

        holder.txtTextHindi = (TextView) convertView.findViewById(R.id.txtTextHindi);

        holder.txtSubText = (TextView) convertView.findViewById(R.id.txtSubText);
        holder.txtSubTextMax = (TextView) convertView.findViewById(R.id.txtSubTextMax);
        holder.imgAdd = (ImageView) convertView.findViewById(R.id.add_qty);
        holder.imgRemove = (ImageView) convertView.findViewById(R.id.remove_qty);

        holder.txtQty = (EditText) convertView.findViewById(R.id.txt_qty);
           holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);

        holder.txtQty.setCursorVisible(false);
        holder.txtQty.setLongClickable(false);
        holder.txtQty.setFocusable(false);
        holder.txtQty.setSelected(false);
        holder.txtQty.setKeyListener(null);
        holder.txtQty.setBackgroundResource(android.R.color.transparent);

        holder.txtText.setText(ActivityMenuBulk.Menu_name.get(position));
        holder.txtTextHindi.setText(ActivityMenuBulk.Menu_name_hindi.get(position));
        holder.txtQty.setText(String.valueOf(ActivityMenuBulk.Menu_quantity.get(position)));

        holder.txtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDetail = new Intent(activity, ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", ActivityMenuBulk.Menu_ID.get(position));
                iDetail.putExtra("qty", String.valueOf(ActivityMenuBulk.Menu_quantity.get(position)));
                iDetail.putExtra("category_id",ActivityMenuBulk.Category_ID);
                iDetail.putExtra("keyword",ActivityMenuBulk.Keyword);
                iDetail.putExtra("category_name",ActivityMenuBulk.Category_name);

                activity.startActivity(iDetail);
                activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
                //activity.finish();
            }
        });

        holder.txtTextHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDetail = new Intent(activity, ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", ActivityMenuBulk.Menu_ID.get(position));
                iDetail.putExtra("qty", String.valueOf(ActivityMenuBulk.Menu_quantity.get(position)));
                iDetail.putExtra("category_id",ActivityMenuBulk.Category_ID);
                iDetail.putExtra("keyword",ActivityMenuBulk.Keyword);
                iDetail.putExtra("category_name",ActivityMenuBulk.Category_name);
                activity.startActivity(iDetail);
                activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
             //   activity.finish();
            }
        });

        if(ActivityMenuBulk.Menu_Stock.get(position)==0L) {

          //  holder.txtSubText.setText(ActivityMenuBulk.Currency + " " + ActivityMenuBulk.Menu_price.get(position) + " ");
           // holder.txtSubTextMax.setPaintFlags(holder.txtSubTextMax.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtSubTextMax.setPaintFlags(holder.txtSubTextMax.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
           // holder.txtSubText.setVisibility(View.GONE);
            holder.txtQty.setVisibility(View.GONE);
            holder.imgAdd.setVisibility(View.GONE);
            holder.imgRemove.setVisibility(View.GONE);
            holder.txtSubTextMax.setVisibility(View.VISIBLE);
           // holder.txtSubTextMax.setText("SOLD OUT");
            holder.lytOptions.setVisibility(View.GONE);

            if (ActivityMenuBulk.Menu_price.get(position) < ActivityMenuBulk.Menu_max_price.get(position)) {
                // holder.txtSubText.setVisibility(View.VISIBLE);
                holder.txtSubTextMax.setVisibility(View.VISIBLE);
                holder.txtSubTextMax.setText(ActivityMenuBulk.Currency + " " + ActivityMenuBulk.Menu_max_price.get(position) + " ");
            } else {
                holder.txtSubTextMax.setVisibility(View.GONE);
            }
            holder.txtSubTextMax.setVisibility(View.GONE);


        } else {


          //  holder.txtSubText.setVisibility(View.GONE);
            holder.txtQty.setVisibility(View.VISIBLE);
            holder.imgAdd.setVisibility(View.VISIBLE);
            holder.imgRemove.setVisibility(View.VISIBLE);

            if(ActivityMenuBulk.Menu_Sizes.get(position).length() > 0 ) {
                holder.lytOptions.setVisibility(View.VISIBLE);
            } else {
                holder.lytOptions.setVisibility(View.GONE);
            }

            holder.lytOptions.setVisibility(View.GONE);

//            holder.txtSubText.setText("Rs. "+pr[id] + " (" + sz[id] +")");

            if(ActivityMenuBulk.Selected_Menu_option.get(position).equals("")) {
                holder.txtSubText.setText(ActivityMenuBulk.Currency + "" + ActivityMenuBulk.Selected_Menu_price.get(position));

            } else {
                holder.txtSubText.setText(ActivityMenuBulk.Currency + "" + ActivityMenuBulk.Selected_Menu_price.get(position) + " (" + ActivityMenuBulk.Selected_Menu_option.get(position) + ")" );
            }

         //   holder.txtSubTextMax.setPaintFlags(holder.txtSubTextMax.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (ActivityMenuBulk.Menu_price.get(position) < ActivityMenuBulk.Menu_max_price.get(position)) {
                // holder.txtSubText.setVisibility(View.VISIBLE);
                holder.txtSubTextMax.setVisibility(View.VISIBLE);
                holder.txtSubTextMax.setText(ActivityMenuBulk.Currency + " " + ActivityMenuBulk.Menu_max_price.get(position) + " ");
            } else {
                holder.txtSubTextMax.setVisibility(View.VISIBLE);
            }

            holder.txtSubTextMax.setPaintFlags(holder.txtSubTextMax.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

         //   holder.txtSubTextMax.setText("AVAILABLE");
            holder.txtSubTextMax.setVisibility(View.GONE);

          //  holder.txtSubTextMax.setTextColor(Color.parseColor("#00A800"));


        }

            imageLoader.DisplayImage(Constant.AdminPageURL+ActivityMenuBulk.Menu_image.get(position), holder.imgThumb);

        holder.imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iDetail = new Intent(activity, ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", ActivityMenuBulk.Menu_ID.get(position));
                iDetail.putExtra("qty", String.valueOf(ActivityMenuBulk.Menu_quantity.get(position)));
                iDetail.putExtra("category_id",ActivityMenuBulk.Category_ID);
                iDetail.putExtra("keyword",ActivityMenuBulk.Keyword);
                iDetail.putExtra("category_name",ActivityMenuBulk.Category_name);

                activity.startActivity(iDetail);
                activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
             //   activity.finish();
            }
        });

        //  holder.txtQty.onSel


        holder.txtQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int pos = (Integer) view.getTag();

                //   EditText txtView = (EditText) view;

                try {
                    //  int c = Integer.parseInt(ActivityMenuList.Menu_quantity.get(pos).toString());
                    // int c = Integer.parseInt(ActivityMenuList.Menu_quantity.get(pos).toString());
                    //  c++;

                    //  Toast.makeText(cView.getContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();
                    ActivityMenuBulk.Menu_quantity.set(pos, Integer.parseInt(((EditText) view).getText().toString()) );
                    AdapterMenuBulk.this.notifyDataSetChanged();
                } catch (Exception ex) {

                    Toast.makeText(cView.getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

     /*   holder.txtQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {



            @Override
            public void onFocusChange(View view, boolean b) {

            }
        }); */
        holder.imgAdd.setTag(position);
        holder.imgRemove.setTag(position);
        holder.txtQty.setTag(position);
        holder.txtSelectedOption.setTag(position);
        holder.btnChooseOption.setTag(position);

        //holder.txtQty.setText(String.valueOf(ActivityMenuBulk.Menu_quantity.get((Integer) holder.imgAdd.getTag())));

        holder.btnChooseOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  final int pos = (Integer) view.getTag();

                final int pos = (Integer) view.getTag();

              //  String szs = ActivityMenuBulk.Menu_Sizes.get(pos);

               if(true) {

                  final String[] sz = ActivityMenuBulk.Menu_Sizes.get(pos).split(",");

                   final String[] pr = ActivityMenuBulk.Menu_Prices.get(pos).split(",");

                   String[] options = new String[sz.length];



                   for (int i = 0; i < sz.length; i++) {

                       options[i] = sz[i] + " - Rs. " + pr[i];

                   }



                   AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                   builder.setTitle("Choose Options");
                   builder.setItems(options, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int item) {
                           // Do something with the selection

                           final int id = item;

                           DecimalFormat formatData = new DecimalFormat("#.##");


                          holder.txtSelectedOption.setText(sz[id]);
                       //   holder.txtSelectedOption.setVisibility(View.VISIBLE);
                          holder.txtSubText.setText("Rs. "+  Double.valueOf(formatData.format(Double.parseDouble(pr[id]))) + " (" + sz[id] +")");
                          holder.txtQty.setText("0");




                           ActivityMenuBulk.Selected_Menu_ID.set(pos, ActivityMenuBulk.Menu_ID.get(pos) +  Long.parseLong(String.valueOf(id)));
                           ActivityMenuBulk.Selected_Menu_name.set(pos, ActivityMenuBulk.Menu_name.get(pos) + " - " + sz[id]);
                           ActivityMenuBulk.Selected_Menu_price.set(pos, Double.valueOf(formatData.format(Double.parseDouble(pr[id]))));
                           ActivityMenuBulk.Selected_Menu_option.set(pos,sz[id]);
                        //   selected_Menu_ID = Menu_ID+Long.parseLong(String.valueOf(id));
                          // Selected_Menu_name = Menu_name + " - " + sz[id];
                         //  Selected_Menu_price = Long.parseLong(pr[id]);






                       }
                   });
                   AlertDialog alert = builder.create();
                   alert.show();

               }



            }
        });


        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                try {
                    //  int c = Integer.parseInt(ActivityMenuList.Menu_quantity.get(pos).toString());
                    int c = Integer.parseInt(ActivityMenuBulk.Menu_quantity.get(pos).toString());
                    c++;
                    AdapterMenuBulk.this.notifyDataSetChanged();
                    //  Toast.makeText(cView.getContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();


                    ActivityMenuBulk.Menu_quantity.set(pos, c);

                    try{
                        dbhelper.openDataBase();
                    }catch(SQLException sqle){
                        throw sqle;
                    }
                    try {
                        boolean isZero = true;

                        // add cart
                        if (ActivityMenuBulk.Menu_quantity.get(pos)!=0) {
                            isZero = false;
                            if (dbhelper.isDataExist(ActivityMenuBulk.Selected_Menu_ID.get(pos))) {
                                if(ActivityMenuBulk.Menu_quantity.get(pos)==0) {
                                    dbhelper.deleteData(ActivityMenuBulk.Selected_Menu_ID.get(pos));
                                } else {
                                    //dbhelper.updateData(Menu_ID, quantity, (Menu_price*quantity));
                                    dbhelper.updateData(ActivityMenuBulk.Selected_Menu_ID.get(pos), ActivityMenuBulk.Menu_quantity.get(pos), (ActivityMenuBulk.Selected_Menu_price.get(pos) * ActivityMenuBulk.Menu_quantity.get(pos)));
                                }

                                // Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            } else {
                                dbhelper.addData(ActivityMenuBulk.Selected_Menu_ID.get(pos), ActivityMenuBulk.Selected_Menu_name.get(pos), ActivityMenuBulk.Menu_quantity.get(pos), (ActivityMenuBulk.Selected_Menu_price.get(pos) * ActivityMenuBulk.Menu_quantity.get(pos)));
                                //Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            }
                        }




                        //mla.notifyDataSetChanged();
                    } catch(Exception ex) {
                        Toast.makeText(activity,ex.toString(),Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {

                    Toast.makeText(cView.getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
                activity.invalidateOptionsMenu();

            }
        });

        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                try {
                    int c = Integer.parseInt(ActivityMenuBulk.Menu_quantity.get(pos).toString());
                    if (c != 0) {
                        c--;

                    }

                    AdapterMenuBulk.this.notifyDataSetChanged();
                    //  Toast.makeText(cView.getContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();
                    ActivityMenuBulk.Menu_quantity.set(pos, c);
                   // ActivityMenuBulk.Menu_quantity.set(pos, c);

                    try{
                        dbhelper.openDataBase();
                    }catch(SQLException sqle){
                        throw sqle;
                    }
                    try {
                        boolean isZero = true;

                        // add cart
                        if (ActivityMenuBulk.Menu_quantity.get(pos)>=0) {
                            isZero = false;
                            if (dbhelper.isDataExist(ActivityMenuBulk.Selected_Menu_ID.get(pos))) {

                                if(ActivityMenuBulk.Menu_quantity.get(pos)==0) {
                                    dbhelper.deleteData(ActivityMenuBulk.Selected_Menu_ID.get(pos));
                                } else {
                                    //dbhelper.updateData(Menu_ID, quantity, (Menu_price*quantity));
                                    dbhelper.updateData(ActivityMenuBulk.Selected_Menu_ID.get(pos), ActivityMenuBulk.Menu_quantity.get(pos), (ActivityMenuBulk.Selected_Menu_price.get(pos) * ActivityMenuBulk.Menu_quantity.get(pos)));
                                }

                                // Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            } else {
                                dbhelper.addData(ActivityMenuBulk.Selected_Menu_ID.get(pos), ActivityMenuBulk.Selected_Menu_name.get(pos), ActivityMenuBulk.Menu_quantity.get(pos), (ActivityMenuBulk.Selected_Menu_price.get(pos) * ActivityMenuBulk.Menu_quantity.get(pos)));
                                //Toast.makeText(ActivityMenuDetail.this,"Product Added in your Cart.",Toast.LENGTH_LONG).show();
                            }
                        }




                        //mla.notifyDataSetChanged();
                    } catch(Exception ex) {
                        Toast.makeText(activity,ex.toString(),Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {

                    Toast.makeText(cView.getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
                activity.invalidateOptionsMenu();

            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtText, txtSubText, txtSubTextMax, txtTextHindi, txtSelectedOption;
        EditText txtQty;
        ImageView imgThumb, imgAdd, imgRemove;
        Button btnChooseOption;
        LinearLayout lytOptions;
    }


}