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

public class CountriesAdapter extends ArrayAdapter<String> {
    String[] spinnerNames;
    int[] spinnerFlags;
    Context mContext;
    int colorId;

    public CountriesAdapter(@NonNull Context context, String[] names, int[] flags, int colorId) {
        super(context, R.layout.countries_spinner_row);
        this.spinnerNames = names;
        this.spinnerFlags = flags;
        this.mContext = context;
        this.colorId = colorId;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerNames.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.countries_spinner_row, parent, false);
            mViewHolder.flag = convertView.findViewById(R.id.flag);
            mViewHolder.name = convertView.findViewById(R.id.name);
            convertView.setTag(mViewHolder);
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.popupBackground));
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.flag.setImageResource(spinnerFlags[position]);
        mViewHolder.name.setText(spinnerNames[position]);
        convertView.setBackgroundColor(convertView.getResources().getColor(colorId));

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        ImageView flag;
    }
}