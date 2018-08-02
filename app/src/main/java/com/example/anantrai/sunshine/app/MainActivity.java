package com.example.anantrai.sunshine.app;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{
    private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.detail_fragment) != null){
            mTwoPane = true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment,new BlankFragment(),DETAILFRAGMENT_TAG).commit();

            }

        } else{
            mTwoPane = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation(this);

        if(location!=null && !location.equals(mLocation)) {
            MainActivityFragment ff = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment);
            if(ff!=null) {
                ff.onLocationChanged();
            }
            BlankFragment bf =(BlankFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if( null != bf){
                bf.onLocationChanged(location);
            }
            mLocation = location;
        }
    }


    @Override
    public void onItemSelected(Uri contentUri){
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(BlankFragment.DETAIL_URI,contentUri);

            BlankFragment fragment = new BlankFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment,fragment,DETAILFRAGMENT_TAG).commit();
        }else{
            Intent intent = new Intent(this,DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.action_map) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Uri intentUri = Uri.parse("geo:0,0?q=" + sharedPreferences.getString(getString(R.string.location_key), getString(R.string.default_zip_code)));
            Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
            PackageManager pm = getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isTwoPane(){
        return mTwoPane;
    }
}
