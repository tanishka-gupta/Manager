package in.manager.loanchacha;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.graphics.Color;

public class SwipeTabFragment4 extends Fragment {

    private String tab;
    private int color;
    private int image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        image = bundle.getInt("image");
        tab = bundle.getString("tab");
        color = bundle.getInt("color");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipetab_fragment, null);

        ImageView tabImage = (ImageView) view.findViewById(R.id.imageView);
        tabImage.setImageResource(image);



        WebView wv = (WebView) view.findViewById(R.id.webDescription);


        //   wv.loadDataWithBaseURL("", "", "text/html", "UTF-8", "");
        //     wv.setWebChromeClient(new WebChromeClient());

        wv.setBackgroundColor(Color.parseColor("#ffffff"));
        wv.getSettings().setJavaScriptEnabled(true);
        //webSlider.loadUrl(Constant.SliderAPI+"?accesskey="+Constant.AccessKey);
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.loadUrl(Constant.DevelopersAPI+"?accesskey="+Constant.AccessKey);



        view.setBackgroundResource(color);
        return view;
    }
}