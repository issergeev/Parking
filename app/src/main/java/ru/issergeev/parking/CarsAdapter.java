package ru.issergeev.parking;

import android.content.Context;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {
    private static final String RUS = "A 777 MP 97";
    private static final String BEL = "1234 AA-7";
    private static final String UKR = "AA 1234 AA";
    private static final String KAZ = "123 ABC 01";

    private String[] countries;
    private int[] flags;

    private LayoutInflater inflater;
    private List<Cars> cars;

    CarsAdapter(Context context, List<Cars> cars) {
        this.cars = cars;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CarsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CarsAdapter.ViewHolder holder, final int position) {
        final Cars car = cars.get(position);

        holder.button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 cars.remove(holder.getAdapterPosition());
                 notifyItemRemoved(holder.getAdapterPosition());
                 notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
            }
        });

        holder.isHint = true;
        holder.name.setText("");
        holder.licencePlate.setHint(RUS);
        holder.licencePlate.setText("");
        holder.licencePlate.setTextSize(35f);
        holder.country.setSelection(0);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                cars.get(holder.getAdapterPosition()).setName(holder.name.getText().toString().trim());
                cars.get(holder.getAdapterPosition()).setLicence_plate(holder.licencePlate.getText().toString().trim());
            }
        };

        holder.name.addTextChangedListener(textWatcher);
        holder.licencePlate.addTextChangedListener(textWatcher);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        boolean isHint = true;
        int position = 0;
        String hint = "";

        final TextView name;
        final TextView licencePlate;
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
                    R.drawable.kz
            };
            CountriesAdapter countriesAdapter = new CountriesAdapter(view.getContext(), countries, flags);
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
                        licencePlate.setHint(view.getResources().getString(R.string.licence_plate));
                        licencePlate.setTextSize(25f);
                        isHint = false;
                    } else {
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
                        }

                        licencePlate.setHint(hint);
                        licencePlate.setTextSize(35f);
                        isHint = true;
                    }

                    if (!isHint) {
                        Snackbar snackbar = Snackbar.make(view, view.getResources().getString(R.string.no_hint), BaseTransientBottomBar.LENGTH_LONG)
                                .setAction(view.getResources().getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            position = country.getSelectedItemPosition();

                                            switch (position) {
                                                case 0:
                                                    hint = RUS;
                                                    break;
                                                case 1:
                                                    hint = BEL;
                                                    break;
                                                case 2:
                                                    hint = UKR;
                                                    break;
                                                case 3:
                                                    hint = KAZ;
                                            }

                                            licencePlate.setHint(hint);
                                            licencePlate.setTextSize(35f);
                                            isHint = true;
                                        } catch (NullPointerException e){}
                                    }
                                });
                        snackbar.setActionTextColor(view.getResources().getColor(R.color.colorAccent));
                        snackbar.show();
                    }

                    return true;
                }
            });
        }
    }
}