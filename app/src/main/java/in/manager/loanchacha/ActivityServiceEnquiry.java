package in.manager.loanchacha;


import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



public class ActivityServiceEnquiry extends FragmentActivity {
    static String Currency = null;
    static final String DATE_DIALOG_ID = "datePicker";
    static double Delivery = 0.0d;
    static final String TIME_DIALOG_ID = "timePicker";
    static double Tax = 0.0d;
    public static final String UPLOAD_KEY = "image";
    public static final String UPLOAD_URL = (Constant.SendServiceEnquiryAPI + "?type=image");
    static DBHelper dbhelper;
    private static int mDay;
    private static int mHour;
    private static int mMinute;
    private static int mMonth;
    private static int mYear;
    String Alamat;
    String Amount;
    String Category;
    ArrayList<String> CategoryId;
    ArrayList<String> CategoryList;
    String Comment = "";
    String Date_n_Time;
    String Email;
    int IOConnect = 0;
    String Name;
    String Name2;
    private int PICK_IMAGE_REQUEST = 1;
    String Phone;
    private int REQUEST_CAMERA = 0;
    String Result;
    private int SELECT_FILE = 1;
    String TaxCurrencyAPI;
    private Bitmap bitmap;
    Button btnSend;
    private Button buttonChoose;
    ArrayList<ArrayList<Object>> data;
    EditText edtAlamat;
    EditText edtAmountOnline;
    EditText edtComment;
    EditText edtEmail;
    EditText edtName;
    EditText edtName2;
    EditText edtPhone;
    private Uri filePath;
    DecimalFormat formatData = new DecimalFormat("#.##");
    private ImageView imageView;
    ProgressBar prgLoading;
    ScrollView sclDetail;
    Spinner spinner;
    Spinner spinnerCategory;
    TextView txtAlert;
    TextView txtAmountHeading;

    public class getDataTask extends AsyncTask<Void, Void, Void> {
        getDataTask() {
        }

        protected Void doInBackground(Void... arg0) {
            ActivityServiceEnquiry.this.parseJSONData();
            return null;
        }

        protected void onPostExecute(Void result) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter(ActivityServiceEnquiry.this, android.R.layout.simple_spinner_item, ActivityServiceEnquiry.this.CategoryList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ActivityServiceEnquiry.this.spinnerCategory.setAdapter(dataAdapter);
            if (ActivityServiceEnquiry.this.IOConnect != 0) {
            }
        }
    }

    public class sendData extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(ActivityServiceEnquiry.this, "", ActivityServiceEnquiry.this.getString(R.string.sending_alert), true);

        }

        protected Void doInBackground(Void... params) {
            ActivityServiceEnquiry.this.Result = ActivityServiceEnquiry.this.getRequest(ActivityServiceEnquiry.this.Name, ActivityServiceEnquiry.this.Alamat, ActivityServiceEnquiry.this.Phone, ActivityServiceEnquiry.this.Email, ActivityServiceEnquiry.this.Comment, ActivityServiceEnquiry.this.Category, ActivityServiceEnquiry.this.Name2);
            return null;
        }

        protected void onPostExecute(Void result) {
            this.dialog.dismiss();
            ActivityServiceEnquiry.this.resultAlert(ActivityServiceEnquiry.this.Result);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, c.get(1), c.get(2), c.get(5));
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ActivityServiceEnquiry.mYear = year;
            ActivityServiceEnquiry.mMonth = month;
            ActivityServiceEnquiry.mDay = day;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(11);
            int minute = c.get(12);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ActivityServiceEnquiry.mHour = hourOfDay;
            ActivityServiceEnquiry.mMinute = minute;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_enquiry);
        this.buttonChoose = (Button) findViewById(R.id.buttonChoose);
        this.spinnerCategory = (Spinner) findViewById(R.id.categorySpinner);
        this.spinnerCategory.setVisibility(8);
        this.spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityServiceEnquiry.this.Category = ((String) ActivityServiceEnquiry.this.CategoryId.get(i)).toString();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
        this.buttonChoose.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ActivityServiceEnquiry.this.selectImage();
            }
        });
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Send Complaint / Enquiry");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
      //  this.Category = getIntent().getStringExtra("category");

        this.Category = "Service";
        this.edtName = (EditText) findViewById(R.id.edtName_enq);
        this.edtName2 = (EditText) findViewById(R.id.edtName2);
        this.edtPhone = (EditText) findViewById(R.id.edtPhone_enq);
        this.edtEmail = (EditText) findViewById(R.id.edtEmail_enq);
        this.edtComment = (EditText) findViewById(R.id.edtComment_enq);
        this.btnSend = (Button) findViewById(R.id.btnSend_enq);
        this.edtAlamat = (EditText) findViewById(R.id.edtAlamat_enq);



        this.edtName2.setText("Pay At Spot");
        if (this.Category.contains("Service")) {
            bar.setTitle("Service Enquiry");
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pay_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        ActivityServiceEnquiry.this.edtName2.setText("Pay At Spot");


                        return;
                    case 1:
                        ActivityServiceEnquiry.this.edtName2.setText("Pay Online (Instamojo)");


                        return;
                    case 2:
                        ActivityServiceEnquiry.this.edtName2.setText("Pay Online (PayuMoney)");


                        return;
                    default:
                        ActivityServiceEnquiry.this.edtName2.setText("Pay Online (Instamojo)");
                        ActivityServiceEnquiry.this.edtAmountOnline.setVisibility(0);
                        ActivityServiceEnquiry.this.txtAmountHeading.setVisibility(0);
                        ActivityServiceEnquiry.this.edtAmountOnline.setText("");
                        return;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (this.Category.contains("In Warranty Service")) {
            spinner.setVisibility(8);
        } else if (this.Category.contains("Free Service")) {
            spinner.setVisibility(8);
            this.edtEmail.setVisibility(8);
        } else {
            spinner.setVisibility(0);
        }
        this.btnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ActivityServiceEnquiry.this.Name = ActivityServiceEnquiry.this.edtName.getText().toString();
                ActivityServiceEnquiry.this.Alamat = ActivityServiceEnquiry.this.edtAlamat.getText().toString();
                ActivityServiceEnquiry.this.Phone = ActivityServiceEnquiry.this.edtPhone.getText().toString();
                ActivityServiceEnquiry.this.Name2 = ActivityServiceEnquiry.this.edtName2.getText().toString();
                ActivityServiceEnquiry.this.Email = ActivityServiceEnquiry.this.edtEmail.getText().toString();
                ActivityServiceEnquiry.this.Comment = ActivityServiceEnquiry.this.edtComment.getText().toString();
                ActivityServiceEnquiry.this.Amount = "0";
                if (ActivityServiceEnquiry.this.Name.equalsIgnoreCase("") || ActivityServiceEnquiry.this.Alamat.equalsIgnoreCase("") || ActivityServiceEnquiry.this.Category.equalsIgnoreCase("") || ActivityServiceEnquiry.this.Phone.equalsIgnoreCase("") || ActivityServiceEnquiry.this.Comment.equalsIgnoreCase("")) {
                    Toast.makeText(ActivityServiceEnquiry.this, R.string.form_alert, 0).show();
                    if (ActivityServiceEnquiry.this.Name.equalsIgnoreCase("")) {
                        ActivityServiceEnquiry.this.edtName.setBackgroundResource(R.drawable.txt_bg);
                    } else {
                        ActivityServiceEnquiry.this.edtName.setBackgroundResource(0);
                    }
                    if (ActivityServiceEnquiry.this.Alamat.equalsIgnoreCase("")) {
                        ActivityServiceEnquiry.this.edtAlamat.setBackgroundResource(R.drawable.txt_bg);
                    } else {
                        ActivityServiceEnquiry.this.edtAlamat.setBackgroundResource(0);
                    }
                    if (ActivityServiceEnquiry.this.Phone.equalsIgnoreCase("")) {
                        ActivityServiceEnquiry.this.edtPhone.setBackgroundResource(R.drawable.txt_bg);
                    } else {
                        ActivityServiceEnquiry.this.edtPhone.setBackgroundResource(0);
                    }
                    if (ActivityServiceEnquiry.this.Comment.equalsIgnoreCase("")) {
                        ActivityServiceEnquiry.this.edtComment.setBackgroundResource(R.drawable.txt_bg);
                        return;
                    } else {
                        ActivityServiceEnquiry.this.edtComment.setBackgroundResource(0);
                        return;
                    }
                }
                new sendData().execute(new Void[0]);
            }
        });
        this.prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        this.txtAlert = (TextView) findViewById(R.id.txtAlert);
        this.sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        this.prgLoading.setVisibility(4);
        new getDataTask().execute(new Void[0]);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectImage() {
        final CharSequence[] items = new CharSequence[]{"Take Photo", "Choose from Library", "Cancel"};
        Builder builder = new Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    ActivityServiceEnquiry.this.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), ActivityServiceEnquiry.this.REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    ActivityServiceEnquiry.this.startActivityForResult(Intent.createChooser(intent, "Select File"), ActivityServiceEnquiry.this.SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("OK")) {
            String url;
            Intent i;
            if (this.edtName2.getText().toString().equals("Pay Online (Instamojo)")) {
                if (Float.parseFloat(this.edtAmountOnline.getText().toString()) != 0.0f && !this.edtAmountOnline.getText().toString().isEmpty()) {
                    url = "http://ishop.uddanpromotions.in/mojo/api.php?amount=" + this.edtAmountOnline.getText().toString() + "&mobile=" + this.Phone + "&name=" + this.Name + "&email=" + this.Email;
                    i = new Intent(this, ActivityPayu.class);
                    i.putExtra("URL", url);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                }
            } else if (!this.edtName2.getText().toString().equals("Pay Online (PayuMoney)")) {
                Toast.makeText(this, R.string.ok_alert, 0).show();
                startActivity(new Intent(this, ActivityConfirmMessage.class));
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();
            } else if (Float.parseFloat(this.edtAmountOnline.getText().toString()) != 0.0f && !this.edtAmountOnline.getText().toString().isEmpty()) {
                url = "http://ishop.uddanpromotions.in/payu/?amount=" + this.edtAmountOnline.getText().toString() + "&phone=" + this.Phone + "&firstname=" + this.Name + "&email=" + this.Email;
                i = new Intent(this, ActivityPayu.class);
                i.putExtra("URL", url);
                startActivity(i);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();
            }
        } else if (HasilProses.trim().equalsIgnoreCase("Failed")) {
            Toast.makeText(this, R.string.failed_enquiry, 0).show();
        } else {
            Log.d("HasilProses", HasilProses);
        }
    }

    public String getRequest(String name, String alamat, String phone, String email, String comment, String category, String name2) {
        String result = "";
        String uploadImage = getStringImage(this.bitmap);
        HashMap<String, String> data = new HashMap();
        data.put(UPLOAD_KEY, uploadImage);
        String response = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();
            if (conn.getResponseCode() == 200) {
                response = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
            } else {
                response = "Error Registering";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.SendServiceEnquiryAPI);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList(9);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("address", alamat));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
            nameValuePairs.add(new BasicNameValuePair("category", category));
            nameValuePairs.add(new BasicNameValuePair("method", name2));
            nameValuePairs.add(new BasicNameValuePair("id", response));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            return request(client.execute(request));
        } catch (Exception e2) {
            return "Unable to connect.";
        }
    }

    public static String request(HttpResponse response) {
        String result = "";
        try {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    str.append(line + "\n");
                } else {
                    in.close();
                    return str.toString();
                }
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        }
        return "0" + String.valueOf(c);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 2) {
            this.bitmap = (Bitmap) data.getExtras().getParcelable("data");
            this.imageView.setImageBitmap(this.bitmap);
            this.imageView.setVisibility(0);
        } else if (requestCode == this.REQUEST_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(CompressFormat.JPEG, 50, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
            try {
                destination.createNewFile();
                FileOutputStream fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                cropCapturedImage(Uri.fromFile(destination));
            } catch (Exception ex) {
                Toast.makeText(this, ex.toString(), 1);
            }
            this.filePath = Uri.fromFile(destination);
            try {
                this.bitmap = Media.getBitmap(getContentResolver(), this.filePath);
                this.imageView.setImageBitmap(this.bitmap);
                this.imageView.setVisibility(0);
            } catch (IOException e22) {
                e22.printStackTrace();
            }
        } else if (requestCode == this.SELECT_FILE) {
            this.filePath = data.getData();
            try {
                cropCapturedImage(this.filePath);
            } catch (Exception ex2) {
                Toast.makeText(this, ex2.toString(), 1).show();
            }
            try {
                this.bitmap = Media.getBitmap(getContentResolver(), this.filePath);
                this.imageView.setImageBitmap(this.bitmap);
                this.imageView.setVisibility(0);
            } catch (IOException e222) {
                e222.printStackTrace();
            }
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode((String) entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public void cropCapturedImage(Uri picUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 2);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 100, baos);

        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    public void parseJSONData() {
        try {
            this.CategoryList = new ArrayList();
            this.CategoryId = new ArrayList();
            this.CategoryId.add("1");
            this.CategoryList.add("Markaz Maszid");
            this.CategoryId.add("2");
            this.CategoryList.add("Madina Maszid");
            this.CategoryId.add("3");
            this.CategoryList.add("Nai Maszid");
            this.CategoryId.add("4");
            this.CategoryList.add("Nurani Maszid");
            this.CategoryId.add("5");
            this.CategoryList.add("Ahmadiya Maszid");
            this.CategoryId.add("6");
            this.CategoryList.add("Sarvodaya Basti");
            this.CategoryId.add("7");
            this.CategoryList.add("Chungi Chowki");
            this.CategoryId.add("8");
            this.CategoryId.add("Other");
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), 1).show();
        }
    }
}
