package ru.issergeev.parking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Cars> carsList;

    private CarsAdapter carsAdapter;

    private AllEventsListener allEventsListener;

    private RecyclerView recyclerView;
    private ViewFlipper myFlipper;
    private Button button, next, nextHidden, datePicker;
    private FloatingActionButton actionButton;
    private EditText name;
    private Spinner spinner;
    private Animation setInAnimation;
    private Animation setOutAnimation;
    private AlertDialog.Builder alertDialog;

//    private String[] countries;
//    private int[] flags;
    private boolean correctDate;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String appData = "ParkingData";
    private static final String firstStart = "FirstStart";
    private static final String userName = " UserName";
    private static final String userAge = " UserAge";
    private static final String page = "Page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        carsList.add(new Cars());

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

        spinner = findViewById(R.id.spinner);
//        countries = getResources().getStringArray(R.array.countries);
//        flags = new int[]{
//                R.drawable.ru,
//                R.drawable.by,
//                R.drawable.ua,
//                R.drawable.kz
//        };
//        CountriesAdapter countriesAdapter = new CountriesAdapter(MainActivity.this, countries, flags);
//        spinner.setAdapter(countriesAdapter);
//        spinner.setOnItemSelectedListener(allEventsListener);

        preferences = getSharedPreferences(appData, MODE_PRIVATE);
        editor = preferences.edit();

        //DELETE after test
        editor.putBoolean(firstStart, true);
        editor.apply();
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
            if (name.getText().toString().trim().length() > 1 && Character.isDigit(datePicker.getText().toString().charAt(0))) {
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

            //Split to different TextWatcher
            if (Character.isDigit(datePicker.getText().toString().charAt(0))) {
                String[] rawDate = datePicker.getText().toString().split("-");
                int[] selectedDate = new int[]{Integer.valueOf(rawDate[2]), Integer.valueOf(rawDate[1]), Integer.valueOf(rawDate[0])};
                int[] currentDate = new int[]{Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};

                correctDate = dateComparator(currentDate, selectedDate);

                if (correctDate)
                    datePicker.setTextColor(getResources().getColor(R.color.textColor));
                else
                    datePicker.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
        }

        //OnClick
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start :
                    setInAnimation.setInterpolator(null);
                    setOutAnimation.setInterpolator(null);
                    myFlipper.setOutAnimation(setOutAnimation);
                    myFlipper.setInAnimation(setInAnimation);
                    myFlipper.showNext();
                    break;
                case R.id.nextHidden :
                    if (!next.isEnabled())
                        Toast.makeText(MainActivity.this, R.string.fill, Toast.LENGTH_SHORT).show();
                    else if (correctDate) {
                        setInAnimation.setInterpolator(null);
                        setOutAnimation.setInterpolator(null);
                        myFlipper.setOutAnimation(setOutAnimation);
                        myFlipper.setInAnimation(setInAnimation);
                        myFlipper.showNext();

                        if (preferences.getBoolean(firstStart, true)) {
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
                                            editor.putBoolean(firstStart, false);
                                            editor.apply();
                                        }
                                    })
                                    .setCancelable(true)
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            editor.putBoolean(firstStart, true);
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
                case R.id.datePicker :
                    showDatePickerDialog(datePicker.getText().toString());
                    break;
                case R.id.actionButton :
                    carsList.add(new Cars());
                    carsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//            Toast.makeText(MainActivity.this, countries[i], Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}