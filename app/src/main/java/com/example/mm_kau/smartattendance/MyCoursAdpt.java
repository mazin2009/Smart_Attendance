package com.example.mm_kau.smartattendance;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Mez on 11/02/18.
 */

public class MyCoursAdpt extends ArrayAdapter {


    private Context context;
    private ArrayList<course> courses;


    public MyCoursAdpt(Context context, ArrayList<course> productses) {
       super(context, R.layout.course_costum_list, R.id.CourseID_InLayout, productses);
        this.context = context;
        this.courses = productses;

    }

    public course getItem(int position){
        return courses.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.course_costum_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.CourseID_InLayout);
        textView.setText("Course Name : "+courses.get(position).getCourse_Name() +"\nCourse ID : "+courses.get(position).getCourse_id());






return view;
    }


}
