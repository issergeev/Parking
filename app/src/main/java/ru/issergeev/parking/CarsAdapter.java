package ru.issergeev.parking;

import android.content.Context;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;


public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {
    private final String RUS = "A 777 MP 777";
    private final String BEL = "1234 AA-7";
    private final String UKR = "AA 1234 AA";
    private final String KAZ = "123 ABC 01";

    private String[] countries;
    private int[] flags;

    private LayoutInflater inflater;
    private List<Cars> cars;

    private CarsAdapter adapter;

    CarsAdapter(Context context, List<Cars> cars) {
        this.cars = cars;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CarsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_layout, parent, false);
        adapter = this;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CarsAdapter.ViewHolder holder, final int position) {
        final Cars car = cars.get(holder.getAdapterPosition());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                car.setName(holder.name.getText().toString().trim());
                car.setLicence_plate(holder.licencePlate.getText().toString().trim());
            }
        };

        holder.name.addTextChangedListener(textWatcher);
        holder.licencePlate.addTextChangedListener(textWatcher);

        holder.getName().setText("");
        holder.getLicencePlate().setText("");
        holder.getCountry().setSelection(0);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        boolean isHint = true;
        int position = 0;
        String hint = RUS;

        final EditText name;
        final EditText licencePlate;

        public EditText getName() {
            return name;
        }

        public EditText getLicencePlate() {
            return licencePlate;
        }

        public Spinner getCountry() {
            return country;
        }

        final Spinner country;
        final Button button;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.carName);
            licencePlate = view.findViewById(R.id.licencePlate);
            country = view.findViewById(R.id.spinner);
            button = view.findViewById(R.id.deleteCar);

            countries = view.getResources().getStringArray(R.array.countries);
            flags = new int[]{
                    R.drawable.ru,
                    R.drawable.by,
                    R.drawable.ua,
                    R.drawable.kz,
                    R.drawable.us
            };
            final CountriesAdapter countriesAdapter = new CountriesAdapter(view.getContext(), countries, flags, android.R.color.transparent);
            country.setAdapter(countriesAdapter);
            country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 0 :
                            hint = RUS;
                            break;
                        case 1 :
                            hint = BEL;
                            break;
                        case 2 :
                            hint = UKR;
                            break;
                        case 3 :
                            hint = KAZ;
                            break;
                        case 4 :
                            hint = view.getResources().getString(R.string.licence_plate);
                    }
                    cars.get(getAdapterPosition()).setCountry(view.getResources().getStringArray(R.array.countries)[country.getSelectedItemPosition()]);

                    licencePlate.setHint(hint);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            licencePlate.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    if (isHint) {
                        Snackbar snackbar = Snackbar.make(view, view.getResources().getString(R.string.no_hint), BaseTransientBottomBar.LENGTH_LONG)
                                .setAction(view.getResources().getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        licencePlate.setText("");

                                        try {
                                            position = country.getSelectedItemPosition();

                                            switch (position) {
                                                case 0 :
                                                    hint = RUS;
                                                    break;
                                                case 1 :
                                                    hint = BEL;
                                                    break;
                                                case 2 :
                                                    hint = UKR;
                                                    break;
                                                case 3 :
                                                    hint = KAZ;
                                                    break;
                                                case 4 :
                                                    hint = view.getResources().getString(R.string.licence_plate);
                                            }

                                            licencePlate.setHint(hint);
                                            isHint = true;
                                        } catch (NullPointerException e){}
                                    }
                                });
                        snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
                        snackbar.show();

                        licencePlate.setHint(view.getResources().getString(R.string.licence_plate));
                        isHint = false;
                    } else {
                        Snackbar snackbar = Snackbar.make(view, view.getResources().getString(R.string.is_hint), BaseTransientBottomBar.LENGTH_LONG);
                        snackbar.show();
                        position = country.getSelectedItemPosition();

                        switch (position) {
                            case 0 :
                                hint = RUS;
                                break;
                            case 1 :
                                hint = BEL;
                                break;
                            case 2 :
                                hint = UKR;
                                break;
                            case 3 :
                                hint = KAZ;
                                break;
                            case 4 :
                                hint = view.getResources().getString(R.string.licence_plate);
                        }

                        licencePlate.setHint(hint);
                        isHint = true;
                    }

                    return true;
                }
            });

            button.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
//            getName().setText("");
//            getLicencePlate().setText("");
//            getCountry().setSelection(0);
            adapter.cars.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());
        }
    }
}