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

public class MystudentAdpt extends ArrayAdapter {


    private Context context;
    private ArrayList<student> students;


    public MystudentAdpt(Context context, ArrayList<student> students) {
        super(context, R.layout.costum_list, students);
        this.context = context;
        this.students = students;

    }

    public student getItem(int position){
        return students.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.costum_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.TextView_INcostum);
        textView.setText("student Name : "+students.get(position).getFname() +"\nStudent ID : "+students.get(position).getId());

        return view;
    }

}
