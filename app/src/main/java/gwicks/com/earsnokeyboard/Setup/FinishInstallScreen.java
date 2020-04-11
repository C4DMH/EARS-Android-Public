package gwicks.com.earsnokeyboard.Setup;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import gwicks.com.earsnokeyboard.AccGryLgt;
import gwicks.com.earsnokeyboard.AnyApplication;
import gwicks.com.earsnokeyboard.Constants;
import gwicks.com.earsnokeyboard.DailyEMAAlarmReceiver;
import gwicks.com.earsnokeyboard.DailyEMAUploadReceiver;
import gwicks.com.earsnokeyboard.EMAAlarmReceiver;
import gwicks.com.earsnokeyboard.EMAUploadReceiver;
import gwicks.com.earsnokeyboard.KeyloggerUploadAlarm;
import gwicks.com.earsnokeyboard.LocationManager;
import gwicks.com.earsnokeyboard.MicRecordUploadAlarm;
import gwicks.com.earsnokeyboard.MusicUploadReceiver;
import gwicks.com.earsnokeyboard.PhotoCropBroadcastReceiver;
import gwicks.com.earsnokeyboard.PhotoUploadReceiver;
import gwicks.com.earsnokeyboard.R;
import gwicks.com.earsnokeyboard.SensorUploadReceiver;
import gwicks.com.earsnokeyboard.StatsAlarmReceiver;
import gwicks.com.earsnokeyboard.StatsJobService;
import gwicks.com.earsnokeyboard.SuicideAlarmReceiver;
import gwicks.com.earsnokeyboard.UploadGPSAlarmReceiver;

/**
 * Created by gwicks on 11/05/2018.
 */

public class FinishInstallScreen extends AppCompatActivity {

    private static final String TAG = "FinishInstallScreen";

    ImageView needToTalkClosed;
    TextView talkText;
    TextView mood;
    ImageView moodCheck;
    ImageView preferences;
    TextView prefText;
    File destroyEvents;
    //TextView  textViewEmail;
    TextView garminConnect;
    WorkManager mWorkManager;
    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;


    // Main Activity variables added 8th Feb 2018

    private PendingIntent alarmIntent;
    private PendingIntent statsIntent;
    private PendingIntent GPSIntent;
    private PendingIntent MicIntent;
    private PendingIntent musicIntent;
    private PendingIntent photoIntent;
    private PendingIntent startEMAIntent;
    private PendingIntent EMAIntent;
    private PendingIntent DailyEMAIntent;
    private PendingIntent sensorIntent;
    private PendingIntent garminIntent;
    private PendingIntent keyloggerIntent;
    private PendingIntent FirebaseEMAIntent;
    private PendingIntent startDailyEMAIntent;
    private PendingIntent startPhotoCropIntent;
    private PendingIntent startBalloonEMAIntent;
    private PendingIntent startFiveMinCMUEMAIntent;
    public static boolean alarmIsSet = false;
    public static boolean statsAlarmIsSet = false;
    public static final String secureID = Settings.Secure.getString(
            AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    SharedPreferences Prefs;
    public String theCurrentDate;

    HashMap mRank = new HashMap();


    public static boolean alarmStarted = false;
    int numberOfInstances = 0;

    private SharedPreferences prefs;
    private static final int REQUEST_WRITE_PERMISSION = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_base);

        Constants.awsBucket = "duke"; //TODO remove



        if(savedInstanceState != null) {
            Log.d(TAG, "onCreate: the activity is being recreated!");
        }

        updateStatusBarColor("#1281e8");
        Log.d(TAG, "onCreate: before FIREBASE");

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.awsBucket);
        Log.d(TAG, "onCreate: FIREBASE subscribed to awsBucket: " + Constants.awsBucket);

        FirebaseMessaging.getInstance().subscribeToTopic(secureID);
        Log.d(TAG, "onCreate: FIREBASE secureID: " + secureID);

        if((Constants.awsBucket.equals("columbia-study"))&& (!checkPermissionForWriteExtertalStorage())){
            Log.d(TAG, "onCreate: requesting permissions for storage");
            ActivityCompat.requestPermissions(FinishInstallScreen.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String s = prefs.getString("bucket", "Defualkty");

        Log.d(TAG, "onCreate: the bucket is from prefs: " + s );

        numberOfInstances++;
        checkForAppUpdate();


        mRank.put(1, "Cotton Tail");
        mRank.put(2, "Flopsy Friend");
        mRank.put(3, "Flower Pot Pupil");
        mRank.put(4, "Cucumber Connoisseur");
        mRank.put(5, "Daikon Dancer");
        mRank.put(6, "Turnip Trader");
        mRank.put(7, "Potato Patron");
        mRank.put(8, "Radish Rebel");
        mRank.put(9, "Leek Loyalist");
        mRank.put(10, "Carrot Captain");
        mRank.put(11, "Artichoke Ace");
        mRank.put(12, "Cabbage Corsair");
        mRank.put(13, "Meadow Master");
        mRank.put(14, "Terragon Tactician");
        mRank.put(15, "Chamomile Commander");
        mRank.put(16, "The Rhubarbarian");
        mRank.put(17, "McGregor's Bain");
        mRank.put(18, "Toolshed Terror");
        mRank.put(19, "Briar's Ire");
        mRank.put(20, "Asparagus Admiral");
        mRank.put(21, "Rutabaga's Roar");
        mRank.put(22, "Fennel's Fervor");
        mRank.put(23, "Blackberry Belt");
        mRank.put(24, "Gooseberry Guardian");
        mRank.put(25, "Artichoke Ancient");
        mRank.put(26, "Onion Oracle");
        mRank.put(27, "Celery Celestial");
        mRank.put(28, "High Sparrow");
        mRank.put(29, "Thyme Traveler");
        mRank.put(30, "Zucchini's Zen");



        // All the extra 7 cups buttons at bottom being removed

//        moodCheck = (ImageView) findViewById(R.id.imageView41);
//        needToTalkClosed = (ImageView) findViewById(R.id.imageView6);
//        talkText = (TextView) findViewById(R.id.textViewTalk);
//        talkText.setVisibility(View.GONE);
//
//        preferences = findViewById(R.id.imageView42);
//        //preferences.setTag(1);
//        prefText = findViewById(R.id.textView2);
//        prefText.setVisibility(View.GONE);
//        prefText.setTag(1);
//
//
//        needToTalkClosed.setTag(1);
//        mood = (TextView) findViewById(R.id.textView1);
//        mood.setTag(1);
//        mood.setVisibility(View.GONE);
//
//        //textViewEmail = (TextView)findViewById(R.id.textViewEmail);
//
//        garminConnect = (TextView)findViewById(R.id.textViewEmail);




//        SpannableString ss = new SpannableString("Get free, anonymous and confidential support at 7 Cups. Listeners available 24/7 to help you feel better\n\nGet the App");
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                launch7cups();
//            }
//
//        };
//        //ss.setSpan(clickableSpan, 48, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(clickableSpan, 106, 117, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        talkText.setText(ss);
//        talkText.setMovementMethod(LinkMovementMethod.getInstance());

//        garminConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: in lauched garmin connect button");
//                //launchSendEmailDialog();
//                Intent myIntent = new Intent(FinishInstallScreen.this, DeviceListActivity.class);
//
//                startActivity(myIntent);
//
//            }
//        });

//
//        needToTalkClosed.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: Clicked");
//
//                if (needToTalkClosed.getTag().equals(1)) {
//                    talkText.setVisibility(View.VISIBLE);
//                    needToTalkClosed.setTag(2);
//
//                } else {
//                    talkText.setVisibility(View.GONE);
//                    needToTalkClosed.setTag(1);
//                }
//            }
//        });
//
//        preferences.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: Clicked moodcheck");
//
//                if (prefText.getTag().equals(1)) {
//                    prefText.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "onClick: visable");
//                    prefText.setTag(2);
//
//                } else {
//                    prefText.setVisibility(View.GONE);
//                    Log.d(TAG, "onClick: invisible");
//                    prefText.setTag(1);
//                }
//            }
//        });
//
//        moodCheck.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: Clicked moodcheck");
//
//                if (mood.getTag().equals(1)) {
//                    mood.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "onClick: visable");
//                    mood.setTag(2);
//
//                } else {
//                    mood.setVisibility(View.GONE);
//                    Log.d(TAG, "onClick: invisible");
//                    mood.setTag(1);
//                }
//            }
//        });

        //Toast.makeText(this, "THE SECURE DEVICE ID IS: " + secureID, Toast.LENGTH_LONG).show();
        if (!isAccessGranted()) {
            try{
                showDialog();
            }catch(Exception e){
                Log.d(TAG, "onCreate: exception: " + e);
            }
        }

        if(!checkNotificationEnabled()){
            try{
                showMusicDialog();
            }catch(Exception e){
                Log.d(TAG, "onCreate: excption: " + e);
            }
        }

        // Added 27th Feb because no daily EMA done


        if(!isAccessibilityEnabled(this, "gwicks.com.earsnokeyboard/.KeyLogger")){
            // do the keyboard thing again.
            launchKeyboardDialog();

        }


        String path2 = (this.getExternalFilesDir(null) + "/DestroyFIS");
        File directory2 = new File(path2);

        if(!directory2.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory2.mkdirs();
        }

        destroyEvents = new File(directory2,  "DestroyEvents.txt");


//        Intent sensors = new Intent(this, AccGryLgt.class);
////        startService(sensors);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            this.startService((new Intent(this, AccGryLgt.class)));
            Log.d(TAG, "onReceive: logging");
        }
        else{
            this.startForegroundService((new Intent(this, AccGryLgt.class)));
            Log.d(TAG, "onReceive: logging");
        }



        startStatsAlarm();
        startMicUploadAlarm();
        startGPSUploadAlarm();
        startMusicUploadAlarm();
        startPhotoUploadAlarm();
        startSensorUploadAlarm();
        //startGarminUploadAlarm();
        startKeyloggerUploadAlarm();
        startDailyEMAAlarm();
        startDailyEMAIntent();
        startPhotoCrop();
        Log.d(TAG, "onCreate: alarmstarted = " + alarmStarted);

        // Comment this out to remove the EMA component - TODO why was this still here?
//        if(alarmStarted != true){
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    Log.d(TAG, "run: in handler, waiting for 10 min");
//                    //startSuicideEMAAlarm();
//                    startEMAAlarm();
//                }
//            }, 1000*60*2);
//
//        }
        Log.d(TAG, "onCreate: aws bucket = " + Constants.awsBucket);

        if(((Constants.awsBucket.equals("maps-study")) || (Constants.awsBucket.equals("earstest")))){
            Log.d(TAG, "onCreate: starting suicide alarm");
            startSuicideEMAAlarm();
        }

        if(Constants.awsBucket.equals("neuroteentest-study") || Constants.awsBucket.equals("neuroteen-study") || Constants.awsBucket.equals("earstest")){ //TODO remove

            Log.d(TAG, "onCreate: aws bucket = " + Constants.awsBucket);
            startFiveMinEMA();

        }else{
            if(alarmStarted != true){
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d(TAG, "run: in handler, waiting for 10 min");
                        //startSuicideEMAAlarm();
                        startEMAAlarm();
                    }
                }, 1000*60*2);
            }
        }

        startEMAUploadAlarm();

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer Size and  rate", "Size :" + size + " & Rate: " + rate);


        final JobInfo job = new JobInfo.Builder(1, new ComponentName(this, StatsJobService.class))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setRequiresCharging(true)
                //.setMinimumLatency(10000)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();
        final JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
        jobScheduler.schedule(job);
        Log.d(TAG, "onCreate: Job Scehduled");

        mWorkManager = WorkManager.getInstance(this);
        PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(LocationManager.class, 15, TimeUnit.MINUTES).build();
        mWorkManager.enqueue(mRequest);

        //throw new RuntimeException("This is a crash");

        // remove this intent 30th october 2017
        //startActivity(new Intent(this, VideoActivity.class));

        setSettingsDone(this);


        // Progress Bar algorithm





    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressBar();
        Log.d(TAG, "onResume: in on Resume, number of instances  = " + numberOfInstances);
        Log.d(TAG, "onResume: bucket name is: " + Constants.awsBucket);

    }

    public void updateStatusBarColor(String color) {// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

//    public void launch7cups() {
//        Log.d(TAG, "launch7cups: clicked");
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("market://details?id=com.sevencupsoftea.app"));
//        startActivity(intent);
//        //https://play.google.com/store/apps/details?id=com.sevencupsoftea.app
//    }


    // 8th Feb 2018, this is first attempt to move the MainActivity and VideoActivity Classes into this final install Activity.

    public void startStatsAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");


        boolean alarmUp = (PendingIntent.getBroadcast(this, 1,
                new Intent(FinishInstallScreen.this, StatsAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "Suicide alarm is up : " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "startStatsAlarm: alarm already up, skipping");
            return;
        }


        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 30);

//        cal.set(Calendar.HOUR_OF_DAY, 16);
//        cal.set(Calendar.MINUTE, 00);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StatsAlarmReceiver.class);
        statsIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, statsIntent);

    }


    public void startMicUploadAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 2,
                new Intent(FinishInstallScreen.this, MicRecordUploadAlarm.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "MicRecord alarm is up : " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "micRecord alarm already up, skipping");
            return;
        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 56);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MicRecordUploadAlarm.class);
        //statsIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        MicIntent = PendingIntent.getBroadcast(this, 2, intent, 0);



        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, MicIntent);


    }

    public void startKeyloggerUploadAlarm(){

        Log.d(TAG, "KeyLogger: in start alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 3,
                new Intent(FinishInstallScreen.this, KeyloggerUploadAlarm.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "KeyLogger alarm is up : " + alarmUp);

//        if(alarmUp){
//            Log.d(TAG, "KeyloggerAlarm: alarm already up, skipping");
//            return;
//        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 48);
//        cal.set(Calendar.HOUR_OF_DAY, 16);
//        cal.set(Calendar.MINUTE, 43);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, KeyloggerUploadAlarm.class);
        //statsIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        keyloggerIntent = PendingIntent.getBroadcast(this, 3, intent, 0);



        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, keyloggerIntent);

    }

    public void startGPSUploadAlarm() {
        Log.d(TAG, "startGPSAlarm: in start alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 4,
                new Intent(FinishInstallScreen.this, UploadGPSAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "startGPSAlarm : " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "startGPSAlarm: alarm already up, skipping");
            return;
        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,49);
//        cal.set(Calendar.HOUR_OF_DAY, 12);
//        cal.set(Calendar.MINUTE,54);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UploadGPSAlarmReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        GPSIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, GPSIntent);

    }

    public void startMusicUploadAlarm() {
        Log.d(TAG, "Music: in start alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 5,
                new Intent(FinishInstallScreen.this, MusicUploadReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "Music upload: " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "startGPSAlarm: alarm already up, skipping");
            return;
        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 50);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MusicUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        musicIntent = PendingIntent.getBroadcast(this, 5, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, musicIntent);

    }

    public void startSensorUploadAlarm() {
        Log.d(TAG, "sensor upload in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 40);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // alarmMgr.setTimeZone();
        Intent intent = new Intent(this, SensorUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        sensorIntent = PendingIntent.getBroadcast(this, 6, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sensorIntent);

    }

    public void startEMAUploadAlarm() {
        Log.d(TAG, "EMA upload in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 41);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, EMAUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        EMAIntent = PendingIntent.getBroadcast(this, 7, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, EMAIntent);


    }

    public void startDailyEMAIntent() {
        Log.d(TAG, "Daily EMA upload in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        //cal.set(Calendar.HOUR_OF_DAY, 17);

        cal.set(Calendar.MINUTE, 42);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, DailyEMAUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        DailyEMAIntent = PendingIntent.getBroadcast(this, 27, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, DailyEMAIntent);


    }


//    public void startGarminUploadAlarm() {
//        Log.d(TAG, "EMA upload in start alarm");
//
//        Calendar cal = Calendar.getInstance();
//        long when = cal.getTimeInMillis();
//        String timey = Long.toString(when);
//
//        //System.out.println("The time changed into nice format is: " + theTime);
//
//        Log.d("the time is: ", when + " ");
//
//        cal.setTimeInMillis(System.currentTimeMillis());
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 15);
//
//        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, GarminUploadReceiver.class);
//        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
//        garminIntent = PendingIntent.getBroadcast(this, 11, intent, 0);
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, garminIntent);
//
//
//    }

    // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startPhotoUploadAlarm() {
        Log.d(TAG, "startPhotoUploadAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 52);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 43);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PhotoUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        photoIntent = PendingIntent.getBroadcast(this, 8, intent, 0);
        //alarmMgr.setExact();

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, photoIntent);
    }

    // This the the once a week suicide check alarm!

    public void startSuicideEMAAlarm(){
        Log.d(TAG, "startSuicideEMAAlarm: in start ema alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 9,
                new Intent(FinishInstallScreen.this, SuicideAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "Suicide alarm is up : " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "startSuicideEMAAlarm: alarm already up, skipping");
            return;
        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 15);
//        cal.set(Calendar.HOUR_OF_DAY, 14);
//        cal.set(Calendar.MINUTE, 25);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SuicideAlarmReceiver.class);
        intent.putExtra("EMA", "EMA1");
        startEMAIntent = PendingIntent.getBroadcast(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 15, startEMAIntent);
        Log.d(TAG, "startEMAAlarm: suicide alarm shjould be set");
        alarmStarted = true;


    }

    // This is for the first 7 days of the EMA alarm

    public void startEMAAlarm(){


        if(Constants.awsBucket.equals("neuroteentest-study")|| Constants.awsBucket.equals("neuroteen-study") || Constants.awsBucket.equals("earstest")){ //TODO remove

        }else{
            Log.d(TAG, "startEMAAlarm: in start ema alarm");

            boolean alarmUp = (PendingIntent.getBroadcast(this, 21,
                    new Intent(FinishInstallScreen.this, EMAAlarmReceiver.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            Log.d(TAG, "Ema alarm boolean alarm up is: " + alarmUp);

            if(alarmUp){
                Log.d(TAG, "startEMAAlarm: alarm already up, skipping");
                return;
            }


            Calendar cal = Calendar.getInstance();
            long when = cal.getTimeInMillis();

            cal.setTimeInMillis(System.currentTimeMillis());
            //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 15);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, EMAAlarmReceiver.class);
            intent.putExtra("EMA", "EMA1");
            startEMAIntent = PendingIntent.getBroadcast(this, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startEMAIntent);
            Log.d(TAG, "startEMAAlarm first 7 days: alarm should be set");
            //alarmStarted = true;
        }
        Log.d(TAG, "startEMAAlarm: in start ema alarm");

//        boolean alarmUp = (PendingIntent.getBroadcast(this, 21,
//                new Intent(FinishInstallScreen.this, EMAAlarmReceiver.class),
//                PendingIntent.FLAG_NO_CREATE) != null);
//
//        Log.d(TAG, "Ema alarm boolean alarm up is: " + alarmUp);
//
//        if(alarmUp){
//            Log.d(TAG, "startEMAAlarm: alarm already up, skipping");
//            return;
//        }
//
//
//        Calendar cal = Calendar.getInstance();
//        long when = cal.getTimeInMillis();
//
//        cal.setTimeInMillis(System.currentTimeMillis());
//        //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
//        cal.set(Calendar.HOUR_OF_DAY, 8);
//        cal.set(Calendar.MINUTE, 15);
//
//        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, EMAAlarmReceiver.class);
//        intent.putExtra("EMA", "EMA1");
//        startEMAIntent = PendingIntent.getBroadcast(this, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startEMAIntent);
//        Log.d(TAG, "startEMAAlarm first 7 days: alarm should be set");
//        //alarmStarted = true;


    }

    public void startPhotoCrop(){

        Log.d(TAG, "startPhotoCrop: ");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 00);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PhotoCropBroadcastReceiver.class);
        intent.putExtra("EMA", "EMA1");
        startPhotoCropIntent = PendingIntent.getBroadcast(this, 27, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startPhotoCropIntent);
        Log.d(TAG, "Daily");

    }

    public void startDailyEMAAlarm(){


        if(Constants.awsBucket.equals("neuroteentest-study")|| Constants.awsBucket.equals("neuroteen-study") || Constants.awsBucket.equals("earstest")){ //TODO remove

            Log.d(TAG, "startDailyCMUEMAAlarm: in start ema alarm");

            boolean alarmUp = (PendingIntent.getBroadcast(this, 22,
                    new Intent(FinishInstallScreen.this, gwicks.com.earsnokeyboard.CMU.CmuDailyOneMinuteAlarm.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            Log.d(TAG, "Daily  CMU Ema alarm boolean alarm up is: " + alarmUp);
//
            if(alarmUp){
                Log.d(TAG, "startCMUDailyEMAAlarm: alarm already up, skipping");
                return;
            }


            Calendar cal = Calendar.getInstance();
            long when = cal.getTimeInMillis();

            cal.setTimeInMillis(System.currentTimeMillis());
            //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            cal.set(Calendar.HOUR_OF_DAY, 11);
            cal.set(Calendar.MINUTE, 00);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, gwicks.com.earsnokeyboard.CMU.CmuDailyOneMinuteAlarm.class);
            intent.putExtra("EMA", "EMA1");
            startDailyEMAIntent = PendingIntent.getBroadcast(this, 22, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startDailyEMAIntent);
            //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 20, startDailyEMAIntent);
            Log.d(TAG, "Daily");

        }else if(Constants.awsBucket.equals("duke")){
            Log.d(TAG, "startDailyCMUEMAAlarm: in start ema alarm");

            boolean alarmUp = (PendingIntent.getBroadcast(this, 22,
                    new Intent(FinishInstallScreen.this, gwicks.com.earsnokeyboard.Duke.DukeEMAAlarm.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            Log.d(TAG, "Daily  Duke Ema alarm boolean alarm up is: " + alarmUp);
//
            if(alarmUp){
                Log.d(TAG, "DukeDailyEMAAlarm: alarm already up, skipping");
                //return; //TODO put back in
            }


            Calendar cal = Calendar.getInstance();


            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 10);
            cal.set(Calendar.MINUTE, 25);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, gwicks.com.earsnokeyboard.Duke.DukeEMAAlarm.class);
            startDailyEMAIntent = PendingIntent.getBroadcast(this, 22, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
            //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startDailyEMAIntent);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 20, startDailyEMAIntent);
            Log.d(TAG, "Daily Duke");
        }

        else {

            Log.d(TAG, "startDailyEMAAlarm: in start ema alarm");

            boolean alarmUp = (PendingIntent.getBroadcast(this, 22,
                    new Intent(FinishInstallScreen.this, DailyEMAAlarmReceiver.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            Log.d(TAG, "Daily Ema alarm boolean alarm up is: " + alarmUp);

            if(alarmUp){
                Log.d(TAG, "startDailyEMAAlarm: alarm already up, skipping");
                return;
            }

            Calendar cal = Calendar.getInstance();
            long when = cal.getTimeInMillis();

            cal.setTimeInMillis(System.currentTimeMillis());
            //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 00);

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, DailyEMAAlarmReceiver.class);
            intent.putExtra("EMA", "EMA1");
            startDailyEMAIntent = PendingIntent.getBroadcast(this, 22, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startDailyEMAIntent);
            Log.d(TAG, "Daily");

        }
    }


    public void startFiveMinEMA(){

        Log.d(TAG, "startFiveMinCMU: in start ema alarm");

        boolean alarmUp = (PendingIntent.getBroadcast(this, 43,
                new Intent(FinishInstallScreen.this,gwicks.com.earsnokeyboard.CMU.CMUFiveMinAlarm.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "Five MIN CMU EMA is up:: " + alarmUp);

        if(alarmUp){
            Log.d(TAG, "startFiveMinCMU: alarm already up, skipping");
            return;
        }

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        //cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 00);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, gwicks.com.earsnokeyboard.CMU.CMUFiveMinAlarm.class);
        intent.putExtra("EMA", "EMA1");
        startFiveMinCMUEMAIntent = PendingIntent.getBroadcast(this, 43, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY , startFiveMinCMUEMAIntent);
        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*20 , startFiveMinCMUEMAIntent);

        Log.d(TAG, "Five min");

    }




    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            Log.d(TAG, "isAccessGranted: mode = " + mode);
            Log.d(TAG, "isAccessGranted: mode : " + mode + "appopsmanager = " + AppOpsManager.MODE_ALLOWED);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void showDialog()
    {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Usage Access")
                .setMessage("App will not run without usage access permissions.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent,0);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alertDialog.show();
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

    public void showMusicDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Music Listening Habits")
                .setMessage("App will not run without usage access permissions. The app only collects information from installed music players, and ignores all other notifications.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.d(TAG, "onPause: in on pause");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: on stiop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Finish install screen OnDestroy Called, why?");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");

        String theTime = df.format(cal.getTime());

        writeToFile(destroyEvents, "The Activity was destroyed at: " + theTime );

        super.onDestroy();

    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        //System.out.println("The state of the media is: " + Environment.getExternalStorageState());
        Log.d(TAG, "writeToFile: file location is:" + file.getAbsolutePath());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            Log.d(TAG, "writeToFile: 3");
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    public void launchSendEmailDialog(){
        DialogFragment newFragment = new EmailSecureDeviceID();
        newFragment.setCancelable(false);

        newFragment.show(getFragmentManager(), "email");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");

        outState.putString("SAVED", "YES");
//        if (imageUri != null) {
//            Log.d(TAG, "onSaveInstanceState: 1");
//            outState.putParcelable(SAVED_INSTANCE_BITMAP, editedBitmap);
//            Log.d(TAG, "onSaveInstanceState: 2");
//            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
//            Log.d(TAG, "onSaveInstanceState: 3");
//            Log.d(TAG, "onSaveInstanceState: the image uri saved is: " + imageUri + " also the outstate = " + outState.getString(SAVED_INSTANCE_URI));
//        }
//        Log.d(TAG, "onSaveInstanceState: 4");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 5");
    }
    public static void setSettingsDone(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("YourPref", 0);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("AlreadySetPref", true);
        editor.commit();
    }

    public static boolean isAlreadySet(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("YourPref", 0);
        return prefs.getBoolean("AlreadySetPref", false);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    // TEST FIREBASE NOT WORKING COPY METHOD TO TEST



//    public void crashApp(View v){
//        Log.d(TAG, "crashApp: crashing aoo");
//        throw new RuntimeException("This is a crash");
//    }

    public boolean checkPermissionForWriteExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void setProgressBar(){
        ProgressBar pb = findViewById(R.id.EMAProgressBar);
        TextView textView = findViewById(R.id.rank);
        TextView score = findViewById(R.id.score);
        TextView stringRankName = findViewById(R.id.textView14);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int prog = prefs.getInt("EMAs", 0);

        Log.d(TAG, "setProgressBar: prog: " + prog);
        int progressNumber = prog % 20;
        Log.d(TAG, "setProgressBar: progressNumber: " + progressNumber);
        int rank = prog / 20 + 1;
        String stringRank = mRank.get(rank).toString();
        stringRankName.setText(stringRank);

        String formatted = String.format("%02d", rank);
        Log.d(TAG, "setProgressBar: mRank: " + rank);
        pb.setProgress(progressNumber);
        score.setText(progressNumber + " / 20");
        textView.setText(formatted);

        pb.setVisibility(View.VISIBLE);
        //pb.setVisibility(View.GONE);

    }

    public void showInfoDialog(View view){
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FinishInstallScreen.this).create();
        //alertDialog.setTitle("7 Cups EARS: Informed Consent & Terms of Service Agreement");
        alertDialog.setTitle("Daily EMA Completion score");
        alertDialog.setMessage("The more daily EMA's completed, the higher the score!");
//        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "I Disagree",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE,"Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();

    }

    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }


    //TODO Remove



    public void launchKeyboardDialog(){

        DialogFragment newFragment = new LaunchKeyboardDialog();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "keyboard");
    }


    public void checkForAppUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(this);
        //appUpdateManager.registerListener(listener);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("appUpdateInfo :", "packageName :"+appUpdateInfo.packageName()+ ", "+ "availableVersionCode :"+ appUpdateInfo.availableVersionCode() +", "+"updateAvailability :"+ appUpdateInfo.updateAvailability() +", "+ "installStatus :" + appUpdateInfo.installStatus() );

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    requestUpdate(appUpdateInfo);
                    Log.d("UpdateAvailable","update is there ");
                }

            }
        });

    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        Log.d(TAG, "requestUpdate: 1");
        try {
            Log.d(TAG, "requestUpdate: 2");
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,this,MY_REQUEST_CODE);

        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 4");

        if (requestCode == MY_REQUEST_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    if(resultCode != RESULT_OK){
                        Toast.makeText(this,"RESULT_OK" +resultCode, Toast.LENGTH_LONG).show();
                        Log.d("RESULT_OK  :",""+resultCode);
                    }
                    break;
                case Activity.RESULT_CANCELED:

                    if (resultCode != RESULT_CANCELED){
                        Toast.makeText(this,"RESULT_CANCELED" +resultCode, Toast.LENGTH_LONG).show();
                        Log.d("RESULT_CANCELED  :",""+resultCode);
                    }
                    break;
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:

                    Log.d(TAG, "onActivityResult: app update failed");
            }
        }
    }



}
