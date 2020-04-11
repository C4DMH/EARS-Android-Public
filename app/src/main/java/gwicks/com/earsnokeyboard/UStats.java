package gwicks.com.earsnokeyboard;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import research.ResearchEncoding;

/**
 * Created by gwicks on 11/05/2018.
 * Get the phone usage stats of the user. Requires permission
 */

public class UStats {

    public static final String TAG = UStats.class.getSimpleName();

    static String directoryName = "/videoDIARY/";
    static long time;

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Log.d(TAG, "getUsageStatsList: SDF =  " + sdf);

        long endTime = calendar.getTimeInMillis();
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long startTime = calendar.getTimeInMillis() - 24*60*60*1000;
        long startTime = calendar.getTimeInMillis() - 24*60*60*1000*7;


//        long endTime = calendar.getTimeInMillis() - 24*60*60*1000;
//        //calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long startTime = calendar.getTimeInMillis() - 24*60*60*1000+1000000 ;


        Log.d(TAG, "getUsageStatsList: endtime: " + endTime + "starttime: " + startTime);

        Date one = new Date(startTime);
        Date two = new Date(endTime);

        Log.d(TAG, "getUsageStatsList: data start time: " + one);
        Log.d(TAG, "getUsageStatsList: date endtime:  " + two);
        //time = two.toString();
        time = endTime;

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  startTime,endTime);     // calendar.getTimeInMillis(), System.currentTimeMillis()); //(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);

        Map<String, UsageStats> stats = usm.queryAndAggregateUsageStats(startTime, endTime);
        Log.d(TAG, "getUsageStatsList: 1");

        for(String key : stats.keySet()){
            Log.d(TAG, "getUsageStatsList: KEYS: " + stats.get(key));
        }

        Log.d(TAG, "getUsageStatsList: 2");

        for(Map.Entry<String, UsageStats> entry : stats.entrySet()){
            Log.d(TAG, "getUsageStatsList: " + entry.getKey() + " " + entry.getValue().getTotalTimeInForeground());
        }

        Log.d(TAG, "getUsageStatsList: 3");

        String aggregateApps = (context.getExternalFilesDir(null) + directoryName + "AppUsageAggeagate" + time + ".txt");
        File agFile = new File(aggregateApps);
        for(Map.Entry<String, UsageStats> entry : stats.entrySet()){
            writeToFile(agFile, entry.getKey() +"," + entry.getValue().getTotalTimeInForeground() + "\n");
        }



        Collections.sort(usageStatsList, new TotalTimeUsed());
        return usageStatsList;
    }


    // attempt to find out what the fuck is going on with usage stats manager



    public static String printUsageStats(List<UsageStats> usageStatsList, Context context){

        Log.d(TAG, "printUsageStats: in print");

        String uri = (context.getExternalFilesDir(null) + directoryName + "AppUsage_" + time + ".txt");



        Log.d(TAG, "printUsageStats: The string URI for file is: " + uri);


        File file = new File(uri);


        if(file.length() == 0){
            WriteToFileHelper.writeHeader(file);
        }

//        JSONArray jsonArray = new JSONArray();
//        JSONObject object = null;

        FileOutputStream fos = null;
        //Constants.writeHeaderToFile(file, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + "," + Constants.androidVersion + "," + Constants.earsVersion +"\n");
        for (UsageStats u : usageStatsList){

            if(u.getTotalTimeInForeground() > 0){

                int minutes = (int)u.getTotalTimeInForeground()/60000;
                int seconds = (int)(u.getTotalTimeInForeground() % 60000) / 1000;


                // Begin the protobuf

                ResearchEncoding.AppUsageEvent event = null;

                try{
                    event = ResearchEncoding.AppUsageEvent.parseFrom(ResearchEncoding.AppUsageEvent.newBuilder()
                            .setFirstTimeStamp(u.getFirstTimeStamp())
                            .setLastTimeStamp(u.getLastTimeStamp())
                            .setTimeLastUsed(u.getLastTimeUsed())
                            .setTimeInForeground((int) u.getTotalTimeInForeground())
                            .setApp( u.getPackageName())
                            .build().toByteArray());
                }catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                try {
                    fos = new FileOutputStream(file,true);
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





                // end the protobuf















//                object = new JSONObject();
//                try {
//                    object.put("Package", u.getPackageName());
//                    object.put("Time in foreground", u.getTotalTimeInForeground());
//                    object.put("First Time stamp", u.getFirstTimeStamp());
//                    object.put("Last time stamp", u.getLastTimeStamp());
//                    object.put("Time last used",u.getLastTimeUsed());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                jsonArray.put(object);


            }



        }

//        JSONObject finalObject = new JSONObject();
//        try {
//            finalObject.put("AppUsage", jsonArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Constants.writeHeaderToFile(file, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + "," + Constants.androidVersion + "," + Constants.earsVersion +"\n");
//
//        writeToFile(file, finalObject.toString());
        return uri;
    }

    public static String printCurrentUsageStatus(Context context){
        return printUsageStats(getUsageStatsList(context), context);
    }


    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    private static class TotalTimeUsed implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
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
