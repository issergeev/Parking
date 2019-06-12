package ru.issergeev.parking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SQLWorker sqlWorker;

    private List<Cars> carsList, rawList;
    private CarsAdapter carsAdapter;

    private AllEventsListener allEventsListener;

    private RecyclerView recyclerView;
    private ViewFlipper myFlipper;
    private Button button, next, nextHidden, finish, datePicker;
    private FloatingActionButton actionButton;
    private EditText name;
    private Animation setInAnimation;
    private Animation setOutAnimation;
    private AlertDialog.Builder alertDialog;

    private String[] rawDate;
    private int[] selectedDate;
    private int[] currentDate;

    private boolean correctDate;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String appData = "ParkingData";
    private final String first = "First";
    private final String userName = " UserName";
    private final String userAge = " UserAge";
    private final String page = "Page";

    private boolean correct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(appData, MODE_PRIVATE);
        editor = preferences.edit();

        sqlWorker = new SQLWorker(this);

        allEventsListener = new AllEventsListener();

        setInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in);
        setOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out);

        myFlipper = findViewById(R.id.myFlipper);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{R.layout.first, R.layout.second, R.layout.third};
        for (int layout : layouts) {
            assert inflater != null;
            myFlipper.addView(inflater.inflate(layout, null));
        }

        carsList = new ArrayList<>();
        carsAdapter = new CarsAdapter(this, carsList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(carsAdapter);
        carsList.add(new Cars(null, null, getResources().getStringArray(R.array.countries)[0]));
        carsAdapter.notifyItemInserted(carsList.size());

        button = findViewById(R.id.start);
        button.setOnClickListener(allEventsListener);

        next = findViewById(R.id.next);
        nextHidden = findViewById(R.id.nextHidden);
        nextHidden.setOnClickListener(allEventsListener);

        datePicker = findViewById(R.id.datePicker);
        datePicker.setOnClickListener(allEventsListener);
        datePicker.addTextChangedListener(allEventsListener);

        name = findViewById(R.id.name);
        name.addTextChangedListener(allEventsListener);
        //END

        myFlipper.setDisplayedChild(preferences.getInt(page, 0));
        name.setText(preferences.getString(userName, ""));
        datePicker.setText(preferences.getString(userAge, getResources().getString(R.string.age_pickness)));

        actionButton = findViewById(R.id.actionButton);
        actionButton.setOnClickListener(allEventsListener);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    actionButton.hide();
                else
                    actionButton.show();

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (Character.isDigit(datePicker.getText().toString().charAt(0))) {
            rawDate = datePicker.getText().toString().split("-");
            selectedDate = new int[]{Integer.valueOf(rawDate[2]), Integer.valueOf(rawDate[1]), Integer.valueOf(rawDate[0])};
            currentDate = new int[]{Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};

            correctDate = dateComparator(currentDate, selectedDate);
        }

        finish = findViewById(R.id.finish);
        finish.setOnClickListener(allEventsListener);
    }

    private boolean dateComparator(int[] current, int[] selected) {
        correctDate = false;

        if (current[0] - selected[0] > 0) {
            return true;
        } else if (current[0] - selected[0] == 0) {
            if (current[1] - selected[1] > 0) {
                return true;
            } else if (current[1] - selected[1] == 0) {
                if (current[2] - selected[2] > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        switch (myFlipper.getCurrentView().getId()) {
            case R.id.first:
                super.onBackPressed();
                break;
            default:
                setInAnimation.setInterpolator(new ReverseInterpolator());
                setOutAnimation.setInterpolator(new ReverseInterpolator());
                myFlipper.setInAnimation(setOutAnimation);
                myFlipper.setOutAnimation(setInAnimation);
                myFlipper.showPrevious();
        }
    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    @Override
    protected void onDestroy() {
        editor.putString(userName, name.getText().toString());
        editor.putString(userAge, datePicker.getText().toString());
        editor.putInt(page, myFlipper.getDisplayedChild());
        editor.apply();
        super.onDestroy();
    }

    //DatePicker class
    private void showDatePickerDialog(final String currentDate) {
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener dateSetListener;

        if (Character.isDigit(currentDate.charAt(0))) {
            String[] split = currentDate.split("-");
            int day = Integer.valueOf(split[0]);
            int month = Integer.valueOf(split[1]) - 1;
            int year = Integer.valueOf(split[2]);

            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    datePicker.setText(new StringBuilder().append(dayOfMonth)
                            .append("-")
                            .append(monthOfYear + 1)
                            .append("-")
                            .append(year)
                            .toString());
                }
            };

            datePickerDialog = new DatePickerDialog(this,
                    dateSetListener, year, month, day);
        } else {
            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    datePicker.setText(new StringBuilder().append(dayOfMonth)
                            .append("-")
                            .append(monthOfYear + 1)
                            .append("-")
                            .append(year)
                            .toString());
                }
            };
            datePickerDialog = new DatePickerDialog(this, dateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
        datePickerDialog.show();
    }

    private class AllEventsListener implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

        //TextWatcher
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            //Split to different TextWatcher
            if (Character.isDigit(datePicker.getText().toString().charAt(0))) {
                rawDate = datePicker.getText().toString().split("-");
                selectedDate = new int[]{Integer.valueOf(rawDate[2]), Integer.valueOf(rawDate[1]), Integer.valueOf(rawDate[0])};
                currentDate = new int[]{Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};

                correctDate = dateComparator(currentDate, selectedDate);

                if (correctDate)
                    datePicker.setTextColor(getResources().getColor(R.color.textColor));
                else
                    datePicker.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }

            //Split to different TextWatcher
            if (name.getText().toString().trim().length() > 1 && correctDate) {
                next.setEnabled(true);
                next.setTextColor(getResources().getColor(R.color.textColor));
            } else {
                next.setEnabled(false);
                next.setTextColor(getResources().getColor(R.color.inactiveTextColor));
            }

            if (name.getText().toString().trim().length() > 1)
                name.setTextColor(getResources().getColor(R.color.textColor));
            else
                name.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }

        //OnClick
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start:
                    setInAnimation.setInterpolator(null);
                    setOutAnimation.setInterpolator(null);
                    myFlipper.setOutAnimation(setOutAnimation);
                    myFlipper.setInAnimation(setInAnimation);
                    myFlipper.showNext();
                    break;
                case R.id.nextHidden:
                    if (!next.isEnabled() && name.getText().toString().trim().length() == 0)
                        Toast.makeText(MainActivity.this, R.string.fill, Toast.LENGTH_SHORT).show();
                    else if (correctDate && name.getText().toString().trim().length() >= 2) {
                        setInAnimation.setInterpolator(null);
                        setOutAnimation.setInterpolator(null);
                        myFlipper.setOutAnimation(setOutAnimation);
                        myFlipper.setInAnimation(setInAnimation);
                        myFlipper.showNext();

                        editor.putString(userName, name.getText().toString());
                        editor.putString(userAge, datePicker.getText().toString());
                        editor.apply();

                        if (preferences.getBoolean(first, true)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                alertDialog = new AlertDialog.Builder(MainActivity.this,
                                        android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                alertDialog = new AlertDialog.Builder(MainActivity.this);
                            }
                            alertDialog.setTitle(R.string.no_template)
                                    .setMessage(R.string.no_template_message)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            editor.putBoolean(first, false);
                                            editor.apply();
                                        }
                                    })
                                    .setCancelable(true)
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            editor.putBoolean(first, true);
                                            editor.apply();
                                        }
                                    })
                                    .setIcon(R.drawable.conversation);

                            final AlertDialog dialog = alertDialog.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                    cancelButton.setTextColor(getResources()
                                            .getColor(android.R.color.holo_green_light));
                                }
                            });
                            dialog.show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.check, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.datePicker:
                    showDatePickerDialog(datePicker.getText().toString());
                    break;
                case R.id.actionButton:
                    carsList.add(new Cars("", "", getResources().getStringArray(R.array.countries)[0]));
                    carsAdapter.notifyItemInserted(carsAdapter.getItemCount());
                    break;
                case R.id.finish:
                    finish.setEnabled(false);
                    new Writer().execute();
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }

    @SuppressLint("StaticFieldLeak")
    private class Writer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            rawList = new ArrayList<>();

            sqlWorker.open();

            if (carsList.size() == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alertDialog = new AlertDialog.Builder(MainActivity.this);
                }

                alertDialog.setTitle(R.string.no_cars)
                        .setMessage(R.string.no_cars_message)
                        .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, MainPage.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(true)
                        .setIcon(R.drawable.conversation);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog dialog = alertDialog.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button nextButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                nextButton.setTextColor(getResources()
                                        .getColor(android.R.color.holo_red_light));
                                cancelButton.setTextColor(getResources()
                                        .getColor(android.R.color.holo_green_light));
                            }
                        });
                        dialog.show();
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
                                    Toast.makeText(MainActivity.this, R.string.no_licence_plate, Toast.LENGTH_SHORT).show();
                                }
                            });
                            rawList.clear();
                            break;
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.no_car_name, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MainActivity.this, R.string.duplicate_fields, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }

                if (correct && rawList.size() == carsList.size()) {
                    for (Cars cars : rawList) {
                        sqlWorker.insertCar(cars.getLicence_plate(), cars.getName(), cars.getCountry());
                    }

                    sqlWorker.close();

                    startActivity(new Intent(MainActivity.this, MainPage.class));
                    finish();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish.setEnabled(true);
                }
            });
            return null;
        }
    }
}