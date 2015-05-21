package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FLPActivityLocationPendingIntent extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "FLP-location-listeners";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView timeUpdateText;

    LocationRequest mLocationRequest;
    private FLPBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flpactivity_location_pending_intent);

        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        timeUpdateText = (TextView) findViewById((R.id.timeupdate_text));

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
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
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        // Set location request
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent anIntent = new Intent(this, FLPIntentService.class);
        anIntent.setAction(Intent.ACTION_SEND);

        PendingIntent aPendingIntent = PendingIntent.getService(this, 0, anIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Register pending intent to LocationServices
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, aPendingIntent);

        Log.d(TAG, "Pending Intent registered");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Intent
     * */

    //restores the intent receiving
    public void onResume(){
        super.onResume();

        receiver =   new FLPBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constants.FLD_IDENTIFIER));

        Log.i(TAG, "Registered for IntentFilter");
    }

    //suspends the intent receiving
    public void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
        super.onPause();
    }

    //private class, manages the intents coming from FLPIntentService
    public class FLPBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "onReceive intent");

            // handle the intent
            String action = intent.getAction();
            Log.i(TAG, "Intent action: " + action);
            if (intent.hasExtra(Constants.FLD_INTENT)) {

                //Get intent sent by onHandleIntent
                Location location = intent.getParcelableExtra(Constants.FLD_INTENT);
                Log.i(TAG, "Letta location: " + location);
                SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String format = s.format(new Date());

                mLatitudeText.setText(String.valueOf(location.getLatitude()));
                mLongitudeText.setText(String.valueOf(location.getLongitude()));
                timeUpdateText.setText(format);
            }
        }
    }
}