package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


/**
 * This class defines the behaviour of the activity used to launch and manage the geofence monitoring
 */
public class GeofencingActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>{

    protected GoogleApiClient mGoogleApiClient;
    protected PendingIntent mGeofencePendingIntent;
    protected Location mLocation;
    protected ArrayList<Geofence> mGeofences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofencing);

        // building the GoogleApiClient
        setGoogleApiClient();
    }

    // initializing the Google API client
    protected synchronized void setGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    // when an API client successfully connects or wheter it does not...
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(Constants.GEO_TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(Constants.GEO_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(Constants.GEO_TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
    }

    // this method will be used to retrieve the location
    public void retrieveLocation(View view) {
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())){
            // retrieving actual position
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                ((TextView) findViewById(R.id.latitudeTextView)).setText(String.valueOf(mLocation.getLatitude()));
                ((TextView) findViewById(R.id.longitudeTextView)).setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.GEO_notAPI), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, this.getResources().getString(R.string.GEO_notAPI), Toast.LENGTH_LONG).show();
        }
    }

    // this method will be called on geofenceAdd button to add a geofence around the position just retrieved
    public void geofenceAdd(View view) {
        if (mLocation == null) {
            Toast.makeText(this, this.getResources().getString(R.string.GEO_notLocation), Toast.LENGTH_LONG).show();
            return;
        }

        // retrieving geofence's radius, expiration and dwelling period
        long perimeter_radius = (long)((SeekBar)findViewById(R.id.perimeterSeekBar)).getProgress()
                                                                                    + Constants.GEOFENCE_MINRADIUS_METERS;
        long expiration = (long) ((SeekBar)findViewById(R.id.durationSeekBar)).getProgress()*1000*60
                                                                              + Constants.GEOFENCE_MINEXPIRATION_MILLI;
        int dwelling = ((SeekBar)findViewById(R.id.dwellingSeekBar)).getProgress()*1000*60
                                                                    + Constants.GEOFENCE_MINDWELLING_MILLI;

        // check for maximum (100) geofences available per device
        if (mGeofences.size() == Constants.GEO_MAX_INSTANCES)
            mGeofences.clear();

        // adding the geofence
        mGeofences.add(new Geofence.Builder()
                        .setRequestId(Constants.GEO_IDENTIFIER + String.valueOf(mGeofences.size() + 1))

                        .setCircularRegion(
                                mLocation.getLatitude(),
                                mLocation.getLongitude(),
                                perimeter_radius)

                        .setExpirationDuration(expiration)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT |
                                Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(dwelling)
                        .build()
        );
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    // this method will suspend the geofence monitoring
    public void geofenceRemove(View view) {
        if (mGoogleApiClient != null) {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    // method used to retrieve the geofence request object
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // initially the event will be triggered by the dwelling in the geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofences);
        return builder.build();
    }

    // method used to retrieve a pending intent to the custom intent service
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // the callback handler method
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i(Constants.GEO_TAG, "Geofence added successfully");
            Toast.makeText(this, this.getResources().getString(R.string.GEO_added), Toast.LENGTH_LONG).show();
        }

        if (status.isSuccess()) {
            Log.i(Constants.GEO_TAG, "Geofence removed successfully");
            Toast.makeText(this, this.getResources().getString(R.string.GEO_removed), Toast.LENGTH_LONG).show();
        }
    }
}
