package ru.issergeev.parking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private final String appData = "ParkingData";
    private final String userNamePrefs = " UserName";
    private final String userLastNamePrefs = "UserLastName";
    private final String userAge = " UserAge";

    private String[] rawDate;
    private int[] selectedDate;
    private int[] currentDate;
    private boolean correctDate = true;

    private EditText userName, lastName;
    private Button agePicker, save;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferences = getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = preferences.edit();

        listener = new Listener();

        userName = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        agePicker = findViewById(R.id.datePicker);
        save = findViewById(R.id.save);

        userName.setText(preferences.getString(userNamePrefs, ""));
        lastName.setText(preferences.getString(userLastNamePrefs, ""));
        agePicker.setText(preferences.getString(userAge, getResources().getString(R.string.date)));

        agePicker.setOnClickListener(listener);
        save.setOnClickListener(listener);

        userName.addTextChangedListener(listener);
        agePicker.addTextChangedListener(listener);
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
                    agePicker.setText(new StringBuilder().append(dayOfMonth)
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
                    agePicker.setText(new StringBuilder().append(dayOfMonth)
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

    private class Listener implements View.OnClickListener, TextWatcher {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.save :
                    if (correctDate && userName.getText().toString().trim().length() > 1) {
                        save.setEnabled(false);
                        editor.putString(userNamePrefs, userName.getText().toString());
                        editor.putString(userLastNamePrefs, lastName.getText().toString());
                        editor.putString(userAge, agePicker.getText().toString());
                        editor.apply();
                        finish();
                    } else
                        Toast.makeText(ProfileActivity.this, R.string.check, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.datePicker :
                    showDatePickerDialog(agePicker.getText().toString());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            //Split to different TextWatcher
            if (Character.isDigit(agePicker.getText().toString().charAt(0))) {
                rawDate = agePicker.getText().toString().split("-");
                selectedDate = new int[]{Integer.valueOf(rawDate[2]), Integer.valueOf(rawDate[1]), Integer.valueOf(rawDate[0])};
                currentDate = new int[]{Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)};

                correctDate = dateComparator(currentDate, selectedDate);

                if (correctDate)
                    agePicker.setTextColor(getResources().getColor(R.color.textColor));
                else
                    agePicker.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }

            //Split to different TextWatcher
            if (userName.getText().toString().trim().length() > 1 && correctDate) {
                save.setEnabled(true);
                save.setTextColor(getResources().getColor(R.color.textColor));
            } else {
                save.setEnabled(false);
                save.setTextColor(getResources().getColor(R.color.inactiveTextColor));
            }

            if (userName.getText().toString().trim().length() > 1)
                userName.setTextColor(getResources().getColor(R.color.textColor));
            else
                userName.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }
}