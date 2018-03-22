package com.example.mm_kau.smartattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class listOfAttenanceInfo_forEachLecture extends AppCompatActivity implements Designable {


    private TextView CourseName , Date , State;
    private ArrayList<String> list_attendance_info_ofLect;
    ListView listview_attendance_info_ofLec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_attenance_info_for_each_lecture);
        InitializeView();
    }

    @Override
    public void InitializeView() {


        CourseName = findViewById(R.id.textViewForCourseNAmeINLectureInfo);
        this.CourseName.setText(getIntent().getStringExtra("Course_name"));
        Date = findViewById(R.id.textViewForDateOfLecture);
        this.Date.setText(getIntent().getStringExtra("DATE"));
        State = findViewById(R.id.textViewForStateOflecture);
        this.State.setText(getIntent().getStringExtra("state"));

        list_attendance_info_ofLect = new ArrayList<>();
        listview_attendance_info_ofLec = findViewById(R.id.listAttendacInfo_forLecture);


        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetAttendInfoForEachLec, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String StudentInfo ;

                        String ID = jsonObject.getString("ID");
                        String name = jsonObject.getString("name");
                        String State = jsonObject.getString("State");
                        StudentInfo = ID+","+name+","+State+","+Date.getText().toString()+","+getIntent().getStringExtra("Course_ID");
                        list_attendance_info_ofLect.add(StudentInfo);
                    }



                    if (list_attendance_info_ofLect.size() == 0) {

                        Toast.makeText(getBaseContext(), "There is no students", Toast.LENGTH_LONG).show();
                    } else {

                        Attend_Info_eache_Lec_Adpt adapter = new Attend_Info_eache_Lec_Adpt(getBaseContext(), list_attendance_info_ofLect);
                        listview_attendance_info_ofLec.setAdapter(adapter);


                    }
                } catch (JSONException e) {

                    Toast.makeText(getBaseContext(), "There is no students", Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ID",getIntent().getStringExtra("Course_ID"));
                map.put("Date",getIntent().getStringExtra("DATE"));
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);





    }

    @Override
    public void Desing() {

    }

    @Override
    public void HandleAction() {

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }



}

