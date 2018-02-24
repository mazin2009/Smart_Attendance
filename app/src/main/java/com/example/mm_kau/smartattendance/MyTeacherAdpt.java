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
 * Created by Mez on 19/02/18.
 */

public class MyTeacherAdpt extends ArrayAdapter {

    private Context context;
    private ArrayList<teacher> teachers;


    public MyTeacherAdpt(Context context, ArrayList<teacher> teachers) {
        super(context, R.layout.costum_list, R.id.TextView_INcostum, teachers);
        this.context = context;
        this.teachers = teachers;

    }

    public teacher getItem(int position){
        return teachers.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.costum_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.TextView_INcostum);
        textView.setText("Teacher Name : "+teachers.get(position).getFname() +"\nTeacher ID : "+teachers.get(position).getId());

        return view;
    }



}
