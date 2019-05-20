package ru.issergeev.parking;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class Fragment_Payment extends Fragment {
    private final String number = "7757";

    private ArrayList<Cars> list;
    private Spinner hoursSpinner, licencePlateSpinner;
    private Button pay;
    private MaskedEditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        list = MainPage.list;
        final CarRowAdapter adapter = new CarRowAdapter(view.getContext(), list);

        licencePlateSpinner = view.findViewById(R.id.spinner1);
        hoursSpinner = view.findViewById(R.id.spinner2);
        licencePlateSpinner.setAdapter(adapter);
        editText = view.findViewById(R.id.parkingID);

        pay = view.findViewById(R.id.payButton);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() == 4) {
                    String payString = adapter.getLicencePlate(licencePlateSpinner.getSelectedItemPosition()) + "-" + hoursSpinner.getSelectedItem().toString().charAt(0);
                    //Log.d("string", payString);
                    //SmsManager.getDefault().sendTextMessage(number, null,payString, null, null);
                    startActivity(new Intent(view.getContext(), ParkingActivity.class).putExtra("Data", payString));
                } else
                    Toast.makeText(view.getContext(), getResources().getString(R.string.no_parking_number), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}