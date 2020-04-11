package gwicks.com.earsnokeyboard.Setup;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import gwicks.com.earsnokeyboard.R;

/**
 * Created by gwicks on 11/05/2018.
 * This step installs the Accessibility Keyboard option, to enable key logging of all keyboard input. Lots of scary warnings on install
 */

public class SetupStepThree extends AppCompatActivity {

    private static final String TAG = "SetupStepThree";

    Context mContext;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_three_zero);
        updateStatusBarColor("#1281e8");
        checkLocationPermission();

        mContext = this;
        logInstalledAccessiblityServices(this);
    }

    public void skipThisStep(View v){
        Intent intent = new Intent(SetupStepThree.this, SetupStepThreeOne.class);
        SetupStepThree.this.startActivity(intent);

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }


    // Button click in XML fires this to install the keyboard

    public void installKeyboard(View v){

        final Handler handler = new Handler();

        Runnable checkOverlaySetting = new Runnable() {

            @Override
            //@TargetApi(23)
            public void run() {
                Log.d(TAG, "run: 1");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d(TAG, "run: 2");
                    //return;
                }

                // 18th Jan 2018, below works, trying to stop using the intent ( ie try back button below).
                if (isAccessibilityEnabled(mContext, "gwicks.com.earsnokeyboard/.KeyLogger")) {
                    Log.d(TAG, "run: 3");
                    //You have the permission, re-launch MainActivity
                    Intent i = new Intent(SetupStepThree.this, SetupStepFour.class);
                    Log.d(TAG, "run: 4");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return;
                }
                Log.d(TAG, "run: 5");

                handler.postDelayed(this, 200);
            }
        };

        Intent startSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            Log.d(TAG, "onClick: 5");
            startActivity(startSettings);
            handler.postDelayed(checkOverlaySetting, 1000);
        } catch (ActivityNotFoundException notFoundEx) {
            //weird.. the device does not have the IME setting activity. Nook?
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }



    }

    protected boolean isStepCompleted(@NonNull Context context) {
        Log.d(TAG, "isStepCompleted: is step completed?");
        InputMethodManager imeManager = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> InputMethods = imeManager.getEnabledInputMethodList();
        for(InputMethodInfo model : InputMethods) {
            System.out.println(model.getPackageName());
        }

        //return SetupSupport.isThisKeyboardEnabled(context);
        return false;
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }

    public static void logInstalledAccessiblityServices(Context context){

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i(TAG, service.getId());
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }





    // Addded location for checking 19th Feb 2019

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("EARS need permission to check your location")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SetupStepThree.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
//                }
//                return;
//            }
//
//        }
//    }



}
