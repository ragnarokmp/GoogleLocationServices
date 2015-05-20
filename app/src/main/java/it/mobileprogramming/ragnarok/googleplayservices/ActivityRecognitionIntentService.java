package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;

/**
 * This class manages the intents handling the background updates about Activity Recognition
 */
public class ActivityRecognitionIntentService extends IntentService {

    public ActivityRecognitionIntentService(){
        super("");
    }

    @Override
    //checks if the external intent is an ActivityRecognitionResult, creates a new internal intent
    //and sends it to the receivers
    protected void onHandleIntent(Intent intent) {

        System.out.println("ActivityRecognition Intent Received");

        if(ActivityRecognitionResult.hasResult(intent)){

            ActivityRecognitionResult result    = ActivityRecognitionResult.extractResult(intent);

            Intent myIntent                     = new Intent(Constants.ACTION_IDENTIFIER);
            myIntent.putExtra("allActivities", result);

            LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
        }
    }
}