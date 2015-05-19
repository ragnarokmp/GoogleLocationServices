package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


public class Utilities {
    public static String getActivityName(int activity,Context aContext){
        String result;
        switch (activity){
            case DetectedActivity.IN_VEHICLE:
                result  = aContext.getString(R.string.ARD_in_vehicle);
                break;
            case DetectedActivity.ON_BICYCLE:
                result  = aContext.getString(R.string.ARD_on_bicycle);
                break;
            case DetectedActivity.ON_FOOT:
                result  = aContext.getString(R.string.ARD_on_foot);
                break;
            case DetectedActivity.RUNNING:
                result  = aContext.getString(R.string.ARD_running);
                break;
            case DetectedActivity.STILL:
                result  = aContext.getString(R.string.ARD_still);
                break;
            case DetectedActivity.TILTING:
                result  = aContext.getString(R.string.ARD_tilting);
                break;
            case DetectedActivity.WALKING:
                result  = aContext.getString(R.string.ARD_walking);
                break;
            case DetectedActivity.UNKNOWN:
                result  = aContext.getString(R.string.ARD_unknown);
                break;
            default:
                result  = aContext.getString(R.string.ARD_unknown_activity);
                break;
        }
        return result;
    }

    public static int getActivityImage(int activity,Context aContext){
        int result;
        switch (activity){
            case DetectedActivity.IN_VEHICLE:
                result  = R.drawable.ard_car;
                break;
            case DetectedActivity.ON_BICYCLE:
                result  = R.drawable.ard_bike;
                break;
            case DetectedActivity.ON_FOOT:
                result  = R.drawable.ard_footsteps;
                break;
            case DetectedActivity.RUNNING:
                result  = R.drawable.ard_run;
                break;
            case DetectedActivity.STILL:
                result  = R.drawable.ard_still;
                break;
            case DetectedActivity.TILTING:
                result  = R.drawable.ard_tilt;
                break;
            case DetectedActivity.WALKING:
                result  = R.drawable.ard_walk;
                break;
            case DetectedActivity.UNKNOWN:
                result  = R.drawable.ard_unknown;
                break;
            default:
                result  = -1;
                break;
        }
        return result;
    }

    //there is not an explicit difference from 1st level to 2nd level activities, this function
    //will return a 2nd level activity if necessary
    public static DetectedActivity getSecondaryMostProbableActivity(DetectedActivity mostProbable,ArrayList<DetectedActivity> activityList){
        DetectedActivity result  =   mostProbable;
        int mostProbableType        =   mostProbable.getType();
        int mostProbableConfidence  =   mostProbable.getConfidence();
        if(mostProbableType== DetectedActivity.ON_FOOT){
            for(int i=0;i<activityList.size();i++){
                DetectedActivity currentActivity    =   activityList.get(i);
                if(
                        ((currentActivity.getType()== DetectedActivity.WALKING)||
                                (currentActivity.getType()== DetectedActivity.RUNNING))
                        &&(currentActivity.getConfidence()==mostProbableConfidence)){
                    result  =   currentActivity;
                }
            }
        }
        else if(mostProbableType== DetectedActivity.IN_VEHICLE){
            for(int i=0;i<activityList.size();i++){
                DetectedActivity currentActivity    =   activityList.get(i);
                if((currentActivity.getType()== DetectedActivity.ON_BICYCLE)&&(currentActivity.getConfidence()==mostProbableConfidence)){
                    result  =   currentActivity;
                }
            }
        }
        return result;
    }
}
