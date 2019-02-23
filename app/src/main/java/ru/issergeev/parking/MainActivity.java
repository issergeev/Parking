package ru.issergeev.parking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ViewFlipper myFlipper;
    private Button button, next, nextHidden, datePicker;
    private EditText name;
    private Spinner spinner;
    private Animation setInAnimation;
    private Animation setOutAnimation;
    private AlertDialog.Builder alertDialog;

    private String[] countries;
    private int[] flags;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String appData = "ParkingData";
    private static final String firstStart = "FirstStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in);
        setOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out);

        myFlipper = findViewById(R.id.myFlipper);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.first, R.layout.second, R.layout.third};
        for (int layout : layouts)
            myFlipper.addView(inflater.inflate(layout, null));

        button = findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInAnimation.setInterpolator(null);
                setOutAnimation.setInterpolator(null);
                myFlipper.setOutAnimation(setOutAnimation);
                myFlipper.setInAnimation(setInAnimation);
                myFlipper.showNext();
            }
        });

        next = findViewById(R.id.next);
        nextHidden = findViewById(R.id.nextHidden);
        nextHidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] date = datePicker.getText().toString().split("-");

                if (!next.isEnabled())
                    Toast.makeText(MainActivity.this, R.string.fill, Toast.LENGTH_SHORT).show();
                else if (dateComparator(Long.valueOf(date[2] + date[1] + date[0]), Long.valueOf(Calendar.getInstance().get(Calendar.YEAR)
                                                                                    + "" + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                                                                                    + "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))) {
                    setInAnimation.setInterpolator(null);
                    setOutAnimation.setInterpolator(null);
                    myFlipper.setOutAnimation(setOutAnimation);
                    myFlipper.setInAnimation(setInAnimation);
                    myFlipper.showNext();
                    preferences = getSharedPreferences(appData, MODE_PRIVATE);
                    editor = preferences.edit();

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
                                    }
                                })
                                .setCancelable(true)
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        editor.putBoolean(firstStart, false);
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
            }
        });

        datePicker = findViewById(R.id.datePicker);
        datePicker.setText(new StringBuilder().append(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .append("-")
                .append(Calendar.getInstance().get(Calendar.MONTH) + 1)
                .append("-")
                .append(Calendar.getInstance().get(Calendar.YEAR)));
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(datePicker.getText().toString());
            }
        });

        name = findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (name.getText().toString().trim().length() > 1) {
                    next.setEnabled(true);
                    next.setTextColor(getResources().getColor(R.color.textColor));
                } else{
                    next.setEnabled(false);
                    next.setTextColor(getResources().getColor(R.color.inactiveTextColor));
                }
            }
        });

        spinner = findViewById(R.id.spinner);
        countries = getResources().getStringArray(R.array.countries);
        flags = new int[]{
                R.drawable.ru,
                R.drawable.by,
                R.drawable.ua,
                R.drawable.kz
        };
        CountriesAdapter countriesAdapter = new CountriesAdapter(MainActivity.this, countries, flags);
        spinner.setAdapter(countriesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, countries[i], Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private boolean dateComparator(long Date1, long Date2) {
        if (Date2 - Date1 > 0)
            return true;
        else
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

    //DatePicker class
    private void showDatePickerDialog(final String currentDate) {
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener dateSetListener;

        if (Character.isDigit(currentDate.charAt(0))) {
            String[] split = currentDate.split("-");
            int day = Integer.valueOf(split[0]);
            final int month = Integer.valueOf(split[1]) - 1;
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
}