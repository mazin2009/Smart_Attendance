package com.example.mm_kau.smartattendance;

import android.Manifest;
import android.app.ProgressDialog;
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

public class course_Info_for_student extends AppCompatActivity  implements Designable{

    TextView C_id , C_name ,Teacher_name , C_CR , STL , ETL , STA, ETA , No_absent;
    Button ViewAttendanceInfo_BTN , MakeAttendance_BTN ;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ListView  listview_of_attendance_info;
    private ArrayList<String> list_attendance_info;

    private Boolean IsInsideTheClassroom = false;
    private ProximityManager proximityManager;
    public static final int REQUEST_CODE_PERMISSIONS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__info_for_student);
        KontaktSDK.initialize("wAJUXvCsDfWLqhkwYIiixyaNqeyBikIo");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setEddystoneListener(createEddystoneListener());
        checkPermissions();
        startScanning();
        setTitle("Course Information : "+getIntent().getStringExtra("name"));
        InitializeView();

    }

    @Override
    public void InitializeView() {

        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_Info_for_student.this);
        MakeAttendance_BTN = findViewById(R.id.buttonOfMakaAttendANCE);
        ViewAttendanceInfo_BTN = findViewById(R.id.button2OFLectureInfo_inST);
        C_id = findViewById(R.id.textViewForCRS_ID_st);
        C_id.setText(getIntent().getStringExtra("course_ID"));
        C_name = findViewById(R.id.textViewForCRS_Name_st);
        C_name.setText( getIntent().getStringExtra("name"));
        C_CR = findViewById(R.id.textViewForCRS_ClassRoom_st);
        Teacher_name = findViewById(R.id.textViewForTeacher_Name_st);
        STL = findViewById(R.id.textViewForCRS_STL_st);
        ETL = findViewById(R.id.textViewForCRS_ETL_st);
        STA = findViewById(R.id.textViewForCRS_STA_st);
        ETA = findViewById(R.id.textViewForCRS_ETA_st);
        No_absent = findViewById(R.id.textViewForCRS_No_absent_st);
        setNumberOfAbsent();

        if (getIntent().getStringExtra("Room_ID").equals("null")) {
            C_CR.setText("undefined");
        }else {

            C_CR.setText(getIntent().getStringExtra("Room_ID"));
        }

        if (getIntent().getStringExtra("T_name").equals("null")) {
            Teacher_name.setText("undefined");
        }else {

            Teacher_name.setText(getIntent().getStringExtra("T_name"));
        }


        STL.setText(getIntent().getStringExtra("STL"));
        ETL.setText(getIntent().getStringExtra("ETL"));
        STA.setText(getIntent().getStringExtra("STA"));
        ETA.setText(getIntent().getStringExtra("ETA"));

Desing();
    }

    @Override
    public void Desing() {

        HandleAction();
    }

    @Override
    public void HandleAction() {


        ViewAttendanceInfo_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_of_lecture_for_st, null, false);
                setContentView(v);
                setTitle("Attendance Information");
                list_attendance_info = new ArrayList<>();
                listview_of_attendance_info = v.findViewById(R.id.listTheLectureOfStu);

                StringRequest  request = new StringRequest(Request.Method.POST, Constants.GetLecture_forStudent, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String AttendanceInfo ;
                                String Date = jsonObject.getString("Date");
                                String State = jsonObject.getString("state");
                                AttendanceInfo = Date+","+State+","+sharedPreferences.getString(Constants.StudentID," ")+","+C_id.getText().toString()+","+getIntent().getStringExtra("T_ID")+","+sharedPreferences.getString(Constants.s_Fname,"") + " "+sharedPreferences.getString(Constants.s_Lname,"");

                                list_attendance_info.add(AttendanceInfo);
                            }



                            if (list_attendance_info.size() == 0) {

                                Toast.makeText(getBaseContext(), "There is no Lecture", Toast.LENGTH_LONG).show();
                            } else {

                                LictureAdpt_OfLecture_inST adapter = new LictureAdpt_OfLecture_inST(getBaseContext(), list_attendance_info);
                                listview_of_attendance_info.setAdapter(adapter);


                            }
                        } catch (JSONException e) {

                            Toast.makeText(getBaseContext(), "There is no Attendance Inforamtion", Toast.LENGTH_LONG).show();

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
                        map.put("CR_ID",C_id.getText().toString());
                        map.put("ST_ID",sharedPreferences.getString(Constants.StudentID," "));
                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);

            }
        });





        MakeAttendance_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(C_CR.getText().toString().equals("undefined")) {

                    Toast.makeText(getBaseContext(), "you cannot make attendance beacuse the classroom undefined", Toast.LENGTH_LONG).show();
                }else if (Student_HomePage.BeaconID.size()==0) {
                        Toast.makeText(getBaseContext(), "Please make sure you are inside the classroom number : "+C_CR.getText().toString()+" and Try agine", Toast.LENGTH_LONG).show();

                    }else {

                        StringRequest  request = new StringRequest(Request.Method.POST, Constants.GetBeacons, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONArray jsonArray = new JSONArray(response);


                                    for (int i = 0; i < Student_HomePage.BeaconID.size(); i++) {
                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            String B = jsonArray.getString(k);
                                            if(Student_HomePage.BeaconID.get(i).equals(B)) {
                                                IsInsideTheClassroom = true;
                                                break;
                                            }
                                        }

                                    }

                                    if (IsInsideTheClassroom) {


                                        Date currentTime = Calendar.getInstance(Locale.getDefault()).getTime();
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                        final String Date = df.format(currentTime);

                                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                        int minute = calendar.get(Calendar.MINUTE);
                                        final String Time = hour+":"+minute;
                                        final String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                                                Settings.Secure.ANDROID_ID);


                                        StringRequest request = new StringRequest(Request.Method.POST, Constants.MakeAttendance, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {


                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String status = jsonObject.getString("state");

                                                    if (status.equals("yes")) {

                                                        Toast.makeText(getBaseContext(), "yes", Toast.LENGTH_SHORT).show();


                                                              if(!jsonObject.isNull("mac_State")) {

                                                                  SendAnnouncmentWhenNewMac_occure(getIntent().getStringExtra("T_ID"),sharedPreferences.getString(Constants.s_Fname," ")+" "+sharedPreferences.getString(Constants.s_Lname,""));
                                                                  Toast.makeText(getBaseContext(), "present with new mac", Toast.LENGTH_SHORT).show();

                                                              }


                                                    } else if (status.equals("no")) {

                                                        if(!jsonObject.isNull("mac_State")) {
                                                            SendAnnouncmentWhenrejectStudents(getIntent().getStringExtra("T_ID"),sharedPreferences.getString(Constants.s_Fname," ")+" "+sharedPreferences.getString(Constants.s_Lname,""));
                                                            Toast.makeText(getBaseContext(), "absent with  mac belong to another", Toast.LENGTH_SHORT).show();

                                                        }
                                                        String r = jsonObject.getString("reason");
                                                        Toast.makeText(getBaseContext(), "no .. Becuse"+r, Toast.LENGTH_SHORT).show();
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
                                                map.put("Course_id",C_id.getText().toString());
                                                map.put("ST_ID", sharedPreferences.getString(Constants.StudentID," "));
                                                map.put("Date", Date);
                                                map.put("Time",Time);
                                                map.put("Mac", android_id);
                                                return map;
                                            }
                                        };
                                        Singleton_Queue.getInstance(getBaseContext()).Add(request);

                                        Toast.makeText(getBaseContext(), "You Are inside classroom  ", Toast.LENGTH_SHORT).show();


                                    }else {
                                        Toast.makeText(getBaseContext(), "You Are not inside classroom "+C_CR.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }


                                } catch (JSONException e) {

                                    Toast.makeText(getBaseContext(), "The is no Beacon", Toast.LENGTH_LONG).show();

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
                                map.put("ID",C_CR.getText().toString());
                                return map;
                            }
                        };
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);





                }







            }
        });





    }



    public void setNumberOfAbsent() {


        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetNumberOfabsent, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");

                    if (status.equals("yes")) {

                        String Nu_absent = jsonObject.getString("total_absent");
                        if(Nu_absent.equals("null")){
                            No_absent.setText("0");

                        }else {

                            No_absent.setText(Nu_absent);
                        }



                    } else {

                        No_absent.setText(" ");

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
                map.put("CR_ID", C_id.getText().toString());
                map.put("ID", sharedPreferences.getString(Constants.StudentID," "));
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);




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
                Toast.makeText(getBaseContext(), "Start Scan: ", Toast.LENGTH_SHORT).show();
                proximityManager.startScanning();
            }
        });
    }

    private void checkPermissions() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Student_HomePage.BeaconID.add(eddystone.getInstanceId());
            }

            
            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Student_HomePage.BeaconID.remove(eddystone.getInstanceId());
                Toast.makeText(getBaseContext(), "renove: "+eddystone.getInstanceId(), Toast.LENGTH_SHORT).show();

            }
        };
    }

    public void SendAnnouncmentWhenNewMac_occure(final String Topic , final String StudentName)  {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body",StudentName+"has make attendace with new Device");
                    jsonData.put("title","Student with device");
                    json.put("notification",jsonData);
                    json.put("to","/topics/"+Topic);

                    RequestBody body = RequestBody.create(JSON,json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization","key=AAAAjjSURVI:APA91bFYLZHZHRXlCr7bh1VHZf3ZDbu1d8ioyfIuzCR40hJks4ILEYLE1UaNqqAj7ECKbToUnEA1FL1ysGRTnD6v87g4_9iQ_81iAwhcKmAgz49G6pY8_87IkdISX899j_bQ_q6JnfCB")
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
                return  null;
            }
        }.execute();

    }


    public void SendAnnouncmentWhenrejectStudents(final String Topic , final String StudentName)  {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body",StudentName+" try to make attendance with device belongs to another student");
                    jsonData.put("title","Reject Attendance");
                    json.put("notification",jsonData);
                    json.put("to","/topics/"+Topic);

                    RequestBody body = RequestBody.create(JSON,json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization","key=AAAAjjSURVI:APA91bFYLZHZHRXlCr7bh1VHZf3ZDbu1d8ioyfIuzCR40hJks4ILEYLE1UaNqqAj7ECKbToUnEA1FL1ysGRTnD6v87g4_9iQ_81iAwhcKmAgz49G6pY8_87IkdISX899j_bQ_q6JnfCB")
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
                return  null;
            }
        }.execute();

    }



}
