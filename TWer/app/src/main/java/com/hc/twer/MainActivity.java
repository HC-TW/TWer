package com.hc.twer;


import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private boolean TbackButtonEnabled = false;
    private boolean SbackButtonEnabled = false;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private LinearLayout profile;
    private ImageView loginPhoto;
    private TextView loginName;
    private TextView loginEmail;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;

    private BackupManager backupManager;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_ADD_SCHEDULE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( findViewById(R.id.fragment_container) != null )
        {
            if ( savedInstanceState != null )
            {
                return;
            }
            // set up Homepage
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeFragment(), null).commit();
        }

        init();

        setupDrawerWithNavigationView();
        Log.d("VERSION", "Version: " + Build.VERSION.SDK_INT);
        Log.d("VERSION", "Version: " + Build.VERSION.RELEASE);
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

    // set up menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    // open the drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add:
                startActivityForResult(new Intent(this, AddScheduleActivity.class), REQUEST_CODE_ADD_SCHEDULE);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.noslide);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            // result for signing in
            case REQUEST_CODE_SIGN_IN:
                if ( resultCode == RESULT_OK )
                {
                    updateViewWithGoogleSignInAccountTask(mGoogleSignInClient.silentSignIn());
                }
                break;
            // result for adding schedule
            case REQUEST_CODE_ADD_SCHEDULE:
                if ( resultCode == RESULT_OK )
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), null).commitAllowingStateLoss();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (TbackButtonEnabled || SbackButtonEnabled)
        {
            getSupportFragmentManager().popBackStack();
        }
        else
        {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // Initialize
    private void init() {


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationview);
        View navigationHeader = navigationView.getHeaderView(0);
        profile = navigationHeader.findViewById(R.id.profile);
        signInButton = navigationHeader.findViewById(R.id.sign_in_button);
        loginPhoto = navigationHeader.findViewById(R.id.login_album);
        loginName = navigationHeader.findViewById(R.id.login_name);
        loginEmail = navigationHeader.findViewById(R.id.login_email);
        // set up ActionBar
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // set drawer button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        // create google sign in client
        mGoogleSignInClient = buildGoogleSignInClient();
        // check if the account have been logged in
        updateViewWithGoogleSignInAccountTask(mGoogleSignInClient.silentSignIn());

        navigationView.setCheckedItem(R.id.nav_myschedule);

        // set TAIWAN's language
        Configuration config = getResources().getConfiguration();
        Locale.setDefault(Locale.TAIWAN);
        config.locale = Locale.TAIWAN;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    // set up drawer(left side menu) and navigationView(menu content)
    private void setupDrawerWithNavigationView() {
        // set up google account sign-in button (header)
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection())
                {
                    MainActivity.this.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "請先檢查是否連線，再進行登入！", Toast.LENGTH_LONG).show();
                }
            }
        });
        // set up navigationView item (menu)
        NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navigationMenuView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // set up navigationView item selected event
        navigationView.setNavigationItemSelectedListener(item -> {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            Fragment currentFragment = fragments.get(fragments.size()-1);
            switch (item.getItemId())
            {
                // go to my schedule page
                case R.id.nav_myschedule:
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    if (!(currentFragment instanceof HomeFragment))
                    {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full).replace(R.id.fragment_container, new HomeFragment(), null).commit();
                    }
                    return true;
                // go to search places page
                case R.id.nav_search:
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    if (!(currentFragment instanceof SearchFragment))
                    {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full).replace(R.id.fragment_container, new SearchFragment(), null).commit();
                    }
                    return true;
                // go to CollectFragment
                case R.id.nav_collect:
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    if (!(currentFragment instanceof CollectFragment))
                    {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full).replace(R.id.fragment_container, new CollectFragment(), null).commit();
                    }
                    return true;
                // go to TravelsFirstFragment
                case R.id.nav_travels:
                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    if (!(currentFragment instanceof TravelsFirstFragment))
                    {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full).replace(R.id.fragment_container, new TravelsFirstFragment(), null).commit();
                    }
                    return true;
                // logout google account
                case R.id.nav_logout:
                    item.setChecked(true);
                    // alert message
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("確定要登出嗎？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mGoogleSignInClient.signOut();
                                    profile.setVisibility(View.GONE);
                                    signInButton.setVisibility(View.VISIBLE);
                                }
                            })
                            .show();
                    return true;
                default:
                    return false;
            }
        });
    }
    // Travels back button
    public void setTbackButtonEnabled(boolean enabled)
    {
        this.TbackButtonEnabled = enabled;
        backButtonChecked();
    }
    // Search back button
    public void setSbackButtonEnabled(boolean enabled)
    {
        this.SbackButtonEnabled = enabled;
        backButtonChecked();
    }
    // if back button enabled, show it
    private void backButtonChecked()
    {
        if (TbackButtonEnabled || SbackButtonEnabled)
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
        else
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }
    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleSignInClient()
    {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    private void updateViewWithGoogleSignInAccountTask(final Task<GoogleSignInAccount> task)
    {
        Log.i("TAG", "Update view with sign in account task");
        task.addOnSuccessListener(
                new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.i("TAG", "Sign in success");

                        // get user information
                        loginName.setText(googleSignInAccount.getDisplayName());
                        loginEmail.setText(googleSignInAccount.getEmail());
                        Glide.with(MainActivity.this).load(googleSignInAccount.getPhotoUrl()).into(loginPhoto);
                        profile.setVisibility(View.VISIBLE);
                        signInButton.setVisibility(View.GONE);
                        /*
                        // restore
                        backupManager.requestRestore(new RestoreObserver() {
                            @Override
                            public void restoreFinished(int error) {
                                super.restoreFinished(error);
                            }
                        });*/
                        /*
                        // Build a drive client.
                        mDriveClient = Drive.getDriveClient(getApplicationContext(), googleSignInAccount);
                        // Build a drive resource client.
                        mDriveResourceClient =
                                Drive.getDriveResourceClient(getApplicationContext(), googleSignInAccount);*/
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Sign in failed", e);
                                profile.setVisibility(View.GONE);
                                signInButton.setVisibility(View.VISIBLE);
                            }
                        });
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (haveNetworkConnection())
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                Fragment currentFragment = fragments.get(fragments.size()-1);

                if (currentFragment instanceof SearchFragment)
                {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchFragment(), null).commit();
                }
                else if (currentFragment instanceof CollectFragment)
                {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new CollectFragment(), null).commit();
                }
            }
            else
            {
                Toast.makeText(MainActivity.this, "未偵測到網路連線，有些功能可能無法正常使用！", Toast.LENGTH_LONG).show();
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
