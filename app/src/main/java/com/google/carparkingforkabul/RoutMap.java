package com.google.carparkingforkabul;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RoutMap extends AppCompatActivity {
    // Initialize variable
    EditText etSource, etDestination;
    Button btTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rout_map);
        // Assign variable
        etSource=findViewById(R.id.et_source);
        etDestination=findViewById(R.id.et_destination);
        btTrack=findViewById(R.id.bt_track);
        btTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get value from EditText
                String sSource=etSource.getText().toString().trim();
                String sDestination=etDestination.getText().toString().trim();
                // Check Condition
                if (sSource.equals("")&&sDestination.equals(""))
                {
                    //when both value blank
                    Toast.makeText(getApplicationContext(),"Enter both Location",Toast.LENGTH_SHORT).show();
                }else
                {
                    //When both value fill
                    //Display Track
                    DisplayTrack(sSource,sDestination);
                }




            }
        });

    }

    private void DisplayTrack(String sSource, String sDestination) {
        //If a Device does not have map installed, then redirect it to Play store
        try
        {
            //If map installed
            // Initialized URI
            Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+sSource+"/"+sDestination);
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            //Set Package
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            // Start Activity
            startActivity(intent);

        }catch (ActivityNotFoundException e)
        {
            // if Google map is not installed
            // initialize URi
            Uri uri=Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            // Initialize Intent with action view
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            // set flags
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }
}
