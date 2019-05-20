package ru.issergeev.parking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

public class ParkingActivity extends AppCompatActivity {
    private Intent intent;

    private TextView textView;
    private Button stop;

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        intent = getIntent();
        data = intent.getStringExtra("Data").split("-");

        textView = findViewById(R.id.title);
        textView.append(data[0]);

        stop = findViewById(R.id.stop);


        final Chronometer chronometer = findViewById(R.id.timer);
        long startTime = SystemClock.elapsedRealtime();
        chronometer.setBase(startTime + Integer.valueOf(data[1]) * 3600000);
        chronometer.setCountDown(true);
        chronometer.start();

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (chronometer.getBase() == SystemClock.elapsedRealtime())
                    chronometer.stop();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.stop();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(ParkingActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(ParkingActivity.this);
        }
        alertDialog.setTitle("Завершить парковку?")
                .setMessage("Вы хотите досрочно завершить сессию?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, null)
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
}