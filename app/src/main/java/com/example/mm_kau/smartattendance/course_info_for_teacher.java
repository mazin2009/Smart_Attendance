package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class course_info_for_teacher extends AppCompatActivity implements Designable {

    TextView C_id , C_name , C_CR , STL , ETL , STA, ETA , No_ST;
    Button change_AT , View_AttendInfo , viewLec , SendAnnouncment , update_TimeOfAttendance ;
    private SharedPreferences sharedPreferences;
    private TimePicker STL_p , ETL_p , STA_p ,ETA_p;
    String STL_ad , ETL_ad , STA_ad ,ETA_ad;
    private ProgressDialog progressDialog;
private ListView listview_attendance_info , listview_of_lecture;
    private ArrayList<String> list_attendance_info;
    private ArrayList<lecture> list_lecture;

    EditText Title , Body;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info_for_teacher);
        InitializeView();

    }

    @Override
    public void InitializeView() {
        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_info_for_teacher.this);
        C_id = findViewById(R.id.textViewForCRS_ID);
        C_name = findViewById(R.id.textViewForCRS_Name);
        C_CR = findViewById(R.id.textViewForCRS_ClassRoom);
        STL = findViewById(R.id.textViewForCRS_STL);
        ETL = findViewById(R.id.textViewForCRS_ETL);
        STA = findViewById(R.id.textViewForCRS_STA);
        ETA = findViewById(R.id.textViewForCRS_ETA);
        No_ST = findViewById(R.id.textViewForCRS_No_student);
        setNumberOfStudent();
        C_id.setText(getIntent().getStringExtra("course_ID"));
        C_name.setText( getIntent().getStringExtra("name"));
        if (getIntent().getStringExtra("Room_ID").equals("null")) {
            C_CR.setText("undefined");
        }else {

            C_CR.setText(getIntent().getStringExtra("Room_ID"));
        }

         STL.setText(getIntent().getStringExtra("STL"));
        ETL.setText(getIntent().getStringExtra("ETL"));
        STA.setText(getIntent().getStringExtra("STA"));
        ETA.setText(getIntent().getStringExtra("ETA"));



        change_AT = findViewById(R.id.button_UP_Ch_Attend);
        View_AttendInfo = findViewById(R.id.button2ViewAttendInfo_InTeacher);
        viewLec = findViewById(R.id.button3ForViweingLecture);
        SendAnnouncment = findViewById(R.id.button4ForSendAnnouncment);


        Desing();
    }





    @Override
    public void Desing() {

        HandleAction();
    }

    @Override
    public void HandleAction() {


        SendAnnouncment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.send_new_announcement, null, false);
                setContentView(v);

                Button Send = v.findViewById(R.id.buttonSendMessage);
                Title = v.findViewById(R.id.editTextTitleOfmessage);
                Body = v.findViewById(R.id.editText3BodyOfmessage);

                Send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (Title.getText().toString().trim().isEmpty() || Body.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                        } else if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();

                        } else {


                            StringRequest request = new StringRequest(Request.Method.POST, Constants.AddNewAnnouncment, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {


                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("state");

                                        if (status.equals("yes")) {

                                            progressDialog.dismiss();

                                            Toast.makeText(getBaseContext(), "The Message Sent", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                            startActivity(intent);

                                        } else {
                                            progressDialog.dismiss();

                                            Toast.makeText(getBaseContext(), "  There is problem m try agine ", Toast.LENGTH_LONG).show();

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    /*** Here you put the HTTP request parameters **/

                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("TeacherID",sharedPreferences.getString(Constants.TeacherID,""));
                                    map.put("CourseID",C_id.getText().toString());
                                    map.put("Title",Title.getText().toString());
                                    map.put("Body", Body.getText().toString());

                                    return map;
                                }
                            };
                            Singleton_Queue.getInstance(getBaseContext()).Add(request);




                        }


                    }
                });


            }
        });




        viewLec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.lecture_list_for_course_in_teacher, null, false);
                setContentView(v);

                list_lecture = new ArrayList<>();
                listview_of_lecture = v.findViewById(R.id.listTheLectureOfCourseInTeacher);

                StringRequest  request = new StringRequest(Request.Method.POST, Constants.Get_Lecture_for_course, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                lecture LEC = new lecture();


                                LEC.setDate(jsonObject.getString("date"));
                                LEC.setState(jsonObject.getString("state"));
                                LEC.setCourseID(jsonObject.getString("CourseID"));
                                list_lecture.add(LEC);
                            }



                            if (list_lecture.size() == 0) {
                                Toast.makeText(getBaseContext(), "The is no Lecture", Toast.LENGTH_LONG).show();
                            } else {







                                LectureList_Adpt adapter = new LectureList_Adpt(getBaseContext(), list_lecture);
                                listview_of_lecture.setAdapter(adapter);

                                listview_of_lecture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                       Intent intent=new Intent(getBaseContext(),listOfAttenanceInfo_forEachLecture.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("Course_name",C_name.getText().toString());
                                        intent.putExtra("Course_ID",list_lecture.get(i).getCourseID());
                                        intent.putExtra("DATE",list_lecture.get(i).getDate());
                                        intent.putExtra("state",list_lecture.get(i).getState());
                                        startActivity(intent);

                                    }
                                });



                            }
                        } catch (JSONException e) {

                            Toast.makeText(getBaseContext(), "The is no Lecture", Toast.LENGTH_LONG).show();

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
                        map.put("ID",C_id.getText().toString());
                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);




            }
        });

        View_AttendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.attendance_info_for_teacher, null, false);
                setContentView(v);

                list_attendance_info = new ArrayList<>();
                listview_attendance_info = v.findViewById(R.id.listAttendance_INFO);

                StringRequest  request = new StringRequest(Request.Method.POST, Constants.Get_attend_INFO, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String StudentInfo ;
                                String ID = jsonObject.getString("ID");
                                String name = jsonObject.getString("name");
                                String total_absent = jsonObject.getString("total_absent");
                                StudentInfo = ID+","+name+","+total_absent;
                                list_attendance_info.add(StudentInfo);
                            }



                            if (list_attendance_info.size() == 0) {

                                Toast.makeText(getBaseContext(), "There is no students", Toast.LENGTH_LONG).show();
                            } else {

                                attendance_Info_adpt adapter = new attendance_Info_adpt(getBaseContext(), list_attendance_info);
                                listview_attendance_info.setAdapter(adapter);


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
                        map.put("ID",C_id.getText().toString());
                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);


            }
        });


        change_AT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.change_attendace_time_teacher, null, false);


                STL_p = v.findViewById(R.id.TimePicker_STL_inTeacher);
                ETL_p = v.findViewById(R.id.TimePicker_ETL_inTeacher);
                STA_p = v.findViewById(R.id.TimePicker_STA_inTeacher);
                ETA_p = v.findViewById(R.id.TimePicker_ETA_inTeacher);
                STL_p.setIs24HourView(true);
                ETL_p.setIs24HourView(true);
                STA_p.setIs24HourView(true);
                ETA_p.setIs24HourView(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    STL_p.setHour(Integer.parseInt(getIntent().getStringExtra("STL").split(":")[0]));
                    STL_p.setMinute(Integer.parseInt(getIntent().getStringExtra("STL").split(":")[1]));

                    ETL_p.setHour(Integer.parseInt(getIntent().getStringExtra("ETL").split(":")[0]));
                    ETL_p.setMinute(Integer.parseInt(getIntent().getStringExtra("ETL").split(":")[1]));


                    STA_p.setHour(Integer.parseInt(getIntent().getStringExtra("STA").split(":")[0]));
                    STA_p.setMinute(Integer.parseInt(getIntent().getStringExtra("STA").split(":")[1]));


                    ETA_p.setHour(Integer.parseInt(getIntent().getStringExtra("ETA").split(":")[0]));
                    ETA_p.setMinute(Integer.parseInt(getIntent().getStringExtra("ETA").split(":")[1]));

                }

                setContentView(v);

                update_TimeOfAttendance = v.findViewById(R.id.buttonUpdateTimeofAttend_inTeacher);
                update_TimeOfAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            STL_ad = String.valueOf(STL_p.getHour())+":"+String.valueOf(STL_p.getMinute())+":00";
                            ETL_ad = String.valueOf(ETL_p.getHour())+":"+String.valueOf(ETL_p.getMinute())+":00";
                            STA_ad = String.valueOf(STA_p.getHour())+":"+String.valueOf(STA_p.getMinute())+":00";
                            ETA_ad = String.valueOf(ETA_p.getHour())+":"+String.valueOf(ETA_p.getMinute())+":00";

                        }




                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();




                        StringRequest request = new StringRequest(Request.Method.POST, Constants.Update_TimeOdAttendance, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");
                                    if (status.equals("yes")) {

                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), " Time Updated.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                        startActivity(intent);

                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(getBaseContext(), " Tehre is problem , try agine ", Toast.LENGTH_LONG).show();

                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    e.printStackTrace();

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
                                /*** Here you put the HTTP request parameters **/

                                HashMap<String, String> map = new HashMap<>();
                                map.put("id", C_id.getText().toString());
                                map.put("STL", STL_ad);
                                map.put("ETL", ETL_ad);
                                map.put("STA", STA_ad);
                                map.put("ETA", ETA_ad);
                                return map;
                            }
                        };
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);



                    }
                });



            }
        });


    }



    public void setNumberOfStudent() {


        StringRequest request = new StringRequest(Request.Method.POST, Constants.Get_numberOfST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");

                    if (status.equals("yes")) {

                        String Nu_ST = jsonObject.getString("Nu_ST");
                        No_ST.setText(Nu_ST);


                    } else {

                        No_ST.setText(" ");

                    }
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    e.printStackTrace();

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
                /*** Here you put the HTTP request parameters **/

                HashMap<String, String> map = new HashMap<>();
                map.put("id", C_id.getText().toString());
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);




    }



    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }


}
