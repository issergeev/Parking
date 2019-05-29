package ru.issergeev.parking;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class Fragment_Payment extends Fragment {
//    private final String number = "7757";
    private final String appData = "ParkingData";
    private final String parkingStation = "ParkingStation";
    private final String carSelected = "SelectedCar";
    private final String timeSelected = "SelectedTime";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ArrayList<Cars> list;

    private RelativeLayout carSpinner;
    private Spinner hoursSpinner, licencePlateSpinner;
    private Button pay;
    private MaskedEditText parkingID;

    private AlertDialog.Builder alertDialog;

    private boolean isEmpty = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        list = MainPage.list;
        final CarRowAdapter adapter = new CarRowAdapter(view.getContext(), list);
        if (adapter.isEmpty())
            isEmpty = true;

        licencePlateSpinner = view.findViewById(R.id.spinner1);
        licencePlateSpinner.setAdapter(adapter);
        hoursSpinner = view.findViewById(R.id.spinner2);
        parkingID = view.findViewById(R.id.parkingID);

        parkingID.setText(sharedPreferences.getString(parkingStation, ""));
        licencePlateSpinner.setSelection(sharedPreferences.getInt(carSelected, 0));
        hoursSpinner.setSelection(sharedPreferences.getInt(timeSelected, 0));

        carSpinner = view.findViewById(R.id.carSpinner);

        carSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alertDialog = new AlertDialog.Builder(getActivity(),
                                android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alertDialog = new AlertDialog.Builder(getActivity());
                    }

                    alertDialog.setTitle(R.string.no_cars)
                            .setMessage(R.string.no_cars_garage)
                            .setPositiveButton(R.string.go_to_garage, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.main, new Fragment_Garage())
                                            .commit();
                                    MainPage.getNavigationView().getMenu().getItem(1).setChecked(true);
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

                            cancelButton.setTextColor(getResources()
                                    .getColor(android.R.color.holo_green_light));
                        }
                    });
                    dialog.show();
                }
            }
        });

        pay = view.findViewById(R.id.payButton);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Character.isDigit(parkingID.getText().charAt(3))) {
                    if (!isEmpty) {
                        String payString = adapter.getLicencePlate(licencePlateSpinner.getSelectedItemPosition()) + "-" + hoursSpinner.getSelectedItem().toString().charAt(0);
                        //Log.d("string", payString);
                        //SmsManager.getDefault().sendTextMessage(number, null,payString, null, null);
                        startActivity(new Intent(view.getContext(), ParkingActivity.class).putExtra("Data", payString));
                    } else {
                        Toast.makeText(view.getContext(), getResources().getString(R.string.no_cars_add), Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(view.getContext(), getResources().getString(R.string.no_parking_number), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editor.putString(parkingStation, parkingID.getText().toString());
        editor.putInt(carSelected, licencePlateSpinner.getSelectedItemPosition());
        editor.putInt(timeSelected, hoursSpinner.getSelectedItemPosition());
        editor.commit();
    }
}