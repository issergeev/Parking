package ru.issergeev.parking;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private final String appData = "ParkingData";
    private final String smsControl = "isSMSAuto";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Switch sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = preferences.edit();

        sms = findViewById(R.id.sms);
        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    editor.putBoolean(smsControl, true);
                    editor.apply();
                } else {
                    editor.putBoolean(smsControl, false);
                    editor.apply();
                }
            }
        });
        sms.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sms.setChecked(preferences.getBoolean(smsControl, true));
    }
}
