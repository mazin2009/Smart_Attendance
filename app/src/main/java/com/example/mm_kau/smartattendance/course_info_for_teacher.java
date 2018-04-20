package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class course_info_for_teacher extends AppCompatActivity implements Designable {

    private TextView C_id, C_name, C_CR, STL, ETL, STA, ETA, No_ST;
    private Button change_AT, View_AttendInfo, viewLec, SendAnnouncment, update_TimeOfAttendance;
    private SharedPreferences userfile;
    private TimePicker STL_p, ETL_p, STA_p, ETA_p;
    private String STL_ad, ETL_ad, STA_ad, ETA_ad;
    private ProgressDialog progressDialog;
    private ListView listview_attendance_info, listview_of_lecture;
    private ArrayList<String> list_attendance_info;
    private ArrayList<lecture> list_lecture;
    private EditText Title, Body;
    private android.app.AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info_for_teacher);
        InitializeView();

    }

    @Override
    public void InitializeView() {

        userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_info_for_teacher.this);
        alertDialog = new android.app.AlertDialog.Builder(course_info_for_teacher.this).create();
        C_id = findViewById(R.id.textViewForCRS_ID);
        C_name = findViewById(R.id.textViewForCRS_Name);
        C_id.setText(getIntent().getStringExtra("course_ID"));
        C_name.setText(getIntent().getStringExtra("name"));

        C_CR = findViewById(R.id.textViewForCRS_ClassRoom);
        STL = findViewById(R.id.textViewForCRS_STL);
        ETL = findViewById(R.id.textViewForCRS_ETL);
        STA = findViewById(R.id.textViewForCRS_STA);
        ETA = findViewById(R.id.textViewForCRS_ETA);
        No_ST = findViewById(R.id.textViewForCRS_No_student);

        setNumberOfStudent();
// check if the course has classroom or not.
        if (getIntent().getStringExtra("Room_ID").equals("null")) {
            C_CR.setText("undefined");
        } else {
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

        Design();
    }


    @Override
    public void Design() {


        setTitle("Course Infornation : " + C_name.getText().toString());
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

                            progressDialog.setMessage("Please wait ...");
                            progressDialog.show();
                            // call server to send new Announcment
                            StringRequest request = new StringRequest(Request.Method.POST, Constants.AddNewAnnouncment, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("state");

                                        if (status.equals("yes")) {
                                            progressDialog.dismiss();
                                            // Send notification
                                            SendAnnouncment(Body.getText().toString(), C_id.getText().toString(), Title.getText().toString());
                                            Toast.makeText(getBaseContext(), "The Message Sent", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                            startActivity(intent);

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "  There is problem m try agine ", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "  There is problem m try agine ", Toast.LENGTH_LONG).show();
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

                                    map.put("TeacherID", userfile.getString(Constants.TeacherID, ""));
                                    map.put("CourseID", C_id.getText().toString());
                                    map.put("Title", Title.getText().toString());
                                    map.put("Body", Body.getText().toString());

                                    return map;
                                }
                            };
                            // Add The volly request to the Singleton Queue.
                            Singleton_Queue.getInstance(getBaseContext()).Add(request);
                            // End of Volly http request

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


                setTitle("Lecture List");

                list_lecture = new ArrayList<>();
                listview_of_lecture = v.findViewById(R.id.listTheLectureOfCourseInTeacher);

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                StringRequest request = new StringRequest(Request.Method.POST, Constants.Get_Lecture_for_course, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // get all lecture of this course and put it in the array list.
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                lecture LEC = new lecture();
                                LEC.setDate(jsonObject.getString("date"));
                                LEC.setState(jsonObject.getString("state"));
                                LEC.setCourseID(jsonObject.getString("CourseID"));
                                list_lecture.add(LEC);
                            }


                            if (list_lecture.size() == 0) {
                                progressDialog.dismiss();
                                alertDialog.setMessage("There is no any lecture for this course.");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getBaseContext(), adminHome.class);
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.show();
                            } else {

                                //  Collections.reverse(list_lecture); // reverse the Lectures to put the near one first.

                                progressDialog.dismiss();
                                Adapter_LectureList adapter = new Adapter_LectureList(getBaseContext(), list_lecture);
                                listview_of_lecture.setAdapter(adapter);

                                // on click listener for items in the list view
                                listview_of_lecture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Intent intent = new Intent(getBaseContext(), listOfAttenanceInfo_forEachLecture.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("Course_name", C_name.getText().toString());
                                        intent.putExtra("Course_ID", list_lecture.get(i).getCourseID());
                                        intent.putExtra("DATE", list_lecture.get(i).getDate());
                                        intent.putExtra("state", list_lecture.get(i).getState());
                                        startActivity(intent);

                                    }
                                });


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
                        map.put("ID", C_id.getText().toString());
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request

            }
        });


        View_AttendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.attendance_info_for_teacher, null, false);
                setContentView(v);

                // set title of action bar.
                setTitle(" Attendance Information");

                list_attendance_info = new ArrayList<>();
                listview_attendance_info = v.findViewById(R.id.listAttendance_INFO);


                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                // cal server to get attendance information of this course.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.Get_attend_INFO, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String StudentInfo;
                                String ID = jsonObject.getString("ID");
                                String name = jsonObject.getString("name");
                                String total_absent = jsonObject.getString("total_absent");
                                // put student attendance info in String and speate it by ,
                                StudentInfo = ID + "," + name + "," + total_absent;
                                list_attendance_info.add(StudentInfo);
                            }

                            if (list_attendance_info.size() == 0) {

                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is no students", Toast.LENGTH_LONG).show();

                            } else {
                                progressDialog.dismiss();
                                Adapter_AllAttendanceInformation_InCourse adapter = new Adapter_AllAttendanceInformation_InCourse(getBaseContext(), list_attendance_info);
                                listview_attendance_info.setAdapter(adapter);

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
                        map.put("ID", C_id.getText().toString());
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request


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
                // set title of action bar.
                setTitle("Change Attendance Time");

                update_TimeOfAttendance = v.findViewById(R.id.buttonUpdateTimeofAttend_inTeacher);

                update_TimeOfAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // get the new time
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            STL_ad = String.valueOf(STL_p.getHour()) + ":" + String.valueOf(STL_p.getMinute()) + ":00";
                            ETL_ad = String.valueOf(ETL_p.getHour()) + ":" + String.valueOf(ETL_p.getMinute()) + ":00";
                            STA_ad = String.valueOf(STA_p.getHour()) + ":" + String.valueOf(STA_p.getMinute()) + ":00";
                            ETA_ad = String.valueOf(ETA_p.getHour()) + ":" + String.valueOf(ETA_p.getMinute()) + ":00";
                        }

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        // call server to update the time of attendance in this course.
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
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), " Tehre is problem , try agine ", Toast.LENGTH_LONG).show();
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
                                map.put("id", C_id.getText().toString());
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
                });


            }
        });


    }


    /**
     *
     */
    public void setNumberOfStudent() {

        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.Get_numberOfST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");

                    if (status.equals("yes")) {
                        progressDialog.dismiss();
                        String Nu_ST = jsonObject.getString("Nu_ST");
                        No_ST.setText(Nu_ST);
                    } else {
                        progressDialog.dismiss();
                        No_ST.setText("undefined");

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

                // HTTP request parameters
                HashMap<String, String> map = new HashMap<>();
                map.put("id", C_id.getText().toString());
                return map;
            }
        };

        // Add The volly request to the Singleton Queue.
        Singleton_Queue.getInstance(getBaseContext()).Add(request);
        // End of Volly http request

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }

    public void SendAnnouncment(final String Msg, final String Topic, final String Title) {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body", Msg);
                    jsonData.put("title", "New Announcment : " + Title);
                    json.put("notification", jsonData);
                    json.put("to", "/topics/" + Topic);

                    RequestBody body = RequestBody.create(JSON, json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization", "key=AAAAjjSURVI:APA91bFYLZHZHRXlCr7bh1VHZf3ZDbu1d8ioyfIuzCR40hJks4ILEYLE1UaNqqAj7ECKbToUnEA1FL1ysGRTnD6v87g4_9iQ_81iAwhcKmAgz49G6pY8_87IkdISX899j_bQ_q6JnfCB")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(course_info_for_teacher.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();

    }
}
