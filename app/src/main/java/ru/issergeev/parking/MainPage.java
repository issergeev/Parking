package ru.issergeev.parking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yandex.mapkit.MapKitFactory;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String appData = "ParkingData";
    private final String firstStart = "FirstStart";
    private final String first = "First";
    private final String displayedView = "ViewID";
    private final String userNamePrefs = " UserName";
    private final String userLastNamePrefs = " UserLastName";

    private int fragment = 0;
    private int position = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static NavigationView navigationView;

    private ImageView profile;
    private TextView userName, lastName;

    private DB db;
    private static SQLiteDatabase database;
    public static ArrayList<Cars> list;

    private FragmentManager fragmentManager;
    private Fragment paymentFragment;

    @Override
    protected void onResume() {
        userName.setText(preferences.getString(userNamePrefs, ""));
        lastName.setText(preferences.getString("UserLastName", ""));
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = preferences.edit();

        editor.putBoolean(firstStart, false);
        editor.apply();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        position = preferences.getInt(displayedView, 0);
        navigationView.getMenu().getItem(position).setChecked(true);

        profile = navigationView.getHeaderView(0).findViewById(R.id.profilePhoto);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPage.this, ProfileActivity.class));
            }
        });
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        lastName = navigationView.getHeaderView(0).findViewById(R.id.userLastName);

        fragmentManager = getFragmentManager();
        paymentFragment = new Fragment_Payment();
        switch (preferences.getInt(displayedView, 0)) {
            case 0:
                fragmentManager.beginTransaction().add(R.id.main, paymentFragment).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().add(R.id.main, new Fragment_Garage()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().add(R.id.main, new Fragment_Map()).commit();
        }

        MapKitFactory.setApiKey("9ddacc40-6a3d-4de4-ad13-7ffcd37e54a9");
        MapKitFactory.initialize(this);

        list = new ArrayList<>();
        db = new DB(this);
        database = db.getReadableDatabase();
        readDB();
    }

    public static void readDB(){
        //DB
        list.clear();
        String[] allColumns = new String[] { DB.getKeyLicencePlate(), DB.getKeyName(), DB.getKeyCountry()};
        Cursor cursor = database.query(DB.getTableCars(), allColumns, null, null, null,
                null, null);

        if (cursor != null && cursor.moveToFirst()) {
            list.add(new Cars(cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyName())),
                    cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyLicencePlate())),
                    cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyCountry()))));

            while (cursor.moveToNext()) {
                list.add(new Cars(cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyName())),
                        cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyLicencePlate())),
                        cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyCountry()))));
            }
        }

        assert cursor != null;
        cursor.close();
    }

    public static NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        } else if (id == R.id.action_about) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.primary) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Payment(), "replace")
                    .commit();
            fragment = 0;
        } else if (id == R.id.garage) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Garage(), "replace")
                    .commit();
            fragment = 1;
        } else if (id == R.id.map) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Map(), "replace")
                    .commit();
            fragment = 2;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        editor.putBoolean(first, false);
        editor.putInt(displayedView, fragment);
        editor.commit();
        super.onDestroy();
    }
}