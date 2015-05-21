package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the handling of the intent to retrieve the geofence transitions
 * updates givent by the LocationServices
 */

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(Constants.GEO_TRANSITION_SERVICE_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    // receives intent from the system
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            // in case of error in geofencing event let us notify the user in order
            // to stop the geofence monitoring in the worst case of all
            String errorMessage = String.valueOf(geofencingEvent.getErrorCode());
            Log.e(Constants.GEO_TRANSITION_SERVICE_TAG, errorMessage);
            sendNotification(Constants.GEO_TRANSITION_SERVICE_TAG + errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){

            // Get the geofences that were triggered. A single event can trigger multiple geofences!
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i(Constants.GEO_TRANSITION_SERVICE_TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(Constants.GEO_TRANSITION_SERVICE_TAG, "invalid transition");
        }
    }


    // getting the transitions detais
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     */
    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), GeofencingActivity.class);

        // using a stackBuilder to send the notification for the pending intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(GeofencingActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

   // retrieving transition's type definition
    private String getTransitionString(int transitionType) {
        Resources res = getApplication().getResources();
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return res.getString(R.string.GEO_alert_enter);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return res.getString(R.string.GEO_alert_exit);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return res.getString(R.string.GEO_alert_dwell);
            default:
                return "";
        }
    }
}
