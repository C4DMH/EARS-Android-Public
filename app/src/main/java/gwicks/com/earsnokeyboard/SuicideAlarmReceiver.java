package gwicks.com.earsnokeyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

import androidx.core.app.NotificationCompat;

public class SuicideAlarmReceiver  extends BroadcastReceiver {

    private static final String TAG = "SuicideAlarmReceiver";
    private NotificationManager mNotificationManager;

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    String CHANNEL_DI = "suicide_ema";
    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(day != Calendar.WEDNESDAY){
            Log.d(TAG, "onReceive: not wednesday");
            return;
        }

        if((hour < 9 ) || (hour > 10)){
            Log.d(TAG, "onReceive: wrong time");
            return;
        }

        if(mNotificationManager == null){
            Log.d(TAG, "onReceive: in notification manager = null");
            //mNotificationManager = FinishInstallScreen.notificationManager;
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE) ;
            Log.d(TAG, "onReceive: notificiation manager  = " + mNotificationManager);
            //mNotificationManager = EMA.
            //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Log.d(TAG, "onReceive: before clear notifications");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String name = "Oreo_Notificaitons";
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(CHANNEL_DI);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_DI, name, importance);
                mChannel.setDescription("blah_1");
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);

            }
        }

        Random random = new Random();
        int nxt = random.nextInt(99);

        Intent resultIntent = new Intent(context, SecondEMA.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, nxt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_DI)
                        .setSmallIcon(R.drawable.noti_icon)
                        .setContentTitle("Quick Survey")
                        .setAutoCancel(true)
                        .setContentText("Time for another quick survey :)")
                        .setOngoing(true)
                        .setChannelId(CHANNEL_DI)
                        .setSound(uri)
                        .setContentIntent(pendingIntent)
                        //.addAction(action)
                        .build();


        mNotificationManager.notify("first",1, mBuilder);
        Log.d(TAG, "onReceive OREO: should be notification built now");



    }
}
