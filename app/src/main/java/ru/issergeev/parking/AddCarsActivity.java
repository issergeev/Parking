package ru.issergeev.parking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
    private AlertDialog.Builder alertDialog;

    private List<Cars> list, carsList, rawList;

    private CarsAdapter carsAdapter;

    private boolean correct = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);

        list = new ArrayList<>();
        carsList = new ArrayList<>();
        rawList = new ArrayList<>();

        carsAdapter = new CarsAdapter(this, list);
        list.add(new Cars(null, null));
        carsAdapter.notifyItemInserted(list.size());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(carsAdapter);

        actionButton = findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new Cars(null, null));
                carsAdapter.notifyItemInserted(carsAdapter.getItemCount());
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

    private class Writer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            sqlWorker.open();

            if (list.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddCarsActivity.this, R.string.no_cars_text, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                for (Cars cars : list) {
                    if (cars.getName() != null && cars.getName().length() != 0) {
                        if (cars.getLicence_plate() != null && cars.getLicence_plate().length() != 0) {
                            rawList.add(new Cars(cars.getName(), cars.getLicence_plate()));
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

                if (correct && rawList.size() == list.size()) {
                    try {
                        for (Cars cars : rawList) {
                            sqlWorker.insertCar(cars.getName(), cars.getLicence_plate(), cars.getCountry());
                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddCarsActivity.this, R.string.duplicate_fields, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } finally {
                        sqlWorker.close();
                    }

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