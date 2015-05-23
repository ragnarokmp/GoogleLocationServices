package it.mobileprogramming.ragnarok.googleplayservices;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ActivityRecognitionMain extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient googleApiClient=null;
    //private TextView tvActivities;
    private TextView tvNamesofActivities;
    private TextView tvLog;
    private String activityLog="";
    private ImageView ivCurrentActivity;
    private TextView tvMostProbableConfidence;
    private ActivityDetectionBroadcastReceiver  receiver;
    private DetectedActivity lastMostProbableLogged=null;
    private ProgressBar progress;

    //saves UI elements and creates an API client
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_recognition_main);
        //this.tvActivities       =   (TextView)this.findViewById(R.id.ARD_tvActivityResults);
        this.tvLog              =   (TextView)this.findViewById(R.id.ARD_tvActivityLog);
        this.ivCurrentActivity  =   (ImageView)this.findViewById(R.id.ARD_ivActivityIcon);
        this.tvNamesofActivities=   (TextView)this.findViewById(R.id.ARD_tvIndexes);
        this.tvMostProbableConfidence   =   (TextView)this.findViewById(R.id.ARD_tvConfidence);
        this.ivCurrentActivity.setImageResource(R.drawable.ard_unknown);
        this.progress = (ProgressBar)this.findViewById(R.id.progressBar);
        googleApiClient =   new GoogleApiClient.Builder(this).
                addApi(ActivityRecognition.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();
    }

    public void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop(){
        super.onStop();
        googleApiClient.disconnect();
    }

    //when Play Services connects attach to Activity Recognition service with ActivityRecognitionIntentService that receives the intents
    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("PLAY sono connesso");
        Intent anIntent    = new Intent(this,ActivityRecognitionIntentService.class);
        PendingIntent aPendingIntent    =   PendingIntent.getService(this, 0, anIntent, PendingIntent.FLAG_UPDATE_CURRENT); //gets an intent service to use as a callback for a recognition event
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(this.googleApiClient,Constants.ACTIVITY_MEASUREMENT_INTERVAL, aPendingIntent);
        System.out.println("REGISTRAZIONE intent completata");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //restores the intent receiving
    public void onResume(){
        super.onResume();
        receiver =   new ActivityDetectionBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter(Constants.ACTION_IDENTIFIER));
    }

    //suspends the intent receiving
    public void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
        super.onPause();
    }

    //updates the view with the current activity list with their confidence levels
    public void updateActivitiesList(ArrayList<DetectedActivity> activities){

        //hide progress bar
        progress.setVisibility(View.GONE);

        String values           =   " ";
        String activitiesNames  =   "";
        for(int i=0;i<activities.size();i++){
            DetectedActivity thisActivity       =   activities.get(i);
            String activityName                 =   Utilities.getActivityName(thisActivity.getType(),this.getApplicationContext());
            //values                              =   values+thisActivity.getConfidence()+"%\n";
            activitiesNames                     =   activitiesNames+activityName+" "+this.getString(R.string.ARD_confidence)+values+thisActivity.getConfidence()+"%\n";
        }
        //this.tvActivities.setText(values);
        this.tvNamesofActivities.setText(activitiesNames);
    }

    //updates the log textview
    public void addToLog(DetectedActivity anActivity){
        Date now                =   new Date();
        SimpleDateFormat sdf    =   new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if(this.lastMostProbableLogged!=null){
            if(this.lastMostProbableLogged.getType()==anActivity.getType()){
                if(this.lastMostProbableLogged.getConfidence()==anActivity.getConfidence()){
                    return;
                }
            }
        }
        this.lastMostProbableLogged =   anActivity;
        this.activityLog            =   sdf.format(now)+" "+
                Utilities.getActivityName(anActivity.getType(),getApplicationContext())+" "+
                this.getString(R.string.ARD_confidence)+anActivity.getConfidence()+"%\n"+this.activityLog;
        this.tvLog.setText(this.activityLog);
    }

    //sets the most probable activity found
    public void setMostProbableActivity(DetectedActivity mostProbableActivity) {

        int imageResourceId =   Utilities.getActivityImage(mostProbableActivity.getType(),getApplicationContext());
        String actDescr     =   Utilities.getActivityName(mostProbableActivity.getType(),getApplicationContext());

        this.tvMostProbableConfidence.setText(actDescr
                + "\n"
                + this.getString(R.string.ARD_confidence)
                + "\t"+ mostProbableActivity.getConfidence()
                + "%");

        this.ivCurrentActivity.setImageResource(imageResourceId);
    }

    //private class, manages the intents coming from ActivityRecognitionIntentService
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ActivityRecognitionResult aResult           =   intent.getParcelableExtra("allActivities");
            ArrayList<DetectedActivity> activitiesList  = (ArrayList<DetectedActivity>) aResult.getProbableActivities();

            updateActivitiesList(activitiesList);

            //calls this method because gives priority to 2nd level activity if has the same confidence level
            DetectedActivity mostProbable  = Utilities.getSecondaryMostProbableActivity(aResult.getMostProbableActivity(),activitiesList);

            addToLog(mostProbable);
            setMostProbableActivity(mostProbable);

            /*for(int i=0;i<activitiesList.size();i++){
                DetectedActivity    temp    =   activitiesList.get(i);
                System.out.println(temp.toString());
            }*/ //block for testing purpose
        }
    }

}
