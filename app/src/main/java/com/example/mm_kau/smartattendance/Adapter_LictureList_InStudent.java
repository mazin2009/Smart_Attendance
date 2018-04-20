package com.example.mm_kau.smartattendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mez on 26/03/18.
 */

public class Adapter_LictureList_InStudent extends ArrayAdapter {


    private Context context;
    private ArrayList<String> LECture;
    String[] Info;

    public Adapter_LictureList_InStudent(Context context, ArrayList<String> LECture) {

        super(context, R.layout.coustom_list_lecture_in_student, LECture);
        this.context = context;
        this.LECture = LECture;
    }

    public String getItem(int position) {
        return LECture.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coustom_list_lecture_in_student, parent, false);

        Info = LECture.get(position).split(",");


        TextView DATE = view.findViewById(R.id.TextView_DateOfLecture_inST);
        DATE.setText(Info[0]);

        TextView State = view.findViewById(R.id.TextView_StateOfLec_INST);
        State.setText(Info[1]);

        Button SendExcuse = view.findViewById(R.id.buttonForSendExcuse);

        // if student present make the button of send Excuse disable.
        if (Info[1].equals("present")) {
            SendExcuse.setEnabled(false);
            SendExcuse.setTextColor(Color.parseColor("#bdbdbd"));
        }


        SendExcuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), send_Excuse.class);
                intent.putExtra("Teacher_ID", Info[4]);
                intent.putExtra("studentID", Info[2]);
                intent.putExtra("Course_id", Info[3]);
                intent.putExtra("Date", Info[0]);
                intent.putExtra("st_name", Info[5]);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

            }
        });

        return view;
    }


}
