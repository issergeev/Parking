package ru.issergeev.parking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {
    Cars car;

    private LayoutInflater inflater;
    private List<Cars> cars;

    CarListAdapter(Context context, List<Cars> cars) {
        this.cars = cars;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CarListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        car = cars.get(position);

        holder.name.setText(car.getName());
        holder.licencePlate.setText(car.getLicence_plate());
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void removeCar(int position) {
        cars.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Cars car, int position) {
        cars.add(position, car);
        notifyItemInserted(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout background, foreground;
        TextView name;
        TextView licencePlate;
        ImageView thumbnail;

        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.carName);
            licencePlate = view.findViewById(R.id.licencePlate);

            background = view.findViewById(R.id.view_background);
            foreground = view.findViewById(R.id.foreground);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}