package gwicks.com.earsnokeyboard;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import research.ResearchEncoding;

public class WriteToFileHelper {

    private static final String TAG = "WriteToFileHelper";

    public static File writeHeader(File file){

        FileOutputStream fosHeader = null;
        Log.d(TAG, "writeHeader: begin header write");

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        SimpleDateFormat date = new SimpleDateFormat("ZZZZ");
        String localTime = date.format(currentLocalTime);
        String stripGMT = localTime.replace("GMT","");
        Log.d(TAG, "writeHeader: stripGMT: " + stripGMT + " localtime: " + localTime);

        ResearchEncoding.Header header = null;
        try {
            header = ResearchEncoding.Header.parseFrom(ResearchEncoding.Header.newBuilder()
                    .setDeviceID(Constants.deviceID)
                    .setModelName(Constants.modelName)
                    .setModelNumber(Constants.modelNumber)
                    .setOsVersion(Integer.toString(Constants.androidVersion))
                    .setAppVersion(Constants.earsVersion)
                    .setTimezone(stripGMT)
                    .build().toByteArray());

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        try {
            fosHeader = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
            //fosHeader.write(header2);
            header.writeDelimitedTo(fosHeader);
            //header.writeTo(fosHeader);
            //bytes.
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fosHeader!=null){
                try {
                    fosHeader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "onCreate: after header");
        return file;
    }


    protected static void writeToFile(File file, String string){
        Log.d(TAG, "writeToFile nothing here yet");
    }


}
