package com.example.moodly.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;


import com.example.moodly.Adapters.MoodAdapter;
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Activities.SocialBase;
import com.example.moodly.Activities.TabBase;
import com.example.moodly.Activities.TabHistory;
import com.example.moodly.Controllers.UserController;

import com.example.moodly.R;

public class ViewMoodList extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // checks periodically
        handler = new Handler();
        synchronizeNetwork.run();
    }

    private int repeatInterval = 30000;
    private Handler handler;

    Runnable synchronizeNetwork = new Runnable() {
        @Override
        public void run() {
            try {
                updateElasticSearch();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(synchronizeNetwork, repeatInterval);
            }
        }
    };

    private void updateElasticSearch() {
        if (networkAvailable())  {
            if (MoodController.getInstance().getAddCompletion()) {
                MoodController.getInstance().syncAddList();
            }
            if(MoodController.getInstance().getDeleteCompletion()) {
                MoodController.getInstance().syncDeleteList();
            }
            Toast.makeText(ViewMoodList.this, "Connected", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ViewMoodList.this, "Not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_mood_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.show_map:
                Toast.makeText(this, "Showing Map", Toast.LENGTH_SHORT).show();
                Intent intentMap = new Intent();
                intentMap.setClass(ViewMoodList.this, SeeMap.class);
                startActivity(intentMap);
                return true;
            case R.id.action_social:

                if (networkAvailable()) {
                    Toast.makeText(this, "Social", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, SocialBase.class);
                    startActivity(intent);
                    return super.onOptionsItemSelected(item);
                }
                else{
                    Toast.makeText(this, "Cannot access social tab when offline!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.log_out:
                Toast.makeText(this, "Goodbye, " + UserController.getInstance().getCurrentUser().getName(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor =getApplicationContext().getSharedPreferences(LoginScreen.FILE_NAME, Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Intent logOut = new Intent(this, LoginScreen.class);
                logOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logOut);
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // return current tab
            switch (position) {
                case 0:
                    TabHistory tab1 = new TabHistory();
                    return tab1;
                case 1:
                    TabBase tab2 = new TabBase();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "History";
                case 1:
                    return "Following";
            }
            return null;
        }
    }




}