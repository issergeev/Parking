package ru.issergeev.parking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class Fragment_Payment extends Fragment {
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    private final String number = "7757";

    private final String appData = "ParkingData";
    private final String parkingStation = "ParkingStation";
    private final String carSelected = "SelectedCar";
    private final String timeSelected = "SelectedTime";
    private final String firstRequest = "FirstRequest";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ArrayList<Cars> list;

    private RelativeLayout carSpinner;
    private Spinner hoursSpinner, licencePlateSpinner;
    private Button pay;
    private MaskedEditText parkingID;

    private CarRowAdapter adapter;

    private AlertDialog.Builder alertDialog;

    private boolean isEmpty = false, permission = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences(appData, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        list = MainPage.list;

        adapter = new CarRowAdapter(view.getContext(), list);
        if (adapter.isEmpty())
            isEmpty = true;

        licencePlateSpinner = view.findViewById(R.id.spinner1);
        licencePlateSpinner.setAdapter(adapter);
        hoursSpinner = view.findViewById(R.id.spinner2);
        parkingID = view.findViewById(R.id.parkingID);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.hours_spinner, getResources().getStringArray(R.array.hours));
        hoursSpinner.setAdapter(spinnerAdapter);

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
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View view) {
                if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == -1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alertDialog = new AlertDialog.Builder(getActivity(),
                                android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alertDialog = new AlertDialog.Builder(getActivity());
                    }
                    alertDialog.setTitle(R.string.permission)
                            .setMessage(R.string.need_permission_message)
                            .setPositiveButton(R.string.permission_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_alert);

                            final AlertDialog dialog = alertDialog.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                                         @Override
                                                         public void onShow(DialogInterface dialogInterface) {
                                                             Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                                             cancelButton.setTextColor(getResources()
                                                                     .getColor(android.R.color.holo_green_light));
                                                         }
                                                     }
                            );
                            dialog.show();
                } else {

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                            new PermissionRequester().execute();
                        }
                    } else {
                        // Permission has already been granted
                        if (Character.isDigit(parkingID.getText().charAt(3))) {
                            if (!isEmpty) {
                                String payString = parkingID.getRawText() + "*" + adapter.getLicencePlate(licencePlateSpinner.getSelectedItemPosition()) + "*" + hoursSpinner.getSelectedItem().toString().charAt(0);
                                Log.d("log", payString);
                                String data = adapter.getLicencePlate(licencePlateSpinner.getSelectedItemPosition()) + "-" + hoursSpinner.getSelectedItem().toString().charAt(0);
                                SmsManager.getDefault().sendTextMessage(number, null, payString, null, null);
                                getFragmentManager().beginTransaction().add(R.id.main, new Choice_Fragment()).addToBackStack("replace").commit();
//                                startActivity(new Intent(view.getContext(), ParkingActivity.class).putExtra("Data", data));
                            } else {
                                Toast.makeText(view.getContext(), getResources().getString(R.string.no_cars_add), Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(view.getContext(), getResources().getString(R.string.no_parking_number), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editor.putString(parkingStation, parkingID.getText().toString());
        editor.putInt(carSelected, licencePlateSpinner.getSelectedItemPosition());
        editor.putInt(timeSelected, hoursSpinner.getSelectedItemPosition());
        editor.commit();
    }

    @SuppressLint("StaticFieldLeak")
    private class PermissionRequester extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog = new AlertDialog.Builder(getActivity(),
                        android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alertDialog = new AlertDialog.Builder(getActivity());
            }
            alertDialog.setTitle(R.string.permission)
                    .setMessage(R.string.no_permission_message)
                    .setPositiveButton(R.string.permission_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean(firstRequest, false);
                            editor.apply();

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.SEND_SMS},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton(R.string.permission_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            permission = false;

                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
            });
            return null;
        }
    }

    public class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    @NonNull ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                           ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            assert inflater != null;
            View view = inflater.inflate(R.layout.hours_spinner, parent, false);
            TextView hour = (TextView) view.findViewById(R.id.hour);
            hour.setText(getResources().getStringArray(R.array.hours)[position]);
//
//            ImageView icon = view.findViewById(R.id.icon);
            return view;
        }
    }
}