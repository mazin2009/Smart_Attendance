package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class listOfAttenanceInfo_forEachLecture extends AppCompatActivity implements Designable {


    private TextView CourseName, Date, State, St1, St2, St3, St4, St5;
    private ArrayList<String> list_attendance_info_ofLect;
    private ArrayList<String> ListOfPresentStudents;
    private ListView listview_attendance_info_ofLec;
    private Button getRandom;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_attenance_info_for_each_lecture);

        InitializeView();
    }

    @Override
    public void InitializeView() {

        this.progressDialog = new ProgressDialog(listOfAttenanceInfo_forEachLecture.this);
        this.CourseName = findViewById(R.id.textViewForCourseNAmeINLectureInfo);
        this.CourseName.setText(getIntent().getStringExtra("Course_name"));
        this.Date = findViewById(R.id.textViewForDateOfLecture);
        this.Date.setText(getIntent().getStringExtra("DATE"));
        this.State = findViewById(R.id.textViewForStateOflecture);
        this.State.setText(getIntent().getStringExtra("state"));
        this.getRandom = findViewById(R.id.button2GetRandom);

        this.list_attendance_info_ofLect = new ArrayList<>();
        // list for put the random PresentStudents
        this.ListOfPresentStudents = new ArrayList<>();

        this.listview_attendance_info_ofLec = findViewById(R.id.listAttendacInfo_forLecture);


        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        // call server to get all attendance information for this lecture.
        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetAttendInfoForEachLec, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String StudentInfo;

                        String ID = jsonObject.getString("ID");
                        String name = jsonObject.getString("name");
                        String State = jsonObject.getString("State");
                        StudentInfo = ID + "," + name + "," + State + "," + Date.getText().toString() + "," + getIntent().getStringExtra("Course_ID");
                        list_attendance_info_ofLect.add(StudentInfo);

                    }


                    if (list_attendance_info_ofLect.size() == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "There is no students", Toast.LENGTH_LONG).show();

                    } else {
                        progressDialog.dismiss();
                        Attend_Info_eache_Lec_Adpt adapter = new Attend_Info_eache_Lec_Adpt(getBaseContext(), list_attendance_info_ofLect);
                        listview_attendance_info_ofLec.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                // HTTP request parameters
                map.put("ID", getIntent().getStringExtra("Course_ID"));
                map.put("Date", getIntent().getStringExtra("DATE"));
                return map;
            }
        };

        // Add The volly request to the Singleton Queue.
        Singleton_Queue.getInstance(getBaseContext()).Add(request);
        // End of Volly http request


        Design();

    }

    @Override
    public void Design() {
        setTitle("Lecture Attendance Information ");

        HandleAction();
    }

    @Override
    public void HandleAction() {


        getRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                //call server to get 5 random of student.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.getRandomST, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() < 5) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Must be the number of student more than 5", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.dismiss();
                                // clear array list
                                ListOfPresentStudents.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ST = jsonArray.getJSONObject(i);
                                    ListOfPresentStudents.add(ST.getString("name"));
                                }

                                Collections.shuffle(ListOfPresentStudents); // shuffle all students.

                                AlertDialog.Builder dialog = new AlertDialog.Builder(listOfAttenanceInfo_forEachLecture.this);
                                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_random_student_dailog, null, false);

                                // get the first 5 students in array list after shuffle
                                St1 = v.findViewById(R.id.Student1);
                                St1.setText(ListOfPresentStudents.get(0));
                                St2 = v.findViewById(R.id.Student2);
                                St2.setText(ListOfPresentStudents.get(1));
                                St3 = v.findViewById(R.id.Student3);
                                St3.setText(ListOfPresentStudents.get(2));
                                St4 = v.findViewById(R.id.Student4);
                                St4.setText(ListOfPresentStudents.get(3));
                                St5 = v.findViewById(R.id.Student5);
                                St5.setText(ListOfPresentStudents.get(4));
                                dialog.setView(v);
                                dialog.setCancelable(true);
                                dialog.show();

                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        // HTTP request parameters
                        map.put("Cr_ID", getIntent().getStringExtra("Course_ID"));
                        map.put("Date", getIntent().getStringExtra("DATE"));
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request

            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }

}

