package gwicks.com.earsnokeyboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "DefaultExceptionHandler";
    private Thread.UncaughtExceptionHandler defaultUEH;


    //Activity activity;
    TransferUtility transferUtility;
    String android_id;
    Context mContext;
    static String folder = "/CRASH/";


    //Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    public DefaultExceptionHandler(Context activity) {
        Log.d(TAG, "DefaultExceptionHandler: ");
        mContext = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.d(TAG, "uncaughtException: ");
        transferUtility = Util.getTransferUtility(mContext);

        //android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        android_id = FinishInstallScreen.secureID;

        Log.d(TAG, "uncaughtException: 1");


        try {
            Log.d("Crash", "Crash 2.5");
            Log.d(TAG, "uncaughtException: 2");

            String CrashReportUpload = (mContext.getExternalFilesDir(null) + "/videoDIARY/" + "CrashReport.txt");
            File file = new File(CrashReportUpload);

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            //String appName = DeveloperUtils.getAppDetails(activity);

            final CharSequence utcTimeDate = DateFormat.format(
                    "kk:mm:ss dd.MM.yyyy", new Date());



            writeToFile(file,Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion +"\n");
            writeToFile(file, "Hi. It seems that we have crashed.... Here are some details:"
                    + "****** UTC Time: "
                    + utcTimeDate

                    + "****** Application name: "

                    + "******************************" +"\n"
                    + "****** Exception type: " +"\n"
                    + ex.getClass().getName() +"\n"
                    +"\n"
                    + "****** Exception message: "
                    + ex.getMessage() + "\n" + "****** Trace trace:"  +"\n" + stackTrace  +"\n"
                    + "******************************" +"\n"
                    + "****** Device information:"  +"\n");


            //writeToFile(file, stackTrace);
            Log.d("Crash", "Crash 2");
            beginUpload2(CrashReportUpload);
            Log.d("Crash", "Crash 3");

            Thread.sleep(15000);




            Log.d("Exception", "Start of Exception handler class");

            //Intent intent = new Intent(activity, MainActivity.class);
            Intent intent = new Intent(mContext, FinishInstallScreen.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("Exception", "Start of Exception handler class 2");

            // Changed 7th June 2017

//            PendingIntent pendingIntent = PendingIntent.getActivity(
//                    MainActivity.getIntance().getBaseContext(), 0, intent, intent.getFlags());

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    mContext, 0, intent, intent.getFlags());

//            PendingIntent pendingIntent = PendingIntent.getActivity(
//                    MainActivity.getIntance().getApplicationContext(), 0, intent, intent.getFlags());
            Log.d("Exception", "Start of Exception handler class 3");
            //Following code will restart your application after 2 seconds


            // Changed 7th June 2017
//            AlarmManager mgr = (AlarmManager) MainActivity.getIntance().getBaseContext()
//                    .getSystemService(Context.ALARM_SERVICE);
            AlarmManager mgr = (AlarmManager) mContext
                    .getSystemService(Context.ALARM_SERVICE);

            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 20000,
                    pendingIntent);
            Log.d("Exception", "Start of Exception handler class 4");
            //This will finish your activity manually
            //activity.finish();
            Log.d("Exception", "Start of Exception handler class 5");
            //This will stop your application and take out from it.
            System.exit(1);
        } catch (Exception e) {
            Log.d(TAG, "uncaughtException: 3");
            e.printStackTrace();
            
        }

    }

    private static void writeToFile(File file, String data) {
        Log.d(TAG, "writeToFile: 1");
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        File path = context.getExternalFilesDir(null);
//        File file = new File(path, "googleFit.txt");


//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
        FileOutputStream stream = null;
        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            android.util.Log.d("Default Exception", "In try");
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            android.util.Log.e("Default exception", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void beginUpload2(String filePath) {
        if (filePath == null) {
            //Toast.makeText(this, "Could not find the filepath of the selected file",Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Crash", "Crash 1");

        //TransferUtility transferUtility;
        //transferUtility = Util.getTransferUtility(this);
        //transferUtility = Util.getTransferUtility(context);

        //setTheDate();
        String newFilePath = android_id + "/CrashReport.txt";

        Log.d(TAG, "beginUpload2: newFIlePath: " + newFilePath);
        Log.d(TAG, "beginUpload2: bucket: " + Constants.awsBucket);

        Log.d(TAG, "beginUpload2: filepath: " + filePath);
        //String newFilePath = UserID + "/" ;

        //Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
        //Log.d("uploading, using: " + newFilePath, "");
        File file = new File(filePath);
        Log.d(TAG, "beginUpload2: before upload");
//        TransferObserver observer = transferUtility.upload(Constants.awsBucket, newFilePath,
//                file);
        transferUtility.upload(Constants.awsBucket,  newFilePath, file);
        Log.d(TAG, "beginUpload2: after upload");

        //Util.uploadFileToBucket(file,"CrashReport.txr", true,logUploadCallback,mContext);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }

//    final Util.FileTransferCallback logUploadCallback = new Util.FileTransferCallback() {
//        @SuppressLint("DefaultLocale")
//
//        private String makeLogLine(final String name, final int id, final TransferState state) {
//            Log.d("LogUploadTask", "This is AWSBIT");
//            return String.format("%s | ID: %d | State: %s", name, id, state.toString());
//        }
//
//        @Override
//        public void onCancel(int id, TransferState state) {
//            Log.d(TAG, makeLogLine("Callback onCancel()", id, state));
//        }
//
//        @Override
//        public void onStart(int id, TransferState state) {
//            Log.d(TAG, makeLogLine("Callback onStart()", id, state));
//
//        }
//
//        @Override
//        public void onComplete(int id, TransferState state) {
//            Log.d(TAG, makeLogLine("Callback onComplete()", id, state));
//        }
//
//        @Override
//        public void onError(int id, Exception e) {
//            Log.d(TAG, makeLogLine("Callback onError()", id, TransferState.FAILED), e);
//        }
//    };


}
