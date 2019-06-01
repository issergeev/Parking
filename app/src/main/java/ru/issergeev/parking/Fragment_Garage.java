package ru.issergeev.parking;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;

public class Fragment_Garage extends Fragment implements AdapterView.OnItemClickListener {
    private static RecyclerView recyclerView;
    private Button add;

    private CarListAdapter adapter;

    private ArrayList<Cars> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.garage_fragment, container, false);

        list = MainPage.list;

        recyclerView = view.findViewById(R.id.carsList);
        add = view.findViewById(R.id.add);

        adapter = new CarListAdapter(view.getContext(), list, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCarsActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CarListAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = list.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Cars deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeCar(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(getView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cars car = list.get(i);
        startActivity(new Intent(getActivity(), CarEditActivity.class)
                .putExtra("Name", car.getName())
                .putExtra("LicencePlate", car.getLicence_plate())
                .putExtra("Country", car.getCountry()));
    }
}