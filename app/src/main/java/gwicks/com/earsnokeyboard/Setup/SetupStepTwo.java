package gwicks.com.earsnokeyboard.Setup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import gwicks.com.earsnokeyboard.R;


/**
 * Created by gwicks on 20/01/2018.
 * Basic info screen
 */

public class SetupStepTwo extends AppCompatActivity {

    private static final String TAG = "SetupStepTwo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_two_zero );
        updateStatusBarColor("#1281e8");
    }

    public void startLocationInstall(View v)
    {
//        Intent installIntent = new Intent(SetupStepTwo.this, SetupStepThree.class);
//        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        SetupStepTwo.this.startActivity(installIntent);
//        finish();
        Intent locationIntent = new Intent(SetupStepTwo.this, LocationPermission.class);
        locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SetupStepTwo.this.startActivity(locationIntent);
        finish();

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
