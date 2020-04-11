package gwicks.com.earsnokeyboard;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import research.ResearchEncoding;

public class LocationManager extends Worker {

    Context mContext;
    private static final String TAG = "LocationManager";

    public LocationManager(Context context, WorkerParameters workerParameters){
        super(context, workerParameters);
        Log.d(TAG, "LocationManager: jconstructor");
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Context myContext = getApplicationContext();
        GPSTracker mGPSTracker = new GPSTracker(myContext);

        Log.d(TAG, "doWork: context = " + mContext);
        Calendar c = Calendar.getInstance();
        Log.d(TAG, "doWork: GPS tracker = " + mGPSTracker);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String currentDate = df2.format(c.getTime());

        String path = mContext.getExternalFilesDir(null) + "/videoDIARY/Location/";

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.
                    mkdirs();
        }

        File location = new File(directory, currentDate +"_MANAGER.txt");

        if(location.length() == 0){
            WriteToFileHelper.writeHeader(location);
        }
        Log.d(TAG, "doWork: before");

        double latitude = mGPSTracker.getLatitude();
        double longitude = mGPSTracker.getLongitude();
        long TS = System.currentTimeMillis();
        Log.d(TAG, "doWork: afert");

        // Start of Protobuf implementation 1/7/19

        FileOutputStream fos = null;

        ResearchEncoding.GPSEvent event = null;

        try{
            event = ResearchEncoding.GPSEvent.parseFrom(ResearchEncoding.GPSEvent.newBuilder()
                    .setTimestamp(TS)
                    .setLat(latitude)
                    .setLon(longitude)
                    .build().toByteArray());
        }catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        try {
            fos = new FileOutputStream(location,true);
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

        // End protobuf

        Log.d(TAG, "onStartJob: Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude);
        return Result.success();
    }
}
