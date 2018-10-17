package com.example.myapplication;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.kl.background.KLService;
import com.kl.data.DatabaseDefinition;
import com.kl.data.DbHelper;
import com.kl.ui.fragments.RadioFavoritesFragment;
import com.kl.ui.fragments.RadioPodcastsFragment;
import com.kl.ui.fragments.RadioSettingsFragment;
import com.kl.ui.fragments.RadioStationFragment;
import com.kl.utils.Logger;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    // A reference to the service used to get location updates.
    private KLService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFloatingActionButton;
    private View mToolbar;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.getLogger().e("[INF] my service connected!");
            KLService.LocalBinder binder = (KLService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            String radioUrl;
            // radioUrl = "https://manehattan.bronytunes.com/stream-128.btr";
            // radioUrl = "http://streaming213.radionomy.com/80s90sPARTYHITS";// EDM
            // radioUrl = "http://mbsradio.leanstream.co/CFQMFM-MP3"; // CFQM Max FM 103.9 (CA Only) - live
            // radioUrl = "http://mbsradio.leanstream.co/CKCWFM-MP3?web_01=args"; // CKCW K94.5 - live
            radioUrl = "http://199.115.115.71:8319/;"; // CVCR - Valley Christian Radio
            // radioUrl = "http://s3.voscast.com:7820/;stream1370537750222/1;nop.mp3"; // VOAR Christian Family Radio
            // KLPlaybackManager.getPlaybackManager().playAudio(radioUrl);

            mService.handlePlayRequest();
        }

        /**
         * Called when a connection to the Service has been lost.
         * This typically happens when the process hosting the service has crashed or been killed.
         * This does not remove the ServiceConnection itself -- this binding to the service will remain active,
         * and you will receive a call to onServiceConnected(ComponentName, IBinder) when the Service is next running.
         * @param name ComponentName: The concrete component name of the service whose connection has been lost.
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.getLogger().e("[INF] my service disconnected!");
            mService = null;
            mBound = false;
        }

    };

    private FragmentManager mFragmentManager = getSupportFragmentManager();

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_radio_stations:
                    //mTextMessage.setText(R.string.title_radio_station);

                    mFragmentManager.beginTransaction()
                            .replace(R.id.holder, new RadioStationFragment())
                            //.addToBackStack(RadioStationFragment.class.getSimpleName())
                            .commit();

                    return true;

                case R.id.nav_radio_fav:
                    //mTextMessage.setText(R.string.title_radio_favorites);

                    mFragmentManager.beginTransaction()
                            .replace(R.id.holder, new RadioFavoritesFragment())
                            //.addToBackStack(RadioFavoritesFragment.class.getSimpleName())
                            .commit();

                    return true;

                case R.id.nav_radio_podcast:
                    //mTextMessage.setText(R.string.title_radio_podcasts);

                    mFragmentManager.beginTransaction()
                            .replace(R.id.holder, new RadioPodcastsFragment())
                            //.addToBackStack(RadioPodcastsFragment.class.getSimpleName())
                            .commit();

                    return true;

                case R.id.nav_radio_settings:
                    //mTextMessage.setText(R.string.title_radio_settings);

                    mFragmentManager.beginTransaction()
                            .replace(R.id.holder, new RadioSettingsFragment())
                            //.addToBackStack(RadioSettingsFragment.class.getSimpleName())
                            .commit();
                    return true;
            }

            return false;
        }
    };

    private long mBackPressed;
    @Override
    public void onBackPressed() {
        // close opened drawer first - if any
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        // do show confirm exit when press back (each check for 2 seconds)
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            // super.onBackPressed();
            finish();
            return;
        } else {
            // show Snackbar at the top of BottomNavigationView
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.confirm_exit_text,
                    Snackbar.LENGTH_SHORT);

            View view = snackbar.getView();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();

            View navigation = findViewById(R.id.navigation);
            params.setMargins(0, 0, 0, navigation.getHeight());

            view.setLayoutParams(params);
            snackbar.show();
        }

        mBackPressed = System.currentTimeMillis();


//        int backStackCount = mFragmentManager.getBackStackEntryCount();
//        Logger.getLogger().e("[INF] back stack count = " + backStackCount);
//        if (backStackCount > 0) {
//            // mFragmentManager.popBackStack();
//
//            FragmentManager.BackStackEntry backStackEntryOnTop = mFragmentManager.getBackStackEntryAt(0);
//
//            // check if we only have screen [A] on top, then finish/close application
//            if (backStackCount == 1 &&
//                    "A".equals(backStackEntryOnTop.getName())) {
//                finish();
//                return;
//            }
//
//            // clear all back stack then put screen [A] to top.
//            mFragmentManager.popBackStack(backStackEntryOnTop.getId(),
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//            mFragmentManager.beginTransaction()
//                    .replace(R.id.holder, new RadioStationFragment())
//                    .addToBackStack("A")
//                    .commit();
//        }
//        else {
//            super.onBackPressed();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setTheme(R.style.DarkTheme);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow(); // in Activity's onCreate() for instance

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }*/ // was set by theme defined in styles.xml

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mFloatingActionButton = findViewById(R.id.floatingActionButton);

        mToolbar = findViewById(R.id.myToolbar);

        // setup bottom bottom_navigation bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // setup drawer layout
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.drawer_radio) {
                            // skip
                            mDrawerLayout.closeDrawers();
                            menuItem.setChecked(true);
                            return true;
                        }

                        // set item as selected to persist highlight
                        // menuItem.setChecked(true);

                        //navigationView.setCheckedItem(R.id.drawer_radio);
                        mDrawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                navigationView.getMenu().getItem(0).setChecked(true);
                            }
                        }, 300);

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Logger.getLogger().e("[INF] item ["+menuItem.getTitle()+"] selected");

                        Toast.makeText(getApplicationContext(), "Feature coming soon.", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });

        // select Radio Station as default
        navigationView.getMenu().getItem(0).setChecked(true);

        //insertDataToDb();
        showDataFromDb();
    }

    private void insertDataToDb() {
        //Note : Wherever we use Sqlite classes, its all from net.sqlite.database.
        SQLiteDatabase db = DbHelper.getInstance(this).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseDefinition.RadioStation._ID, ""+System.currentTimeMillis());
        values.put(DatabaseDefinition.RadioStation.C_NAME, "CVCR - Valley Christian Radio");
        values.put(DatabaseDefinition.RadioStation.C_STREAMING_URL, "http://199.115.115.71:8319/;");

        db.insert(DatabaseDefinition.RadioStation.TABLE_NAME, null, values);

        db.close();
    }


    private void showDataFromDb() {
        SQLiteDatabase db = DbHelper.getInstance(this).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM '" + DatabaseDefinition.RadioStation.TABLE_NAME + "';", null);
        Logger.getLogger().e("[INF] Rows count: " + cursor.getCount());

        String dbValues = "";

        //if (cursor.moveToFirst()) {
        //    do {
        //        dbValues = dbValues + "\n" + cursor.getString(0) + " , " + cursor.getString(1);
        //    } while (cursor.moveToNext());
        //}

        cursor.close();
        db.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent backgroundIntent = new Intent(this, KLService.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // NV: we need to start our service in other to trigger onStartCommand() on android 6.
            // This way ensure the flag START_STICKY will be used and our service not being killed by doze mode!
            startService(backgroundIntent);
        }

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, KLService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            Logger.getLogger().e("[INF] unbinding service...");
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = Uri.parse("https://google.com");
        String uriText = uri.toString();
        Logger.getLogger().d("[INF] onResume");

        /*mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });*/

        mFragmentManager.beginTransaction()
                .replace(R.id.holder, new RadioStationFragment())
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    private void testing() {
        Thread t = new Thread();
        try {
            t.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(null);
        //LocalBroadcastManager.getInstance(this).registerReceiver(null, null);

        // sendStickyBroadcast(null);

    }
}
