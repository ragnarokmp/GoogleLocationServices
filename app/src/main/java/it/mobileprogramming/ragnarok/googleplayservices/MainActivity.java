package it.mobileprogramming.ragnarok.googleplayservices;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide action bar (Design reason)
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        //listeners
        flpApiActivity();
        geoApiActivity();
        plaApiActivity();
        acrApiActivity();
    }

    /**
     * Start Fused Location Provider Activity
     * */
    public void flpApiActivity() {

        final Button button  = (Button) findViewById(R.id.buttonFLP);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, FLPActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Start Geofencing API Activity
     * */
    public void geoApiActivity() {

        final Button button  = (Button) findViewById(R.id.buttonGEO);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                //intent.putExtra(MESSAGE, text);
                //intent.putExtra(PRICE, String.valueOf(total)+ "0" + "€");
                //startActivity(intent);
            }
        });
    }

    /**
     * Start Places API Activity
     * */
    public void plaApiActivity() {

        final Button button  = (Button) findViewById(R.id.buttonPLA);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                //intent.putExtra(MESSAGE, text);
                //intent.putExtra(PRICE, String.valueOf(total)+ "0" + "€");
                //startActivity(intent);
            }
        });
    }

    /**
     * Start Activity Recognition Activity
     * */
    public void acrApiActivity() {

        final Button button  = (Button) findViewById(R.id.buttonACR);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                //intent.putExtra(MESSAGE, text);
                //intent.putExtra(PRICE, String.valueOf(total)+ "0" + "€");
                //startActivity(intent);
            }
        });
    }
}
