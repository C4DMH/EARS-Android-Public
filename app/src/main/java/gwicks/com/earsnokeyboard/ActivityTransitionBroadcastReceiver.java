package gwicks.com.earsnokeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import research.ResearchEncoding;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ActivityTransitionBroad";
    Context mContext;

    public static final String INTENT_ACTION = "gwicks.com.earsnokeyboard" +
            ".ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: ACTIVITY DETECTED");

        mContext = context;

        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            if (ActivityTransitionResult.hasResult(intent)) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());
                String path = mContext.getExternalFilesDir(null) + "/MotionActivity/";
                long unixTime = System.currentTimeMillis();
                File directory = new File(path);
                if(!directory.exists()){
                    Log.d(TAG, "onStartJob: making directory");
                    directory.mkdirs();
                }

                File location = new File(directory, "MotionActivity_" + formattedDate +".txt");

                if(location.length() == 0){
                    Log.e(TAG, "on Activity Transition Event: writing header!");
                    WriteToFileHelper.writeHeader(location);
                }

                FileOutputStream fos = null;

                ActivityTransitionResult intentResult = ActivityTransitionResult.extractResult(intent);
                Log.d(TAG, "onReceive: stuff" );
                List<ActivityTransitionEvent> events = intentResult.getTransitionEvents();
                for(ActivityTransitionEvent e : events){
                    Log.d(TAG, "onReceive: elapsed time seconds " + e.getElapsedRealTimeNanos()/100000);
                    Log.d(TAG, "onReceive: tranistion type" + e.getTransitionType());
                    Log.d(TAG, "onReceive: get activity type " + e.getActivityType());
                    Log.d(TAG, "onReceive: to string " + e.toString());


                    ResearchEncoding.ActivityTransitionEvent event = null;
                    try{
                        event = ResearchEncoding.ActivityTransitionEvent.parseFrom(ResearchEncoding.ActivityTransitionEvent.newBuilder()
                                .setTimestamp(unixTime)
                                .setElapsedRealTime(e.getElapsedRealTimeNanos())
                                .setActivityValue(e.getActivityType())
                                .setTransitionType(e.getTransitionType())
                                .build().toByteArray() );
                    }catch (InvalidProtocolBufferException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        fos = new FileOutputStream(location, true);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        event.writeDelimitedTo(fos);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }finally {
                        if(fos!=null){
                            try {
                                fos.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                Log.d(TAG, "onReceive: intent action: " + intent.getAction());
            }
        }
    }
}
