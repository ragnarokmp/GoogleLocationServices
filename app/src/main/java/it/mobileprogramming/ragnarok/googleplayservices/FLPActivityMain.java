package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class FLPActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flp);

        flpLastLocationActivity();
        flpLocationListenersActivity();
        flpPendingIntentActivity();
    }

    /**
     * Start Activity for show  last location maintained by the system
     * */
    public void flpLastLocationActivity() {

        final Button button  = (Button) findViewById(R.id.buttonLastLocation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(FLPActivityMain.this, FLPActivityLastLocation.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Start Activity for request location by LocationListeners
     * */
    public void flpLocationListenersActivity() {

        final Button button  = (Button) findViewById(R.id.buttonListener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(FLPActivityMain.this, FLPActivityLocationListeners.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Start Activity for request location by PendingIntent
     * */
    public void flpPendingIntentActivity() {

        final Button button  = (Button) findViewById(R.id.buttonPendingIntent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(FLPActivityMain.this, FLPActivityLocationPendingIntent.class);
                startActivity(intent);
            }
        });
    }
}
