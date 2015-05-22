package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class PlacesMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_main);

        final Button button  = (Button) findViewById(R.id.placePickerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PlacesMainActivity.this, PlacePickerDemo.class);
                startActivity(intent);
            }
        });

        final Button addPlaceButton  = (Button) findViewById(R.id.addPlaceButton);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PlacesMainActivity.this, AddPlaceActivity.class);
                startActivity(intent);
            }
        });

        final Button reportPlaceButton  = (Button) findViewById(R.id.reportPlaceButton);
        reportPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PlacesMainActivity.this, ReportPlaceActivity.class);
                startActivity(intent);
            }
        });
    }
}
