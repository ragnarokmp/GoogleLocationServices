package it.mobileprogramming.ragnarok.googleplayservices;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;


public class AddPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        AddPlaceRequest place = new AddPlaceRequest(
                        "Manly Sea Life Sanctuary", // Name
                        new LatLng(-33.7991, 151.2813), // Latitude and longitude
                        "W Esplanade, Manly NSW 2095", // Address
                        Collections.singletonList(Place.TYPE_AQUARIUM), // Place types
                        "+61 1800 199 742", // Phone number
                        Uri.parse("http://www.manlysealifesanctuary.com.au/") // Website
                        );

        Places.GeoDataApi
                .addPlace(mGoogleApiClient, place)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        Toast.makeText(AddPlaceActivity.this,
                                "Place add result: " + places.getStatus().toString(),
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(AddPlaceActivity.this,
                                "Added place: " + places.get(0).getName().toString(),
                                Toast.LENGTH_LONG).show();
                        places.release();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
