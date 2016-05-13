package com.example.xyzreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.xyzreader.data.UpdaterService;

public class MainActivity extends AppCompatActivity implements NewsList.OnFragmentInteractionListener {


    NewsList mNewsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.fragment_news_list)!=null)
        {
            if(savedInstanceState!=null)
            {
                return;
            }

            mNewsList=NewsList.newInstance();
            mNewsList.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_news_list,mNewsList).commit();
        }


    }


    @Override
    public void onChoice(int id) {
        Intent intent=new Intent(this,DetailsActivity.class);
        intent.putExtra(DetailsFragment.ARG_POSITION,id);

        startActivity(intent);
    }


}
