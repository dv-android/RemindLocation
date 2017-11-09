package com.example.devang.remindlocation;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity implements  OnCompleteListener<Void>{

    private static boolean isLaunch = true;
    private double lattitude,longitude;
    private Button setLocation;
    private static final String TAG = MainActivity.class.getSimpleName();

    static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    private GeofencingClient mGeofencingClient;

    private String resultAddr;

    private static final String PACKAGE_NAME = "com.example.devang.remindlocation";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivityy","onCreate called");
        Log.d("MainActivityy","isLaunch="+isLaunch);
        setLocation = (Button) findViewById(R.id.button1);
        if(isLaunch){
            isLaunch = false;
            Log.d("MainActivityy","opened from system");
        }
        else
        {
            Intent intent = getIntent();
            Log.d("MainActivityy","Intent Extra ="+intent.hasExtra("whichActivity"));
            if(intent.hasExtra("whichActivity"))
            {
                String s = getIntent().getExtras().getString("whichActivity");
                lattitude = getIntent().getExtras().getDouble("lat");
                longitude = getIntent().getExtras().getDouble("longit");
                setLocation.setText(String.valueOf(lattitude));
                Log.d("MainActivityy", "S =" + s);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(lattitude, longitude, 1);
                    if(addresses!=null && addresses.size()>0){
                        Address address = addresses.get(0);
                        resultAddr = address.getAddressLine(1) + ","+address.getLocality();
                        Log.i("Main ACtivity","Address ="+resultAddr);
                    }
                } catch(IOException e) {

                }
                mGeofenceList = new ArrayList<>();
                mGeofencePendingIntent = null;

                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("abc")

                        .setCircularRegion(
                                lattitude,
                                longitude,
                                1609
                        )
                        .setExpirationDuration(12* 60 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build());

                mGeofencingClient = LocationServices.getGeofencingClient(this);
            }
            Log.d("MainActivityy","opened from another activity");
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        //intent.putExtra("address",resultAddr);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofences() throws SecurityException{


        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }


    public void onComplete(@NonNull Task<Void> task) {
        /**mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }*/
        if(task.isSuccessful()){
            Toast.makeText(this, "Geofence added", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Geofence can not be added", Toast.LENGTH_SHORT).show();
        }
    }










    public void openMapActivity(View v){
        Log.d("MainActivity","Button clicked");
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

    public void startBckService(View v){
        addGeofences();
    }






}
