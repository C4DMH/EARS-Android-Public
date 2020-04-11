package gwicks.com.earsnokeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhotoCropBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhotoCropBroadcastRecei";
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.d(TAG, "onReceive: PhotoCrop receiver starting");

        FaceDetectAndCrop fd = new FaceDetectAndCrop(mContext);
        fd.execute();

        Log.d(TAG, "onReceive: PhotoCrop onReceive finished");
        
        
    }
}
