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
 * Created by Mez on 28/03/18.
 */

public class Adapter_Excuse extends ArrayAdapter {


    private Context context;
    private ArrayList<excuse> excuses;


    public Adapter_Excuse(Context context, ArrayList<excuse> excuses) {
        super(context, R.layout.coustom_list_for_excuses, excuses);
        this.context = context;
        this.excuses = excuses;

    }

    public excuse getItem(int position) {
        return excuses.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coustom_list_for_excuses, parent, false);

        TextView from = view.findViewById(R.id.textViewFrom_excuse);
        from.setText(excuses.get(position).getStudent_name());
        TextView course = view.findViewById(R.id.textViewForCourseOf_listExcuse);
        course.setText(excuses.get(position).getCourse_name());
        TextView date = view.findViewById(R.id.textViewForDate_of_lecture);
        date.setText(excuses.get(position).getDate());
        TextView state = view.findViewById(R.id.textViewForStateOfExcuse);
        state.setText(excuses.get(position).getState());

        return view;
    }


}
