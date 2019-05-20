package ru.issergeev.parking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CarRowAdapter extends ArrayAdapter<String> {
    ArrayList<Cars> cars;
    Context mContext;

    public CarRowAdapter(@NonNull Context context, ArrayList<Cars> cars) {
        super(context, R.layout.car_row);
        this.cars = cars;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return cars.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.car_row, parent, false);
            mViewHolder.name = convertView.findViewById(R.id.carName);
            mViewHolder.licencePlate = convertView.findViewById(R.id.licencePlate);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.name.setText(cars.get(position).getName());
        mViewHolder.licencePlate.setText(cars.get(position).getLicence_plate());

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView licencePlate;
    }

    public String getLicencePlate(int position) {
        return cars.get(position).getLicence_plate();
    }
}