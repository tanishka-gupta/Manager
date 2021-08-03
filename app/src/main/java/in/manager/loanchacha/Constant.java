package in.manager.loanchacha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import android.os.StrictMode;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Constant {

    // staging = https://appadmin.loanchacha.com
    // live =  https://appadmin.loanchacha.com/
	// API URL configuration
	static String AdminPageURL = "https://appadmin.loanchacha.com";
	static String CategoryAPI = "https://appadmin.loanchacha.com/api/get-all-category-data";
	static String MenuAPI = "https://appadmin.loanchacha.com/api/get-product-data-by-category-id";
    static String NoticesAPI = "https://appadmin.loanchacha.com/api/get-notices-data-by-category-id";
    static String SchedulesAPI = "https://appadmin.loanchacha.com/api/get-schedules-data-by-category-id";
    static String SearchAPI = "https://appadmin.loanchacha.com/api/get-product-data-by-keyword";
    static String SliderAPI = "https://appadmin.loanchacha.com/api/get-slider";
    static String PartnerSliderAPI = "https://appadmin.loanchacha.com/api/get-partner-slider2";

    static String NotificationsAPI = "https://appadmin.loanchacha.com/api/get-notifications-partner";

    static String EntCategoryAPI = "http://appadmin.loanchacha.com/api/get-greeting-category-data";
    static String EntMenuAPI = "http://appadmin.loanchacha.com/api/get-greeting-data-by-category-id";
    static String EntDetailAPI = "http://appadmin.loanchacha.com/api/get-greeting-detail";

    static String EntSliderAPI = "http://appadmin.loanchacha.com/api/get-greeting-slider";


	static String TaxCurrencyAPI = "https://appadmin.loanchacha.com/api/get-tax-and-currency";
	static String MenuDetailAPI = "https://appadmin.loanchacha.com/api/get-product-detail2";
    static String NoticesDetailAPI = "https://appadmin.loanchacha.com/api/get-notices-detail";
    static String SchedulesDetailAPI = "https://appadmin.loanchacha.com/api/get-schedules-detail";
	static String SendDataAPI = "https://appadmin.loanchacha.com/api/add-order";
    static String SendEnquiryAPI = "https://appadmin.loanchacha.com/api/add-enquiry";
    static String SendServiceEnquiryAPI = "https://appadmin.loanchacha.com/api/add-service-enquiry";
    static String SendContactAPI = "https://appadmin.loanchacha.com/api/add-contact";
    static String AddLeadAPI = "https://appadmin.loanchacha.com/api/add-lead";

    static String BasicDetailAPI = "https://appadmin.loanchacha.com/api/get-basic-detail";
    static String LoginDataAPI = "https://appadmin.loanchacha.com/api/login";
    static String DirectoryDetailAPI = "https://appadmin.loanchacha.com/api/get-directory-detail";
    static String MenuSliderAPI = "https://appadmin.loanchacha.com/api/get-menu-slider";

    static String ForgotAPI = "https://appadmin.loanchacha.com/api/get-partner-password";


    static String EventDetailAPI = "https://appadmin.loanchacha.com/api/get-event-detail";

    static String DirectoryMenuAPI = "https://appadmin.loanchacha.com/api/get-directory-data-by-category-id";
    static String DirectoryCountAPI = "https://appadmin.loanchacha.com/api/get-directory-count-api";


    static String SendRegisterAPI = "https://appadmin.loanchacha.com/api/add-user";
    static String SendSignUpAPI = "https://appadmin.loanchacha.com/api/add-client";
    static String LoginClientAPI = "https://appadmin.loanchacha.com/api/login-partner";

    static String BlogCategoryAPI = "https://appadmin.loanchacha.com/api/get-all-blog-category-data";
    static String BlogAPI = "https://appadmin.loanchacha.com/api/get-blog-data-by-category-id";
    static String BlogDetailAPI = "https://appadmin.loanchacha.com/api/get-blog-detail";
    static String EventAPI = "https://appadmin.loanchacha.com/api/get-event-data-by-category-id";


    static String GalleryCategoryAPI = "https://appadmin.loanchacha.com/api/get-gallery-category-data";
    static String VideoGalleryCategoryAPI = "https://appadmin.loanchacha.com/api/get-video-gallery-category-data";

    static String GalleryAPI = "https://appadmin.loanchacha.com/api/get-gallery-data-by-category-id";
    static String VideoGalleryAPI = "https://appadmin.loanchacha.com/api/get-video-gallery-data-by-category-id";
    static String GalleryDetailAPI = "https://appadmin.loanchacha.com/api/get-gallery-detail";
    static String VideoGalleryDetailAPI = "https://appadmin.loanchacha.com/api/get-video-gallery-detail";

    static String DevelopersAPI = "https://appadmin.loanchacha.com/api/get-developers";
    static String FormsAPI = "https://appadmin.loanchacha.com/api/get-forms";
    static String FeesAPI = "https://appadmin.loanchacha.com/api/get-fees";
    static String CrisisAPI = "https://appadmin.loanchacha.com/api/get-crisis";
    static String CampsAPI = "https://appadmin.loanchacha.com/api/get-camps";
    static String FaqAPI = "https://appadmin.loanchacha.com/api/get-faq";

    static String DownloadsAPI = "https://appadmin.loanchacha.com/api/get-downloads-data-by-category-id";

    static String AllLeadsAPI = "https://appadmin.loanchacha.com/api/get-all-leads";
    static String AllFreelancersAPI = "https://appadmin.loanchacha.com/api/get-all-freelancers";


    static String AddLeadsAPI = "https://appadmin.loanchacha.com/api/add-lead";
    static String CreateOrderAPI = "https://appadmin.loanchacha.com/api/add-order";
    static String ViewOrderAPI = "https://appadmin.loanchacha.com/api/view-orders";
    static String MyProfileAPI = "https://appadmin.loanchacha.com/api/my-profile";
    static String MyEarningsAPI = "https://appadmin.loanchacha.com/api/my-earnings";
    static String MyVisitingCardAPI = "https://appadmin.loanchacha.com/api/my-visiting-card";
    static String GreetingsAPI = "https://appadmin.loanchacha.com/api/greetings";
    static String OrderDetailAPI = "https://appadmin.loanchacha.com/api/order-details";

    static String AddFreelancerAPI = "https://appadmin.loanchacha.com/api/add-freelancer";

    static String JoinFreelancerAPI = "https://appadmin.loanchacha.com/api/join-freelancer";


    static String Payment;
    static String Cancel;
     static String Refund;
    static String About;
    static String Facebook;
    static String Instagram;
    static String Locate;
    static String Youtube;
     static String Contact;
    static String ThoughtHeading;
    static String Thought;
    static String NoticeHeading;
    static String Notice;
    static double DeliveryLimit;
    static String Helpline;
    static String Developer;
    static String Welcome;
    static String Paytm;
    static String Bhim;
    static String UserType;
    static String UserId;

    static String DefaultDetails;

    static int IOConnect=0;
	
	// change this access similar with accesskey in admin panel for security reason
	static String AccessKey = "12345";
	
	// database path configuration
	static String DBPath = "/data/data/in.manager.loanchacha/databases/";


	
	// method to check internet connection
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        /* get basic details */
                        if (android.os.Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }

                       // if(Cancel==null || About == null || Contact == null || Payment == null || Helpline == null  ) {

                            parseJSONData();

                       // }
						return true;
					}
				}
			}
		}
		return false;
	}

	// method to handle images from server
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }


    public static void parseJSONData(){

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(BasicDetailAPI+"?accesskey="+AccessKey);
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

               //setPayment(menu.getString("payment")) ;
               Cancel = menu.getString("cancellation");
               // Log.w("Payment", Payment);
                Refund = menu.getString("refund");
                Welcome = menu.getString("welcome_msg");
                About = menu.getString("about");
                Contact = menu.getString("contact");
                Payment = menu.getString("payment");
                Helpline = menu.getString("helpline");
                DeliveryLimit = menu.getDouble("del_limit");
                Facebook = menu.getString("facebook");
                Instagram = menu.getString("faq");
                Locate = menu.getString("camps");
                Youtube = menu.getString("youtube");
                NoticeHeading = menu.getString("notice_heading");
                Notice = menu.getString("notice");
                Thought = menu.getString("thought");
                DefaultDetails = menu.getString("thought");
                ThoughtHeading = menu.getString("thought_heading");
                Paytm = menu.getString("paytm");
                Bhim = menu.getString("bhim");
                Developer = menu.getString("forms");


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

}
