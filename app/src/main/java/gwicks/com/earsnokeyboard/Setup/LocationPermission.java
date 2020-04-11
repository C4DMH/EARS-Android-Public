package gwicks.com.earsnokeyboard.Setup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import gwicks.com.earsnokeyboard.R;

public class LocationPermission extends AppCompatActivity {

    private static final String TAG = "LocationPermission";
    private static final int ACCESS_FINE_LOCATION = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_permission);
        updateStatusBarColor("#1281e8");
    }


    public void updateStatusBarColor(String color) {// Color must be in hexadecimal fromat
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: 11");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: why am i here?");
                    moveToStepTwo();
                } else {
                    Toast.makeText(LocationPermission.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }


        }
    }

    public void askForLocationPermission(View v) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Do something
                Log.d(TAG, "requestCameraPermission: location permission OK");
                moveToStepTwo();
            } else {
                Log.d(TAG, "requestLocationPermission: rewquesting permission location");

                ActivityCompat.requestPermissions(LocationPermission.this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void moveToStepTwo(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent installIntent = new Intent(LocationPermission.this, BatteryOptimization.class);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            LocationPermission.this.startActivity(installIntent);
            finish();

        }else {
            Intent installIntent = new Intent(LocationPermission.this, SetupStepThree.class);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            LocationPermission.this.startActivity(installIntent);
            finish();
        }
    }
}
