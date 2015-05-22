package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;


public class AddPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private EditText nameET;
    private EditText addressET;
    private Button pickButton;
    private Button addButton;
    private TextView latTV;
    private TextView lngTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        nameET = (EditText) findViewById(R.id.nameEditText);
        addressET = (EditText) findViewById(R.id.addressEditText);
        pickButton = (Button) findViewById(R.id.pickerButton);
        latTV = (TextView) findViewById(R.id.latTextView);
        lngTV = (TextView) findViewById(R.id.lngTextView);
        addButton = (Button) findViewById(R.id.addPlaceButton);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    Context context = getApplicationContext();
                    try {
                        startActivityForResult(builder.build(context), 1);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddPlaceActivity.this,"Service temporary unavalaible",Toast.LENGTH_SHORT).show();
                }
            }
        });




            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGoogleApiClient.isConnected()) {

                        String name = nameET.getText().toString();
                        String address = addressET.getText().toString();
                        String lat = latTV.getText().toString();
                        String lng = lngTV.getText().toString();
                        if (name.isEmpty() || address.isEmpty()) {
                            Toast.makeText(AddPlaceActivity.this, "Please fill the form completely", Toast.LENGTH_SHORT).show();
                        }
                        AddPlaceRequest place = new AddPlaceRequest(
                                name, // Name
                                new LatLng(Double.valueOf(lat), Double.valueOf(lng)), // Latitude and longitude
                                address, // Address
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
                                                "Place added succssfully",
                                                Toast.LENGTH_LONG).show();
//                            Toast.makeText(AddPlaceActivity.this,
//                                    "Place add result: " + places.getStatus().toString(),
//                                    Toast.LENGTH_LONG).show();
//                            Toast.makeText(AddPlaceActivity.this,
//                                    "Added place: " + places.get(0).getName().toString(),
//                                    Toast.LENGTH_LONG).show();
                                        places.release();
                                    }
                                });
                    } else {
                        Toast.makeText(AddPlaceActivity.this,"Service temporary unavalaible",Toast.LENGTH_SHORT).show();
                    }

                }
            });
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
        Log.i(Constants.PLACE_TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(Constants.PLACE_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(Constants.PLACE_TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                final LatLng latlng = place.getLatLng();
                latTV.setText(String.valueOf(place.getLatLng().latitude));
                lngTV.setText(String.valueOf(place.getLatLng().longitude));

            } else {
                this.finish();
            }
        }
    }
}
