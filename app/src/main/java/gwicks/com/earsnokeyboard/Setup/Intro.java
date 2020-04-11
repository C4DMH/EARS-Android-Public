package gwicks.com.earsnokeyboard.Setup;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import gwicks.com.earsnokeyboard.BuildConfig;
import gwicks.com.earsnokeyboard.R;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * Created by gwicks on 21/01/2018.
 * First step in installion of EARS tool
 * It should check to see if tool is already installed and if so move to final step,
 * it should also check if each step is completed, and automatically move to the proper
 * place, that is not working however, due to a weirdness with LG phones
 * TODO: fix the above
 */
public class Intro extends AppCompatActivity {


    Context mContext;

    private static final String TAG = "Intro";

    public boolean appUsage = false;
    public boolean keyboardInstalled = false;
    public boolean keyboardSelected = false;
    public boolean notificationListener = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_step_one);
        updateStatusBarColor("#07dddd");
        mContext = this;

        Calendar cal = Calendar.getInstance();
        int doy = cal.get(Calendar.DAY_OF_YEAR);

        checkPrefs(doy);
        setSpecialPrefs(doy);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String path = getExternalFilesDir(null) + "/InstallInfo/";

        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

//        String desination = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sleep/";
//        File destination = new File(desination);
//
//        if(!destination.exists()){
//            destination.mkdirs();
//        }

        File installFile = new File(path + "InstallFile.txt");


        String deviceMan = MANUFACTURER;

        Log.d(TAG, "onResume: deviceMan = " + deviceMan);

        String deviceModel = MODEL;
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String versionFlavor = BuildConfig.FLAVOR;


        writeToFile(installFile, deviceMan + "," + deviceModel +"," + versionCode +"," + versionName + "," + versionName + "," + versionName + "," + versionFlavor);

        // THis is causing issues now we have study code, removing on 15th Feb 2020, hopefully it does not cause issues
//        if(deviceMan.equals("LGE")){ // LG bug, so cant step through to end
//            moveToNextStep();
//        }
        // If we have access to usage stats, move to final step as app has previously been installed
        if(isAccessGranted()) {
            moveToFinalStep();
        }else if(passedStudyCodeCheck()){
            movetoStepTwo();
        }
        // Otherwise, install from the beginning
        else {
            moveToNextStep();
        }
    }

    public void moveToNextStep(){
        Log.d(TAG, "moveToNextStep: ");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this, SetupStepOne.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Intro.this.startActivity(intent);
                finish();
            }
        }, 6000);

    }

    public void movetoStepTwo(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this, SetupStepTwo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Intro.this.startActivity(intent);
                finish();
            }
        }, 6000);

    }

    public void moveToFinalStep(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this, FinishInstallScreen.class);
                Intro.this.startActivity(intent);
            }
        }, 6000);

    }

    public boolean passedStudyCodeCheck(){

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bool = prefs.getBoolean("StudyCodeOK", false);
        Log.d(TAG, "passedStudyCodeCheck: in passed Study Code check, the value is: " + bool);
        return bool;

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public boolean isAccessGranted() {
        try {
            Log.d(TAG, "isAccessGranted: in isaccessgranted");

            PackageManager packageManager = this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 1;
            if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
                Log.d(TAG, "isAccessGranted: usage stats = " + AppOpsManager.MODE_ALLOWED );
                Log.d(TAG, "isAccessGranted: ??");
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            Log.d(TAG, "isAccessGranted: mode = " + mode);
            Log.d(TAG, "isAccessGranted: mode : " + mode + "appopsmanager = " + AppOpsManager.MODE_ALLOWED);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "isAccessGranted: name not found");
            return false;
        }
    }


    public boolean checkNotificationEnabled() {
        try{
            Log.d(TAG, "checkNotificationEnabled: in try");
            if(Settings.Secure.getString(this.getContentResolver(),
                    "enabled_notification_listeners").contains(this.getApplication().getPackageName()))
            {
                Log.d(TAG, "checkNotificationEnabled: in true");

                Log.d(TAG, "checkNotificationEnabled: true");
                return true;
            } else {

                Log.d(TAG, "checkNotificationEnabled: ruturn false");
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "checkNotificationEnabled: Did not get into settings?");
        return false;
    }

    public static void writeToFile(File file, String data) {

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPrefs(int doy){
        Log.d(TAG, "checkPrefs: checking prefs");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        
        if(prefs.contains("doy")){
            Log.d(TAG, "checkPrefs: contains doy already, skipping");

        }else{
            Log.d(TAG, "checkPrefs: does not contain doy, adding!");
            editor.putInt("doy", doy);
            editor.apply();
        }
       
    }

    public void setSpecialPrefs(int  doy){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "setSpecialPrefs: NotFirst Install = " + prefs.getBoolean("NotFirstInstall", false));
        if(prefs.getBoolean("NotFirstInstall", false)== true){
            Log.d(TAG, "setSpecialPrefs: firstinstall must be true");
        }else{
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("NotFirstInstall", true);
            editor.putInt("doy", doy);
            editor.apply();
            Log.d(TAG, "setSpecialPrefs: the doy is: " + doy);

        }

    }
}

