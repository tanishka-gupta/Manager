package in.manager.loanchacha;


import com.onesignal.OneSignal;

public final class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/proxima.ttf");




    }


}
