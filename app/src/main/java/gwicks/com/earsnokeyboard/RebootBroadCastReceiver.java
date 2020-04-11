package gwicks.com.earsnokeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

/**
 * Created by gwicks on 11/05/2018.
 * Reloads the app on phone restart
 */

public class RebootBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Intent myIntent = new Intent(context, FinishInstallScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

}