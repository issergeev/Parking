package ru.issergeev.parking;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Choice_Fragment extends Fragment implements View.OnClickListener {
    private final String appData = "ParkingData";
    private final String smsControl = "isSMSAuto";

    private StringBuilder message, SMS;

    private String parkingID, licencePlate, hours;
    private Button pay, add, stop;

    private SharedPreferences preferences;

    private AlertDialog.Builder alertDialog;

    private Choice_Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coosing_fragment, container, false);

        preferences = getActivity().getSharedPreferences(appData, Context.MODE_PRIVATE);

        fragment = this;

        parkingID = getArguments().getString("parkingID");
        licencePlate = getArguments().getString("licencePlate");
        hours = getArguments().getString("hours");

        pay = view.findViewById(R.id.pay);
        add = view.findViewById(R.id.add);
        stop = view.findViewById(R.id.stop);

        pay.setOnClickListener(this);

        add.setOnClickListener(this);

        stop.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        RelativeLayout rootLayout = getActivity().findViewById(R.id.rootLayout);
        rootLayout.setClickable(true);

        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pay:
                SMS = new StringBuilder(parkingID + "*" + licencePlate + "*" + hours);
                break;
            case R.id.add:
                SMS = new StringBuilder("Х" + hours);

                break;
            case R.id.stop :
                SMS = new StringBuilder("С");
        }

        message = new StringBuilder(" \"").append(SMS.toString()).append("\" ").append(getString(R.string.message_end));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(getActivity());
        }

        StringBuilder newMessage = new StringBuilder(getResources().getString(R.string.send_message));
        newMessage.append(message);

        alertDialog.setTitle(R.string.send)
                .setMessage(newMessage.toString())
                .setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (preferences.getBoolean(smsControl, true)) {
                            SmsManager.getDefault().sendTextMessage(getResources().getString(R.string.MOS_PARKING), null, SMS.toString(), null, null);
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.sent, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + getResources().getString(R.string.MOS_PARKING)));
                            intent.putExtra("sms_body", SMS.toString());
                            startActivity(intent);
                            Toast.makeText(getActivity(), R.string.sms_redirecting, Toast.LENGTH_SHORT).show();
                        }

                        getFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .setIcon(R.drawable.conversation);

        final AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                cancelButton.setTextColor(getResources()
                        .getColor(android.R.color.holo_red_light));
                okButton.setTextColor(getResources()
                        .getColor(android.R.color.holo_green_light));
            }
        });
        dialog.show();
    }
}