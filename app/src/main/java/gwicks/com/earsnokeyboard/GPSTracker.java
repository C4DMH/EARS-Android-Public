package gwicks.com.earsnokeyboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by gwicks on 13/04/2018.
 * basic GPS tracking class, used in various places throughout the application
 *
 */

public class GPSTracker {

    private static final String TAG = "GPSTracker";

    private final Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        Log.d(TAG, "GPSTracker: 1");
        this.mContext = context;
        Log.d(TAG, "GPSTracker: 2");
        getLocation();
        Log.d(TAG, "GPSTracker: 3");
    }


//    public static Location getLocation2(){
//
//    }

    public Location getLocation() {
        Log.d(TAG, "getLocation: 1");


        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);
                Log.d(TAG, "getLocation: 2");

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    Log.d(TAG, "getLocation: 3");
                    // no network provider is enabled
                } else {

                    //Location locatrio = locationManager.getLastKnownLocation()
                    this.canGetLocation = true;
                    Log.d(TAG, "getLocation: 4");
                    Log.d(TAG, "getLocation: boolean isNetwork enabled: " + isNetworkEnabled);
                    if (isNetworkEnabled) {
                        Log.d(TAG, "getLocation: 4.1");
                        Log.d(TAG, "getLocation: locationManger = " + locationManager);
                        Log.d(TAG, "getLocation: this = " + this);
                        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        Log.d(TAG, "getLocation: 4.2");
                        if (locationManager != null) {
                            Log.d(TAG, "getLocation: 4.3");
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                Log.d(TAG, "getLocation: lat1: " + latitude);
                                longitude = location.getLongitude();
                                Log.d(TAG, "getLocation: long1 : " + longitude);
                            }
                        }
                    }
                    Log.d(TAG, "getLocation: 5");
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        Log.d(TAG, "getLocation: 5.1");
                        if (location == null) {
                            Log.d(TAG, "getLocation: 5.2");
                            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d(TAG, "getLocation: 5.3");
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    Log.d(TAG, "getLocation: lat2: " + latitude);
                                    longitude = location.getLongitude();
                                    Log.d(TAG, "getLocation: long2 : " + longitude);
                                }
                                Log.d(TAG, "getLocation: 7");
                            }
                            Log.d(TAG, "getLocation: 8");
                        }
                        Log.d(TAG, "getLocation: 9");
                    }
                    Log.d(TAG, "getLocation: 10");
                }

            } catch (Exception e) {
            }
        }
            return location;

    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app.
     * */
//    public void stopUsingGPS() {
//        if (locationManager != null) {
//            locationManager.removeUpdates(GPSTracker.this);
//        }
//    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        Log.d(TAG, "getLongitude: 11");
        if (location != null) {
            Log.d(TAG, "getLongitude: 11.1");
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting DialogHelp Title
        alertDialog.setTitle("GPS is settings");

        // Setting DialogHelp Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }


    public float getAccurecy()
    {
        return location.getAccuracy();
    }




}
