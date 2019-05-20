package ru.issergeev.parking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String appData = "ParkingData";
    private final String first = "First";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private DB db;
    private SQLiteDatabase database;
    public static ArrayList<Cars> list;

    private FragmentManager fragmentManager;
    private Fragment paymentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = preferences.edit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        fragmentManager = getFragmentManager();
        paymentFragment = new Fragment_Payment();
        fragmentManager.beginTransaction().add(R.id.main, paymentFragment).commit();

        MapKitFactory.setApiKey("9ddacc40-6a3d-4de4-ad13-7ffcd37e54a9");
        MapKitFactory.initialize(this);

        //DB
        list = new ArrayList<>();
        db = new DB(this);
        database = db.getReadableDatabase();
        String[] allColumns = new String[] { DB.getKeyName(), DB.getKeyLicencePlate()};
        Cursor cursor = database.query(DB.getTableCars(), allColumns, null, null, null,
                null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("log", cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyName())) + " " + cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyLicencePlate())));
            list.add(new Cars(cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyName())), cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyLicencePlate()))));

            while (cursor.moveToNext()) {
                list.add(new Cars(cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyName())), cursor.getString(cursor.getColumnIndexOrThrow(DB.getKeyLicencePlate()))));
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_cars_text), Toast.LENGTH_SHORT).show();
        }

        assert cursor != null;
        cursor.close();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.primary) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Payment(), "replace")
                    .commit();
        } else if (id == R.id.garage) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Garage(), "replace")
                    .commit();
        } else if (id == R.id.map) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main, new Fragment_Map(), "replace")
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        editor.putBoolean(first, false);
        editor.apply();
        super.onDestroy();
    }
}