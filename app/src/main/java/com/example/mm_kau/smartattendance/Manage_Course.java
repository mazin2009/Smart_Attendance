package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Manage_Course extends AppCompatActivity implements Designable  {

    private EditText  CourseName_AD, TeacherID_AD, ClassRommID_AD;
    private TextView CourseID_AD;
    private TimePicker STL , ETL , STA ,ETA;
    private Button UpdateBTN , DeleteBTN ;
    private ProgressDialog progressDialog;
    private String TeacherID , ClassroomID , STL_ad , ETL_ad , STA_ad ,ETA_ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__course);
        InitializeView();

    }

    @Override
    public void InitializeView() {

        this.progressDialog = new ProgressDialog(Manage_Course.this);

        this.UpdateBTN = findViewById(R.id.UpdateCourseBTN);
        this.DeleteBTN = findViewById(R.id.DeleteCourseBTN);

        this.CourseID_AD = findViewById(R.id.editTextForViewCourseID);
        this.CourseID_AD.setText(getIntent().getStringExtra("course_ID"));

        this.CourseName_AD = findViewById(R.id.editTextForViewCourseName);
        this.CourseName_AD.setText(  getIntent().getStringExtra("name"));

        this.TeacherID_AD = findViewById(R.id.editTextForTeacherIdOfCourse_mng);

        // check if the course has teacher or not yet.
        if (getIntent().getStringExtra("TeacherID").equals("null")) {
            TeacherID_AD.setHint("undefined");
        }else {
            this.TeacherID_AD.setText(  getIntent().getStringExtra("TeacherID"));
        }

        // check if the course has  classroom or not yet.
        this.ClassRommID_AD = findViewById(R.id.editTextForCLassRoomOfCourse_mng);
        if (getIntent().getStringExtra("Room_ID").equals("null")) {
            this.ClassRommID_AD.setHint("undefined"); }else {
            this.ClassRommID_AD.setText(  getIntent().getStringExtra("Room_ID"));
                }




        this.STL = findViewById(R.id.TimePicker_STL);
        this.ETL = findViewById(R.id.TimePicker_ETL);
        this.STA = findViewById(R.id.TimePicker_STA);
        this.ETA = findViewById(R.id.TimePicker_ETA);
        // Make the time picker as 24 hour view
        STL.setIs24HourView(true);
        ETL.setIs24HourView(true);
        STA.setIs24HourView(true);
        ETA.setIs24HourView(true);

        // check version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // get the time from intent and put it in time picker
            STL.setHour(Integer.parseInt(getIntent().getStringExtra("STL").split(":")[0]));
            STL.setMinute(Integer.parseInt(getIntent().getStringExtra("STL").split(":")[1]));

            ETL.setHour(Integer.parseInt(getIntent().getStringExtra("ETL").split(":")[0]));
            ETL.setMinute(Integer.parseInt(getIntent().getStringExtra("ETL").split(":")[1]));


            STA.setHour(Integer.parseInt(getIntent().getStringExtra("STA").split(":")[0]));
            STA.setMinute(Integer.parseInt(getIntent().getStringExtra("STA").split(":")[1]));


            ETA.setHour(Integer.parseInt(getIntent().getStringExtra("ETA").split(":")[0]));
            ETA.setMinute(Integer.parseInt(getIntent().getStringExtra("ETA").split(":")[1]));

        }


        Design();

    }

    @Override
    public void Design() {

        HandleAction();
    }

    @Override
    public void HandleAction() {


        // delete this course.
        DeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                //  call server to delete this course.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.DeleteCourseByID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("state");
                            if (status.equals("yes")) {

                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), " Course Deleted.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                startActivity(intent);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

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

// HTTP request parameters

                        HashMap<String, String> map = new HashMap<>();
                        map.put("course_id", CourseID_AD.getText().toString());

                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request


            }
        });



        UpdateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    if (CourseName_AD.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else if (Network.isConnected(getBaseContext()) == false) {
                        Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                    } else {

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        // if admin did not set the teacher we will make it null.
                        if (TeacherID_AD.getText().length() == 0) {
                            TeacherID = "NULL";

                        } else {
                            TeacherID = TeacherID_AD.getText().toString();
                        }
                        // if admin did not set the classroom we will make it null.
                        if (ClassRommID_AD.getText().length() == 0) {
                            ClassroomID = "NULL";
                        } else {
                            ClassroomID = ClassRommID_AD.getText().toString();
                        }



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // get the time from time picker to update it.
                            // make the format hh:mm:ss a
                            STL_ad = String.valueOf(STL.getHour())+":"+String.valueOf(STL.getMinute())+":00";
                            ETL_ad = String.valueOf(ETL.getHour())+":"+String.valueOf(ETL.getMinute())+":00";
                            STA_ad = String.valueOf(STA.getHour())+":"+String.valueOf(STA.getMinute())+":00";
                            ETA_ad = String.valueOf(ETA.getHour())+":"+String.valueOf(ETA.getMinute())+":00";
                        }


                        StringRequest request = new StringRequest(Request.Method.POST, Constants.updateCourse, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");

                                    if (status.equals("yes")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), " Course Updated.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), adminHome.class);
                                        startActivity(intent);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

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

                                // HTTP request parameters

                                HashMap<String, String> map = new HashMap<>();
                                map.put("course_id", CourseID_AD.getText().toString());
                                map.put("course_name", CourseName_AD.getText().toString());
                                map.put("teacherID", TeacherID);
                                map.put("classroomID", ClassroomID);
                                map.put("STL", STL_ad);
                                map.put("ETL", ETL_ad);
                                map.put("STA", STA_ad);
                                map.put("ETA", ETA_ad);
                                return map;
                            }
                        };

                        // Add The volly request to the Singleton Queue.
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);
                        // End of Volly http request

                    }


                } catch (Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();


                }


            }
        });


    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);

    }


}
