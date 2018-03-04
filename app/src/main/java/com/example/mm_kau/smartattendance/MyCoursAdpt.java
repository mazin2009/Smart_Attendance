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
 * Created by Mez on 11/02/18.
 */

public class MyCoursAdpt extends ArrayAdapter {


    private Context context;
    private ArrayList<course> courses;


    public MyCoursAdpt(Context context, ArrayList<course> courses) {
       super(context, R.layout.costum_list, R.id.TextView_INcostum, courses);
        this.context = context;
        this.courses = courses;

    }

    public course getItem(int position){
        return courses.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.costum_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.TextView_INcostum);
        textView.setText("Course Name : "+courses.get(position).getCourse_Name() +"\nCourse ID : "+courses.get(position).getCourse_id());


return view;
    }


}
