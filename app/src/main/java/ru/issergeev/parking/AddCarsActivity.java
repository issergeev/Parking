package ru.issergeev.parking;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddCarsActivity extends AppCompatActivity {
    private SQLWorker sqlWorker;

    private FloatingActionButton actionButton;
    private RecyclerView recyclerView;
    private Button add;

    private List<Cars> carsList, rawList;

    private CarsAdapter carsAdapter;

    private boolean correct = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);

        carsList = new ArrayList<>();

        carsAdapter = new CarsAdapter(this, carsList);
        carsList.add(new Cars(null, null, getResources().getStringArray(R.array.countries)[0]));
        carsAdapter.notifyItemInserted(carsList.size());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(carsAdapter);

        actionButton = findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carsList.size() < 10) {
                    carsList.add(new Cars(null, null, getResources().getStringArray(R.array.countries)[0]));
                    carsAdapter.notifyItemInserted(carsAdapter.getItemCount());
                } else {
                    Toast.makeText(AddCarsActivity.this, R.string.more_than_ten, Toast.LENGTH_LONG).show();
                }
            }
        });

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setEnabled(false);
                new Writer().execute();
            }
        });

        sqlWorker = new SQLWorker(this);
    }

    @SuppressLint("StaticFieldLeak")
    private class Writer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            rawList = new ArrayList<>();

            sqlWorker.open();

            if (carsList.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddCarsActivity.this, R.string.no_cars_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                for (Cars cars : carsList) {
                    if (cars.getName() != null && cars.getName().length() != 0) {
                        if (cars.getLicence_plate() != null && cars.getLicence_plate().length() != 0) {
                            rawList.add(new Cars(cars.getName(), cars.getLicence_plate(), cars.getCountry()));
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddCarsActivity.this, R.string.no_licence_plate, Toast.LENGTH_SHORT).show();
                                }
                            });
                            rawList.clear();
                            break;
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddCarsActivity.this, R.string.no_car_name, Toast.LENGTH_SHORT).show();
                            }
                        });
                        rawList.clear();
                        break;
                    }
                }

                correct = true;
                for (int i = 0; i < rawList.size() - 1; i++) {
                    for (int j = i + 1; j < rawList.size(); j++) {
                        if (rawList.get(i).getName().equals(rawList.get(j).getName())
                                || rawList.get(i).getLicence_plate().equals(rawList.get(j).getLicence_plate())) {
                            correct = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddCarsActivity.this, R.string.duplicate_fields, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }

                if (correct && rawList.size() == carsList.size()) {
                    int i = 0;
                    for (final Cars cars : rawList) {
                        i = (int) sqlWorker.insertCar(cars.getLicence_plate(), cars.getName(), cars.getCountry());
                        if (i < 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddCarsActivity.this, getString(R.string.vehicle) + " " + getString(R.string.with_licence_plate) + " " + cars.getLicence_plate() + " " + getString(R.string.is_already_exist), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    sqlWorker.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            add.setEnabled(true);
                        }
                    });

                    MainPage.readDB();

                    finish();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    add.setEnabled(true);
                }
            });
            return null;
        }
    }
}