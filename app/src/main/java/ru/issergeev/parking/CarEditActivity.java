package ru.issergeev.parking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class CarEditActivity extends AppCompatActivity {
    private SQLWorker sqlWorker;

    private EditText name, licencePlate;
    private Spinner country;
    private Button save, delete;

    private int[] flags;
    private String[] countries;
    private int position = 0;

    private ArrayList<Cars> carsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_edit_layout);

        sqlWorker = new SQLWorker(this);

        carsList = new ArrayList<>(MainPage.list);

        name = findViewById(R.id.carName);
        licencePlate = findViewById(R.id.licencePlate);
        country = findViewById(R.id.spinner);
        save = findViewById(R.id.editCar);
        delete = findViewById(R.id.deleteCar);

        final Intent intent = getIntent();

        for (int i = 0; i < carsList.size(); i++)
            if (carsList.get(i).getLicence_plate().equals(intent.getStringExtra("LicencePlate")))
                position = i;

        carsList.remove(position);

        countries = getResources().getStringArray(R.array.countries);
        flags = new int[]{
                R.drawable.ru,
                R.drawable.by,
                R.drawable.ua,
                R.drawable.kz,
                R.drawable.us
        };
        final CountriesAdapter countriesAdapter = new CountriesAdapter(CarEditActivity.this, countries, flags, R.color.background);
        country.setAdapter(countriesAdapter);

        name.setText(intent.getStringExtra("Name"));
        licencePlate.setText(intent.getStringExtra("LicencePlate"));
        String[] counties = getResources().getStringArray(R.array.countries);
        int position = new ArrayList<>(Arrays.asList(counties)).indexOf(intent.getStringExtra("Country"));
        country.setSelection(position);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setEnabled(false);

                new Writer().execute(intent.getStringExtra("LicencePlate"), name.getText().toString(), licencePlate.getText().toString(), getResources().getStringArray(R.array.countries)[country.getSelectedItemPosition()], getResources().getString(R.string.updateCar));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete.setEnabled(false);

                new Writer().execute(intent.getStringExtra("LicencePlate"), name.getText().toString(), licencePlate.getText().toString(), getResources().getStringArray(R.array.countries)[country.getSelectedItemPosition()], getResources().getString(R.string.deleteCar));
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class Writer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            sqlWorker.open();

            if (strings[4].equals(getResources().getString(R.string.updateCar))) {
                boolean correct = true;

                for (Cars cars : carsList)
                    if (cars.getLicence_plate().equals(strings[1])) {
                        correct = false;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CarEditActivity.this, R.string.existed, Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    }

                if (correct) {
                    sqlWorker.updateCar(strings[0], strings[1], strings[2], strings[3]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CarEditActivity.this, R.string.updated, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                sqlWorker.deleteCar(strings[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CarEditActivity.this, R.string.deleted, Toast.LENGTH_LONG).show();
                    }
                });
            }
            sqlWorker.close();

            MainPage.readDB();

            finish();

            return null;
        }
    }
}