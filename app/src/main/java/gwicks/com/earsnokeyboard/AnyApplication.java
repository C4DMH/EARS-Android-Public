package gwicks.com.earsnokeyboard;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static android.os.Build.VERSION.SDK_INT;
/**
        * Created by gwicks on 6/10/2017.
        * This class provides a static context if need anywhere in the Application. Currently this Class is NOT used
        * anywhere
        */


public class AnyApplication extends Application {

    //private static Context context;
    private static final String TAG = "AnyApplication";
    private static AnyApplication instance;
    private SharedPreferences mSharedPreferences;

    //private String secureID;

    public void onCreate(){
        Log.d(TAG, "onCreate: anyapplication oncreate");



        super.onCreate();

        //Constants.awsBucket = "neuroteentest-study"; //TODO remove
        //Constants.awsBucket = "earstest"; //TODO remove

//        secureID = Settings.Secure.getString(
//                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        //context = this ;
        instance = this;


        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        Log.d(TAG, "onCreate: instance = " + instance);

        if(Constants.awsBucket == null){
            Log.d(TAG, "onCreate: setting name");
            setBucketName();
        }
        if(Constants.deviceID == null){
            setDeviceID();
        }
        if(Constants.androidVersion == 0){
            setAndroidVersion();
        }
        if(Constants.earsVersion == null){
            setEarsVersion();
        }
        if(Constants.modelName == null){
            setModelName();
        }
        if(Constants.modelNumber == null){
            setModelNumber();
        }
        if(Constants.site == null){
            setSite();
        }
        if(Constants.study == null){
            setStudyName();
        }
        if(Constants.studyName == null){
            setStudyName();
        }

        if(Constants.secureID == null){
            Constants.secureID = Settings.Secure.getString(
                    AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if(Constants.encryption == null){
            if((Constants.awsBucket.equals("maps-study")) || (Constants.awsBucket.equals("conte-study"))|| (Constants.awsBucket.equals("pms-study") )){
                Constants.encryption = true;
            }else{
                Constants.encryption = false;
                Log.d(TAG, "onCreate: encryption is switched off");
            }
        }

        //setDeactivated();
    }

    public static AnyApplication getInstance() {
        Log.d(TAG, "getInstance: getting instance");
        Log.d(TAG, "getInstance: instance = " + instance);
        return instance;
    }

    public void setBucketName(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String s = mSharedPreferences.getString("bucket", "earstest");
        Log.d(TAG, "setBucketName: getting bucket: " + s);
        Constants.awsBucket = s;
        //Constants.awsBucket = "UNC";
        Log.d(TAG, "setBucketName: bucket: " + Constants.awsBucket);


    }

    public void setStudyName(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String s = mSharedPreferences.getString("studyName", "test");
        Constants.study = s;
    }

    public void setDeviceID(){
        String secureID = Settings.Secure.getString(
                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        Constants.deviceID = secureID;
    }

    public void setAndroidVersion(){
        Constants.androidVersion = SDK_INT;
        Log.d(TAG, "setAndroidVersion: " + Constants.androidVersion);
    }

    public void setEarsVersion(){

        String s;

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            Constants.earsVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setEarsVersion: " + Constants.earsVersion);



    }

    public void setModelName(){
       Constants.modelName = MANUFACTURER;
        Log.d(TAG, "setModelName: " + Constants.modelName);
    }
    public void setModelNumber(){
        Constants.modelNumber = MODEL;
        Log.d(TAG, "setModelNumber:  " + Constants.modelNumber);
    }

    public void setSite(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String s = mSharedPreferences.getString("studySite", "New York");

        Constants.site = s;
    }

    public void setDeactivated(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean aBoolean = mSharedPreferences.getBoolean("deactivated", false);

        Constants.deactivated = aBoolean;
        Constants.setDeactivated();
        Log.d(TAG, "setDeactivated: deactivated set to: " + aBoolean);

    }




//    public String getSecureID(){
//        return secureID;
//    }
}
