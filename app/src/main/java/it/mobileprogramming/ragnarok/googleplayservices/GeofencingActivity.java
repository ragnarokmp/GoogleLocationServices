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


public class GeofencingActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>{

    private final String GEO_TAG = "GEO";
    private final String GEOFENCE_ID = "GEOFENCE";
    private final long GEOFENCE_MINEXPIRATION_MILLI = 1000*60*10;
    private final long GEOFENCE_MINRADIUS_METERS = 50;

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

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(GEO_TAG, "Connected to GoogleApiClient");
    }

    public void retrieveLocation(View view) {
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())){
            // retrieving actual position
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                ((TextView) findViewById(R.id.latitudeTextView)).setText(String.valueOf(mLocation.getLatitude()));
                ((TextView) findViewById(R.id.longitudeTextView)).setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.GEO_notAPI), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(this, this.getResources().getString(R.string.GEO_notAPI), Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void geofenceAdd(View view) {

        if (mLocation == null) {
            Toast.makeText(this, this.getResources().getString(R.string.GEO_notLocation), Toast.LENGTH_LONG).show();
            return;
        }

        // retrieving the radius
        long perimeter_radius = (long)((SeekBar)findViewById(R.id.perimeterSeekBar)).getProgress() + GEOFENCE_MINRADIUS_METERS;
        long expiration = (long) ((SeekBar)findViewById(R.id.durationSeekBar)).getProgress()*1000*60 + GEOFENCE_MINEXPIRATION_MILLI;
        // adding the geofence
        mGeofences.add(new Geofence.Builder()
                        .setRequestId(GEOFENCE_ID + String.valueOf(mGeofences.size() + 1))

                        .setCircularRegion(
                                mLocation.getLatitude(),
                                mLocation.getLongitude(),
                                perimeter_radius)

                        .setExpirationDuration(expiration)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
                        .build()
        );
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(GEO_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(GEO_TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i(GEO_TAG, "Geofence added successfully");
            Toast.makeText(this, this.getResources().getString(R.string.GEO_added), Toast.LENGTH_LONG).show();
        }
    }
}
