package gwicks.com.earsnokeyboard.Setup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.location.ActivityTransition;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import gwicks.com.earsnokeyboard.R;

public class SetupFitness extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 45;
    private static final String TAG = "SetupFitness";
    List<ActivityTransition> transitions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitness_permission);
        updateStatusBarColor("#1281e8");
    }

    public void askForActivityPermission(View v) {
        Log.d(TAG, "askForActivityPermission: 1");

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION);
        Log.d(TAG, "askForActivityPermission: permision check: " + permissionCheck);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForActivityPermission: permission not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_ACTIVITY_RECOGNITION);

            // Permission is not granted
        }





        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_ACTIVITY_RECOGNITION);

    }


//    public void askForActivityPermission(View v) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Log.d(TAG, "askForActivityPermission: q or greater");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
//        } else {
//            Log.d(TAG, "askForActivityPermission: less than q");
//            moveNextStep();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        String permissionResult = "Request code: " + requestCode + ", Permissions: " +
                Arrays.toString(permissions) + ", Results: " + Arrays.toString(grantResults);

        Log.d(TAG, "onRequestPermissionsResult(): " + permissionResult);

        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            Log.d(TAG, "onRequestPermissionsResult: permission granted?");
//            Log.d(TAG, "askForActivityPermission: " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION));
//            Log.d(TAG, "askForActivityPermission: " +  ActivityCompat.checkSelfPermission(this, "com.google.android.gms.permission.ACTIVITY_RECOGNITION") );
            // Close activity regardless of user's decision (decision picked up in main activity).
            moveNextStep();
            
        }
    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void moveNextStep(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent installIntent = new Intent(SetupFitness.this, BatteryOptimization.class);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SetupFitness.this.startActivity(installIntent);
            finish();

        }else {
            Intent installIntent = new Intent(SetupFitness.this, SetupStepThree.class);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SetupFitness.this.startActivity(installIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }



}