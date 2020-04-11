package gwicks.com.earsnokeyboard;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import research.ResearchEncoding;

/**
 * Created by gwicks on 11/05/2018.
 * The Keylogger that logs the keyboard input using the accessibility service.
 */

public class KeyLogger extends AccessibilityService {

    private static final String TAG = "KeyLogger";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {


        long unixTime = System.currentTimeMillis();
        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){



            Log.d(TAG, "onAccessibilityEvent: Accessibilty event fired");

            Log.d(TAG, "onAccessibilityEvent: event type: " + accessibilityEvent.getEventType());

            String data = "";
            String dataPackage = "";
            Calendar c = Calendar.getInstance();

            if(isNumber(data)){
                return;
            }

            SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
            String currentDate = df2.format(c.getTime());
            String path = this.getExternalFilesDir(null) + "/videoDIARY/KeyLogger/";

            File directory = new File(path);
            if(!directory.exists()){
                Log.d(TAG, "onStartJob: making directory");
                directory.mkdirs();
            }

            File location = new File(directory, currentDate +".txt");
            File location2 = new File(directory, currentDate +"_old.txt");


            if(location.length() == 0){
                Log.e(TAG, "onAccessibilityEvent: writing header!");
                WriteToFileHelper.writeHeader(location);
            }



            FileOutputStream fos = null;

            try{
                data = accessibilityEvent.getText().toString();
            }catch(Exception e){
                Log.d(TAG, "onAccessibilityEvent: exception caught");
                data = e.toString();
            }

            try{
                dataPackage =  accessibilityEvent.getPackageName().toString();
            }catch(Exception e){
                Log.d(TAG, "onAccessibilityEvent: caught excpetion");
                dataPackage = e.toString();
            }

            Log.d(TAG, "TIME: " + unixTime + ": The data is: " + data);
            Log.d(TAG,unixTime + "," + dataPackage +"," + data);


            // Protobuf test begin

            ResearchEncoding.KeyEvent event = null;
            try{
                event = ResearchEncoding.KeyEvent.parseFrom(ResearchEncoding.KeyEvent.newBuilder()
                .setTimestamp(unixTime)
                        .setApp(dataPackage)
                        .setTextField(data)
                        .build().toByteArray() );
            }catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            try {
                fos = new FileOutputStream(location, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                event.writeDelimitedTo(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    public boolean isNumber(CharSequence s){
        try{
            return android.text.TextUtils.isDigitsOnly(s);

        }catch(Exception e){
            Log.d(TAG, "isNumber: exception!");
        }
        return false;
    }


    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
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
        }
    }
}
