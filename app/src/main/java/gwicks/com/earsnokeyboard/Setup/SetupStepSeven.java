package gwicks.com.earsnokeyboard.Setup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import gwicks.com.earsnokeyboard.R;

/**
 * Created by gwicks on 11/05/2018.
 * This class is basically just a handler to show a thank you screen for 4 seconds before moving to final page
 */

public class SetupStepSeven extends AppCompatActivity {

    private static final String TAG = "StepSeven";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_seven );
        updateStatusBarColor("#1281e8");

        moveToNextStep();

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void moveToNextStep(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(SetupStepSeven.this, FinishInstallScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                SetupStepSeven.this.startActivity(intent);
            }
        }, 4000);

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
