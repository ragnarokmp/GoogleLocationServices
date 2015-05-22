package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * This class manages the intents handling the background updates about Fused Location Provider
 */
public class FLPIntentService extends IntentService {

    protected static final String TAG = "Location-IntentService";

    /**
     * Creates an IntentService.
     */
    public FLPIntentService() {
        super("");
    }

    @Override
    //Receive intent from system
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Intent received");

        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {

            Location location = intent.getParcelableExtra("com.google.android.location.LOCATION");

            if (location != null){
                Log.d(TAG, "Location! " + location.toString());

                Intent myIntent = new Intent(Constants.FLD_IDENTIFIER);
                myIntent.putExtra(Constants.FLD_INTENT, location);

                boolean result = LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
                Log.i(TAG, "Sent intent to broadcast manager: " + result);
            }
        }
    }
}
