package com.example.mm_kau.smartattendance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class course_Info_for_student extends AppCompatActivity implements Designable {

    private TextView C_id, C_name, Teacher_name, C_CR, STL, ETL, STA, ETA, No_absent;
    private Button ViewAttendanceInfo_BTN, MakeAttendance_BTN;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ListView listview_of_attendance_info;
    private ArrayList<String> list_attendance_info;
    private Boolean IsInsideTheClassroom = false;
    private AlertDialog alertDialog;
    // create proximityManager to manage the connection with Beacons.
    private ProximityManager proximityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__info_for_student);

        InitializeView();

    }

    @Override
    public void InitializeView() {

        alertDialog = new AlertDialog.Builder(course_Info_for_student.this).create();

        // configuration of proximityManager
        KontaktSDK.initialize("wAJUXvCsDfWLqhkwYIiixyaNqeyBikIo"); // Server API Key
        proximityManager = ProximityManagerFactory.create(this); // create new proximityManager
        proximityManager.setEddystoneListener(createEddystoneListener());  // set Listener to discover the beacons
        checkPermissions(); // check Permissions of bluetooth.
        startScanning();// start scan for beacon device


        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_Info_for_student.this);

        MakeAttendance_BTN = findViewById(R.id.buttonOfMakaAttendANCE);
        ViewAttendanceInfo_BTN = findViewById(R.id.button2OFLectureInfo_inST);

        C_id = findViewById(R.id.textViewForCRS_ID_st);
        C_id.setText(getIntent().getStringExtra("course_ID"));
        C_name = findViewById(R.id.textViewForCRS_Name_st);
        C_name.setText(getIntent().getStringExtra("name"));

        C_CR = findViewById(R.id.textViewForCRS_ClassRoom_st);
        Teacher_name = findViewById(R.id.textViewForTeacher_Name_st);
        STL = findViewById(R.id.textViewForCRS_STL_st);
        ETL = findViewById(R.id.textViewForCRS_ETL_st);
        STA = findViewById(R.id.textViewForCRS_STA_st);
        ETA = findViewById(R.id.textViewForCRS_ETA_st);
        No_absent = findViewById(R.id.textViewForCRS_No_absent_st);

        setNumberOfAbsent();

        // check if the course has classroom or not.
        if (getIntent().getStringExtra("Room_ID").equals("null")) {
            C_CR.setText("undefined");
        } else {
            C_CR.setText(getIntent().getStringExtra("Room_ID"));
        }
        // check if the course has Teacher or not.
        if (getIntent().getStringExtra("T_name").equals("null")) {
            Teacher_name.setText("undefined");
        } else {
            Teacher_name.setText(getIntent().getStringExtra("T_name"));
        }

        STL.setText(getIntent().getStringExtra("STL"));
        ETL.setText(getIntent().getStringExtra("ETL"));
        STA.setText(getIntent().getStringExtra("STA"));
        ETA.setText(getIntent().getStringExtra("ETA"));

        Design();
    }

    @Override
    public void Design() {
        // set title of bar action
        setTitle("Course Information : " + getIntent().getStringExtra("name"));
        HandleAction();
    }

    @Override
    public void HandleAction() {


        ViewAttendanceInfo_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_of_lecture_for_st, null, false);
                setContentView(v);
                // set title of action bar.
                setTitle("Attendance Information");
                list_attendance_info = new ArrayList<>();
                listview_of_attendance_info = v.findViewById(R.id.listTheLectureOfStu);

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                // call server to get all lecture of the course with attendance info.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetLecture_forStudent, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String AttendanceInfo;
                                String Date = jsonObject.getString("Date");
                                String State = jsonObject.getString("state");
                                AttendanceInfo = Date + "," + State + "," + sharedPreferences.getString(Constants.StudentID, " ") + "," + C_id.getText().toString() + "," + getIntent().getStringExtra("T_ID") + "," + sharedPreferences.getString(Constants.s_Fname, "") + " " + sharedPreferences.getString(Constants.s_Lname, "");
                                list_attendance_info.add(AttendanceInfo);
                            }


                            if (list_attendance_info.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is no Lecture", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.dismiss();
                                LictureAdpt_OfLecture_inST adapter = new LictureAdpt_OfLecture_inST(getBaseContext(), list_attendance_info);
                                listview_of_attendance_info.setAdapter(adapter);

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
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("CR_ID", C_id.getText().toString());
                        map.put("ST_ID", sharedPreferences.getString(Constants.StudentID, " "));
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request

            }
        });


        MakeAttendance_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the course has classroom or not yet.
                if (C_CR.getText().toString().equals("undefined")) {
                    Toast.makeText(getBaseContext(), " Sorry , you cannot make attendance because the classroom undefined", Toast.LENGTH_LONG).show();
                } else if (Student_HomePage.BeaconID.size() == 0) { // if the Beacon id array list is empty So the student not inside the classromm
                    Toast.makeText(getBaseContext(), "Please make sure you are inside the classroom number : " + C_CR.getText().toString() + " and Try agine", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog.setMessage("Please wait ...");
                    progressDialog.show();

                    // call server to get the list of beacons belong to this course.
                    StringRequest request = new StringRequest(Request.Method.POST, Constants.GetBeacons, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONArray jsonArray = new JSONArray(response);

                                // Comparison if any connection beacon id match with any beacon id belong to this course
                                for (int i = 0; i < Student_HomePage.BeaconID.size(); i++) {
                                    for (int k = 0; k < jsonArray.length(); k++) {
                                        String B = jsonArray.getString(k);
                                        if (Student_HomePage.BeaconID.get(i).equals(B)) {
                                            // if match make the IsInsideTheClassroom as true and break the loop.
                                            IsInsideTheClassroom = true;
                                            break;
                                        }
                                    }
                                }

                                // if the student inside the classroom.
                                if (IsInsideTheClassroom) {


                                    // Get The android_id (it's unique like mac-address )
                                    final String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);


                                    // call server to check the informations and make attendance if matched.
                                    StringRequest request = new StringRequest(Request.Method.POST, Constants.MakeAttendance, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try {

                                                JSONObject jsonObject = new JSONObject(response);
                                                String status = jsonObject.getString("state");

                                                if (status.equals("yes")) {

                                                    // if system make the student present but the student make attendance with new device.
                                                    // the application will send notification to teacher.
                                                    if (!jsonObject.isNull("mac_State")) { // if the state yes and the mac_State not null , means student attendance with new device
                                                        SendAnnouncmentWhenNewMac_occure(getIntent().getStringExtra("T_ID"), sharedPreferences.getString(Constants.s_Fname, " ") + " " + sharedPreferences.getString(Constants.s_Lname, ""));
                                                    }

                                                    progressDialog.dismiss();
                                                    alertDialog.setMessage("Your attendance process has been successfully registered");
                                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    alertDialog.show();

                                                } else if (status.equals("no")) {

                                                    // if system doesn't make student present and the student make attendance with device device belong to another student.
                                                    // the application will send notification to teacher.
                                                    if (!jsonObject.isNull("mac_State")) { // if the state no and the mac_State not null , means student attendance with  device belong to another student.
                                                        SendAnnouncmentWhenrejectStudents(getIntent().getStringExtra("T_ID"), sharedPreferences.getString(Constants.s_Fname, " ") + " " + sharedPreferences.getString(Constants.s_Lname, ""));
                                                    }

                                                    progressDialog.dismiss();
                                                    alertDialog.setMessage("Your attendance process has been Failed. \nReason : " + jsonObject.getString("reason"));
                                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    });
                                                    alertDialog.show();
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
                                            map.put("Course_id", C_id.getText().toString());
                                            map.put("ST_ID", sharedPreferences.getString(Constants.StudentID, " "));
                                            map.put("Mac", android_id);
                                            return map;
                                        }
                                    };
                                    // Add The volly request to the Singleton Queue.
                                    Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                    // End of Volly http request

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), "You Are not inside classroom " + C_CR.getText().toString(), Toast.LENGTH_SHORT).show();
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
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ID", C_CR.getText().toString());
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

    public void setNumberOfAbsent() {

        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetNumberOfabsent, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");

                    if (status.equals("yes")) {
                        progressDialog.dismiss();
                        String Nu_absent = jsonObject.getString("total_absent");
                        if (Nu_absent.equals("null")) {
                            No_absent.setText("0");
                        } else {

                            No_absent.setText(Nu_absent);
                        }


                    } else {
                        progressDialog.dismiss();
                        No_absent.setText("undefined");

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
                map.put("CR_ID", C_id.getText().toString());
                map.put("ID", sharedPreferences.getString(Constants.StudentID, " "));
                return map;
            }
        };


        // Add The volly request to the Singleton Queue.
        Singleton_Queue.getInstance(getBaseContext()).Add(request);
// End of Volly http request


    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
        startActivity(intent);

    }


    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private void checkPermissions() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                // when discover new Beacon device will add it to the beacon array list
                Student_HomePage.BeaconID.add(eddystone.getInstanceId());
            }


            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                // when Lose Beacon device will delete it from beacon array list
                Student_HomePage.BeaconID.remove(eddystone.getInstanceId());
            }
        };
    }

    public void SendAnnouncmentWhenNewMac_occure(final String Topic, final String StudentName) {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body", StudentName + "has make attendace with new Device");
                    jsonData.put("title", "Student with device");
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
                    // Toast.makeText(MainActivity.this, finalResponse,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(course_Info_for_student.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();

    }


    public void SendAnnouncmentWhenrejectStudents(final String Topic, final String StudentName) {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body", StudentName + " try to make attendance with device belongs to another student");
                    jsonData.put("title", "Reject Attendance");
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
                    // Toast.makeText(MainActivity.this, finalResponse,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(course_Info_for_student.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();

    }


}
