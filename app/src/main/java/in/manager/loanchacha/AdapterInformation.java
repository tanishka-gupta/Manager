package in.manager.loanchacha;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterInformation extends FragmentPagerAdapter {


    public AdapterInformation(FragmentManager fm) {
        super(fm);
    }




    @Override
    public Fragment getItem(int index) {


        Bundle bundle = new Bundle();
        int imgResId = 0;
        String tab = "";
        int colorResId = 0;



  /*     if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } */
     //  parseJSONData();
//        new getDataTask().execute();

     //   Constant ct = new Constant();
        switch (index) {
            case 0:
            	tab = Constant.Payment;
                break;
            case 1:
            	tab = Constant.Refund;
                break;
            case 2:
            	tab = Constant.Cancel;
                break;
        }
        bundle.putInt("image", imgResId);
        bundle.putString("tab", tab);
        bundle.putInt("color", colorResId);
        SwipeTabFragment swipeTabFragment = new SwipeTabFragment();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

 /*   public void parseJSONData(){

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(basicDetailApi);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();


            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            String str = "";
            while ((line = in.readLine()) != null){
                str += line;
            }

            // parse json data and store into tax and currency variables
            JSONObject json = new JSONObject(str);
            JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject menu = object.getJSONObject("Basic_detail");

                Payment  = menu.getString("payment");
                Cancel = menu.getString("cancellation");
                Log.w("Payment",Payment);
                Refund = menu.getString("refund");
                About = menu.getString("about");
                Contact = menu.getString("contact");
            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            IOConnect = 1;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class getDataTask extends AsyncTask<Void, Void, Void> {

        // show progressbar first
        getDataTask(){
           // if(!prgLoading.isShown()){
            //    prgLoading.setVisibility(0);

            //}
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            parseJSONData();
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // when finish parsing, hide progressbar
           // prgLoading.setVisibility(8);
            // if internet connection and data available show data
            // otherwise, show alert text
            if((Contact != null) && IOConnect == 0){
                // sclDetail.setVisibility(0);

                // imageLoader.DisplayImage(Constant.AdminPageURL+Menu_image, imgPreview);

                //  txtText.setText(Menu_name);
                //  txtSubText.setText("" + ActivityMenuList.Currency + Menu_price+" "+"\n");
                //    txtSubText.setVisibility(View.INVISIBLE);
                //   txtDescription.loadDataWithBaseURL("", Menu_description, "text/html", "UTF-8", "");

                // txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));
            }else{
                // txtAlert.setVisibility(0);
            }
        }
    } */
}