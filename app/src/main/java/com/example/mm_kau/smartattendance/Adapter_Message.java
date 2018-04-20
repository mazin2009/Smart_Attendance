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
 * Created by Mez on 23/03/18.
 */

public class Adapter_Message extends ArrayAdapter {


    private Context context;
    private ArrayList<Message> MSG;


    public Adapter_Message(Context context, ArrayList<Message> MSG) {
        super(context, R.layout.costum_list, MSG);
        this.context = context;
        this.MSG = MSG;

    }

    public Message getItem(int position){
        return MSG.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coustm_list_msg, parent, false);

        TextView Title = view.findViewById(R.id.textViewTitleOfms_inMsgList);
        Title.setText(MSG.get(position).getTitle());
        TextView Teacher_name = view.findViewById(R.id.textViewForTeacher_inMsgList);
        Teacher_name.setText(MSG.get(position).getTeacheName());
        TextView course = view.findViewById(R.id.textViewForCourseOfMSg_inMsgList);
        course.setText(MSG.get(position).getCourseNAme());
        TextView Date = view.findViewById(R.id.textViewForDateInMsglist);
        Date.setText(MSG.get(position).getDate());

        return view;
    }



}
