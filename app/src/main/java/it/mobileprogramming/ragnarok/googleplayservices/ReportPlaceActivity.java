package it.mobileprogramming.ragnarok.googleplayservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;


public class ReportPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String PLACEREPORT_REVIEW = "review";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_place);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
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
        Log.i(Constants.PLACE_TAG, "Connected to GoogleApiClient");

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(final PlaceLikelihoodBuffer likelyPlaces) {
                List<CharSequence> lista = new ArrayList<>();
                final List<String> listId = new ArrayList<>();

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    lista.add(placeLikelihood.getPlace().getName());
                    listId.add(placeLikelihood.getPlace().getId());
                }

                final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(ReportPlaceActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, lista);
                final ListView list = (ListView) findViewById(R.id.listView);

                list.setAdapter(adapter); //Set adapter and that's it.
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String placeId = listId.get(position);
                        final PlaceReport report = PlaceReport.create(placeId, PLACEREPORT_REVIEW);
                        Places.PlaceDetectionApi.reportDeviceAtPlace(mGoogleApiClient, report)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        Toast.makeText(ReportPlaceActivity.this,
                                                "Report place result result: " + status.getStatusMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                        likelyPlaces.release();
                    }
                });
            }
        });
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
}
