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
 * Created by Mez on 19/03/18.
 */

public class attendance_Info_adpt  extends ArrayAdapter {



    private Context context;
    private ArrayList<String> students;


    public attendance_Info_adpt(Context context, ArrayList<String> students) {

        super(context, R.layout.coutum_list_for_attend_info,students);
        this.context = context;
        this.students = students;
    }

    public String getItem(int position){
        return students.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coutum_list_for_attend_info, parent, false);

       String Info [] = students.get(position).split(",");


        TextView ID = (TextView) view.findViewById(R.id.TextView_IDofSTU);
        ID.setText(Info[0]);

        TextView name = (TextView) view.findViewById(R.id.TextView_NameofSTU);
        name.setText(Info[1]);

        TextView Total_absent = (TextView) view.findViewById(R.id.TextView_Total_AbsentofSTU);
        Total_absent.setText(Info[2]);
        return view;
    }


}
