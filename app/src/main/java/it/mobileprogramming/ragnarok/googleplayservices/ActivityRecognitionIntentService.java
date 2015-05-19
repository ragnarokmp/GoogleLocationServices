package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;

/**
 * Created by paride on 15/05/15.
 * This class manages the intents handling the background updates about Activity Recognition
 */
public class ActivityRecognitionIntentService extends IntentService {
    public ActivityRecognitionIntentService(){
        super("");
    }
    public ActivityRecognitionIntentService(String name) {
        super(name);
    }

    @Override
    //checks if the external intent is an ActivityRecognitionResult, creates a new internal intent
    //and sends it to the receivers
    protected void onHandleIntent(Intent intent) {
        System.out.println("ARRIVATO intent attivita");
        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result                  =   ActivityRecognitionResult.extractResult(intent);
            Intent myIntent                                    =   new Intent(Constants.ACTION_IDENTIFIER);
            myIntent.putExtra("allActivities", result);
            LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
        }
    }
}