package com.example.mm_kau.smartattendance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class course_Info_for_student extends AppCompatActivity  implements Designable{

    TextView C_id , C_name ,Teacher_name , C_CR , STL , ETL , STA, ETA , No_absent;
    Button ViewAttendanceInfo_BTN , MakeAttendance_BTN ;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ListView listview_attendance_info ;
    private ArrayList<String> list_attendance_info;
    private ArrayList<String> BeaconID;
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
        InitializeView();

    }

    @Override
    public void InitializeView() {
        BeaconID =  new ArrayList<>();
        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_Info_for_student.this);
        MakeAttendance_BTN = findViewById(R.id.buttonOfMakaAttendANCE);
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

        MakeAttendance_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(C_CR.getText().toString().equals("undefined")) {

                    Toast.makeText(getBaseContext(), "you cannot make attendance beacuse the classroom undefined", Toast.LENGTH_LONG).show();
                }else if (BeaconID.size()==0) {
                        Toast.makeText(getBaseContext(), "Please make sure you are inside the classroom number : "+C_CR.getText().toString()+" and Try agine", Toast.LENGTH_LONG).show();

                    }else {

                        StringRequest  request = new StringRequest(Request.Method.POST, Constants.GetBeacons, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONArray jsonArray = new JSONArray(response);


                                    for (int i = 0; i < BeaconID.size(); i++) {
                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            String B = jsonArray.getString(k);
                                            if(BeaconID.get(i).equals(B)) {
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

                                                                  Toast.makeText(getBaseContext(), "present with new mac", Toast.LENGTH_SHORT).show();

                                                              }


                                                    } else if (status.equals("no")) {

                                                        if(!jsonObject.isNull("mac_State")) {

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
                BeaconID.add(eddystone.getInstanceId());
            }

            
            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                BeaconID.remove(eddystone.getInstanceId());
                Toast.makeText(getBaseContext(), "renove: "+eddystone.getInstanceId(), Toast.LENGTH_SHORT).show();

            }
        };
    }



}
