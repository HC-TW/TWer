package com.hc.twer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class EditScheduleActivity extends AppCompatActivity implements CalendarDialogFragment.OnCalendarDialogListener {

    private Toolbar toolbar;
    private boolean BackButtonEnabled = false;
    private int schedulePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        init();

        // if there is a fragment in the container, do not make a new fragment
        if ( findViewById(R.id.edit_fragment_container) != null )
        {
            if ( savedInstanceState != null )
            {
                return;
            }
            // set up Homepage
            setUpHomePage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_schedule_menu, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Fragment currentFragment = fragments.get(fragments.size()-1);
        if (!BackButtonEnabled)
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else if (currentFragment instanceof EditTimeFragment || currentFragment instanceof EditScheduleNameTimeFragment || currentFragment instanceof EditMapFragment
                 || currentFragment instanceof NavigationFragment || currentFragment instanceof AddPlaceChoiceFragment
                 ||(currentFragment instanceof DetailFragment && getSupportFragmentManager().getBackStackEntryCount() == 0))
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean("EDIT_MODE", EditHomeFragment.EDIT_MODE);
            bundle.putInt("schedulePos", schedulePos);
            bundle.putInt("datePos", EditHomeFragment.datePos);
            EditHomeFragment editHomeFragment = new EditHomeFragment();
            editHomeFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, editHomeFragment).commit();
        }
        else
        {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DialogConfirm(List<Date> dates) {
        EditScheduleNameTimeFragment editScheduleNameTimeFragment = (EditScheduleNameTimeFragment) getSupportFragmentManager().findFragmentById(R.id.edit_fragment_container);
        editScheduleNameTimeFragment.DialogConfirm(dates);
    }

    private void init()
    {
        // get schedulePos
        schedulePos = getIntent().getIntExtra("schedulePos", 0);
        // set up ActionBar
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    private void setUpHomePage()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("schedulePos", schedulePos);
        EditHomeFragment editHomeFragment = new EditHomeFragment();
        editHomeFragment.setArguments(bundle);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().add(R.id.edit_fragment_container, editHomeFragment, null).commit();
    }

    public void setBackButtonEnabled(boolean enabled)
    {
        this.BackButtonEnabled = enabled;
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            Fragment currentFragment = fragments.get(fragments.size()-1);
            if (haveNetworkConnection())
            {
                if (currentFragment instanceof EditHomeFragment)
                {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("EDIT_MODE", EditHomeFragment.EDIT_MODE);
                    bundle.putInt("schedulePos", schedulePos);
                    bundle.putInt("datePos", EditHomeFragment.datePos);
                    EditHomeFragment editHomeFragment = new EditHomeFragment();
                    editHomeFragment.setArguments(bundle);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.edit_fragment_container, editHomeFragment).commit();
                }
            }
            else
            {
                Toast.makeText(EditScheduleActivity.this, "未偵測到網路連線，有些功能可能無法正常使用！", Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
