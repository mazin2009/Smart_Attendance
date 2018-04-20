package com.example.mm_kau.smartattendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mez on 24/02/18.
 */

public class Adapter_ClassRoom extends ArrayAdapter {


    private Context context;
    private ArrayList<classroom> CRS;


    public Adapter_ClassRoom(Context context, ArrayList<classroom> CRS) {
        super(context, R.layout.costum_list, CRS);
        this.context = context;
        this.CRS = CRS;
    }

    public classroom getItem(int position) {
        return CRS.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.costum_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.TextView_INcostum);
        textView.setText("Class room ID : " + CRS.get(position).getID() + "\nClass Room Name : " + CRS.get(position).getName());
        return view;
    }


}
