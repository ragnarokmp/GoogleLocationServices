package it.mobileprogramming.ragnarok.googleplayservices;


public class Constants {

    /**
     * Activity Recognition
     * */
    public final static String ACTION_IDENTIFIER            =   "LOCATION"; //ID for internal intent
    public final static int ACTIVITY_MEASUREMENT_INTERVAL   =   0;          //interval for activity measurement (0 indicates shortest interval possible)

    /**
     * Fused Location Provider
     * */
    public final static String FLD_IDENTIFIER               =   "FLD_LOCATION"; //ID for internal intent
    public final static String FLD_INTENT                   =   "FLD_INTENT";   //ID for internal intent

    /**
     * Geofencing
     */
    public final static String GEO_TAG                      =   "GEO";          //TAGs for debug purposes
    public static final String GEO_TRANSITION_SERVICE_TAG   =   "geofencing-transition-intent-service";
    public final static String GEO_IDENTIFIER               =   "GEOFENCE";     //ID for internal intent
    public final static long GEOFENCE_MINEXPIRATION_MILLI   =   1000*60*10;     //Geofence min expiration
    public final static long GEOFENCE_MINRADIUS_METERS      =   50;             //Geofence min perimeter radius
    public final static int GEOFENCE_MINDWELLING_MILLI      =   1000*60;        //Gefeonce min dwelling duration
    public final static int GEO_MAX_INSTANCES               =   100;            //Max available geofences per device

    /**
     * Places
     */
    public final static String PLACE_TAG                    =   "PLACES";          //TAGs for debug purposes
}
