package com.example.xyzreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Katrina on 11/05/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    DetailsFragment mDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(findViewById(R.id.fragment_details)!=null) {


            if(savedInstanceState!=null)
            {
                return;
            }
            mDetails = new DetailsFragment();
            mDetails.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_details,mDetails).commit();
        }


    }
}
