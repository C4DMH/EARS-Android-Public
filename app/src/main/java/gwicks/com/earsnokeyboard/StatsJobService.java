package gwicks.com.earsnokeyboard;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

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
 */
public class StatsJobService extends JobService {

    //Context myContext;
    static String folder = "/videoDIARY/";
    private static final String TAG = "StatsJobService";
    @Override
    public boolean onStartJob(JobParameters params) {
        GPSTracker mGPSTracker = new GPSTracker(this);


        //myContext = this.getApplication().getApplicationContext();
        Calendar c = Calendar.getInstance();

        //Log.d(TAG, "doWork: context = " + myContext);
        Log.d(TAG, "doWork: context this= " + this);

        Log.d(TAG, "doWork: GPS tracker = " + mGPSTracker);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String currentDate = df2.format(c.getTime());

        String path = this.getExternalFilesDir(null) + "/videoDIARY/Location/";

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.
                    mkdirs();
        }

        File location = new File(directory, currentDate +".txt");

        if(location.length() == 0){


            WriteToFileHelper.writeHeader(location);

        }
        Log.d(TAG, "onStartJob: before");

        double latitude = mGPSTracker.getLatitude();
        double longitude = mGPSTracker.getLongitude();
        long TS = System.currentTimeMillis();
        Log.d(TAG, "onStartJob: after");

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
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: in on stop job");
        return false;
    }


}
