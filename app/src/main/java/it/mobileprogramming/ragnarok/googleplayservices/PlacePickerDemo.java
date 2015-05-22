package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;


public class PlacePickerDemo extends AppCompatActivity {

    private int PLACE_PICKER_REQUEST = 1;
    private TextView mViewName;
    private TextView mViewAddress;
    private TextView mViewAttributions;
    private TextView mViewLat;
    private TextView mViewLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        mViewName = (TextView) findViewById(R.id.nameTextView);
        mViewAddress = (TextView) findViewById(R.id.addressTextView);
        mViewAttributions = (TextView) findViewById(R.id.attrTextView);
        mViewLat = (TextView) findViewById(R.id.latTextView);
        mViewLng = (TextView) findViewById(R.id.lngTextView);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final LatLng latlng = place.getLatLng();
                String attributions = PlacePicker.getAttributions(data);
                if (attributions == null) {
                    attributions = "";
                    mViewAttributions.setVisibility(View.GONE);
                }
                mViewName.setText(name);
                mViewAddress.setText(address);
                mViewAttributions.setText(Html.fromHtml(attributions));
                mViewLat.setText(String.valueOf(latlng.latitude));
                mViewLng.setText(String.valueOf(latlng.longitude));

            } else {
                this.finish();
            }
            }
        }
    }
