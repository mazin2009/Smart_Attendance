package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Student_HomePage extends AppCompatActivity implements Designable {
    private ListView listView_course, ListViewMSG;
    private ArrayList<course> list_course;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    private ArrayList<Message> List_MSG;
    public static ArrayList<String> BeaconID;
    private Button LogOUT;
    private ImageView MsgBTN, SettingBTN;
    private TextView Name;
    private EditText newPass1, newPass2, prevPass;
    private ProgressDialog progressDialog;
    private android.app.AlertDialog alertDialog;
    // create proximityManager to manage the connection with Beacons.
    private ProximityManager proximityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__home_page);

        InitializeView();

    }

    @Override
    public void InitializeView() {

        // make new BeaconID arraylist object to state scan and save (or unsave) the connection beacons devices.
        BeaconID = new ArrayList<>();
        alertDialog = new android.app.AlertDialog.Builder(Student_HomePage.this).create();
        this.progressDialog = new ProgressDialog(Student_HomePage.this);
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        Name = findViewById(R.id.textViewForNAmeOfSTU);

        MsgBTN = findViewById(R.id.imageViewMessage);
        LogOUT = findViewById(R.id.buttonLogOUT_ST);
        SettingBTN = findViewById(R.id.imageViewSetting);

        listView_course = findViewById(R.id.listCoursesInStudent);
        list_course = new ArrayList<>();

        // configuration of proximityManager
        KontaktSDK.initialize("wAJUXvCsDfWLqhkwYIiixyaNqeyBikIo"); // Server API Key
        proximityManager = ProximityManagerFactory.create(this); // create new proximityManager
        proximityManager.setEddystoneListener(createEddystoneListener()); // set Listener to discover the beacons
        checkPermissions(); // check Permissions of bluetooth.
        startScanning(); // start scan for beacon device


        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        // call server to get all courses belong to this student.
        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetCoursesForStudent, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject CourseObject = jsonObject.getJSONObject("object");
                        String TeacherName = jsonObject.getString("Teacher_Name");

                        course Course = new course();
                        Course.setCourse_id(CourseObject.getString("Course_ID"));
                        Course.setCourse_Name(CourseObject.getString("Course_name"));
                        Course.setNumberOfStudent(Integer.parseInt(CourseObject.getString("No.of_Std")));
                        Course.setSTL(CourseObject.getString("S_T_L"));
                        Course.setETL(CourseObject.getString("E_T_L"));
                        Course.setSTA(CourseObject.getString("S_T_A"));
                        Course.setETA(CourseObject.getString("E_T_A"));

                        if (!CourseObject.getString("Teacher_ID").isEmpty()) {
                            Course.setTeacher_ID(CourseObject.getString("Teacher_ID"));
                            Course.setTeacher_name(TeacherName);
                        }
                        if (!CourseObject.getString("room_ID").isEmpty()) {
                            Course.setRoom_ID(CourseObject.getString("room_ID"));
                        }

                        FirebaseMessaging.getInstance().subscribeToTopic(Course.getCourse_id());
                        list_course.add(Course);
                    }


                    if (list_course.size() == 0) {
                        progressDialog.dismiss();
                        alertDialog.setMessage("There is no any course.");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        alertDialog.show();

                    } else {
                        progressDialog.dismiss();
                        // put the list of courses of student in Constants class to use it for unsubscribe From Topic when student log out.
                        Constants.list_course_of_Student = list_course;
                        Adapter_Course adapter = new Adapter_Course(getBaseContext(), list_course);
                        listView_course.setAdapter(adapter);


                        // on click listener for items in the list view
                        listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent = new Intent(getBaseContext(), course_Info_for_student.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("course_ID", list_course.get(i).getCourse_id());
                                intent.putExtra("name", list_course.get(i).getCourse_Name());
                                intent.putExtra("T_name", list_course.get(i).getTeacher_name());
                                intent.putExtra("T_ID", list_course.get(i).getTeacher_ID());
                                intent.putExtra("Room_ID", list_course.get(i).getRoom_ID());
                                intent.putExtra("STL", list_course.get(i).getSTL());
                                intent.putExtra("ETL", list_course.get(i).getETL());
                                intent.putExtra("STA", list_course.get(i).getSTA());
                                intent.putExtra("ETA", list_course.get(i).getETA());
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

                // HTTP request parameters
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ID", userfile.getString(Constants.StudentID, " "));
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

        setTitle("Student Home Page");
        Name.setText(userfile.getString(Constants.s_Fname, "") + " " + userfile.getString(Constants.s_Lname, ""));
        HandleAction();
    }

    @Override
    public void HandleAction() {


        SettingBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.setting, null, false);
                setContentView(v);

                setTitle("Setting");

                Button ChangePass = v.findViewById(R.id.button2ChangePass);

                ChangePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.change_pass, null, false);
                        setContentView(v);
                        setTitle("Change Password");

                        newPass1 = v.findViewById(R.id.editTextRestPass1);
                        newPass2 = v.findViewById(R.id.editText2RestPass2);
                        prevPass = v.findViewById(R.id.editText2PrevPass);

                        Button Send_ResetPass = v.findViewById(R.id.button2ResetPass);

                        Send_ResetPass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                if (newPass1.getText().toString().trim().isEmpty() || newPass2.getText().toString().trim().isEmpty() || prevPass.getText().toString().trim().isEmpty()) {
                                    Toast.makeText(getBaseContext(), "؛please fill in all fild", Toast.LENGTH_LONG).show();
                                } else if (Network.isConnected(getBaseContext()) == false) {
                                    Toast.makeText(getBaseContext(), "No Internet", Toast.LENGTH_LONG).show();
                                } else if (!newPass1.getText().toString().equals(newPass2.getText().toString())) {
                                    Toast.makeText(getBaseContext(), "Two passwords are not the same", Toast.LENGTH_LONG).show();
                                } else {

                                    progressDialog.setMessage("please wait ...");
                                    progressDialog.show();
                                    // call server to change the password
                                    StringRequest request = new StringRequest(Request.Method.POST, Constants.changePass, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {


                                            try {

                                                JSONObject jsonObject = new JSONObject(response);
                                                String status = jsonObject.getString("state");

                                                if (status.equals("yes")) {

                                                    progressDialog.dismiss();

                                                    Toast.makeText(getBaseContext(), "the password has been changed", Toast.LENGTH_LONG).show();
                                                    userfileEditer.putBoolean(Constants.UserIsLoggedIn, false);
                                                    userfileEditer.commit();
                                                    for (int i = 0; i < Constants.list_course_of_Student.size(); i++) {
                                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.list_course_of_Student.get(i).getCourse_id());
                                                    }

                                                    Intent intent = new Intent(getBaseContext(), LoginPage.class);
                                                    startActivity(intent);

                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "The previous Password is incorrect ", Toast.LENGTH_LONG).show();
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
                                            map.put("ID", userfile.getString(Constants.StudentID, " "));
                                            map.put("prevPass", prevPass.getText().toString());
                                            map.put("password", newPass1.getText().toString());
                                            map.put("UserType", userfile.getString(Constants.UserType, " "));
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


            }
        });


        LogOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder ConfirmationDialog = new AlertDialog.Builder(Student_HomePage.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("Do you want to logout ?");
                ConfirmationDialog.setTitle("Confirm");
                ConfirmationDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "no connection", Toast.LENGTH_LONG).show();
                        } else {

                            // unsubscribe From Topic (Topic is the all courses ID student study it. )
                            for (int i = 0; i < Constants.list_course_of_Student.size(); i++) {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.list_course_of_Student.get(i).getCourse_id());
                            }

                            // logout teacher in cach file.
                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, false);
                            userfileEditer.commit();
                            Intent intent = new Intent(getBaseContext(), LoginPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });

                ConfirmationDialog.setNegativeButton("No", null);
                ConfirmationDialog.show();


            }
        });


        MsgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.msg_list, null, false);
                setContentView(v);

                // set title of action bar.
                setTitle("Message box");

                List_MSG = new ArrayList<>();
                ListViewMSG = v.findViewById(R.id.listMsg);

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                // call server to get all message
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetMSG, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Message MsgInfo = new Message();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject MsgObject = jsonObject.getJSONObject("Msg");
                                String TeacherName = jsonObject.getString("TeacherName");
                                MsgInfo.setM_ID(MsgObject.getString("ID"));
                                MsgInfo.setTeacherID(MsgObject.getString("Teacher_ID"));
                                MsgInfo.setTeacheName(TeacherName);
                                MsgInfo.setCourseID(MsgObject.getString("CourseID"));
                                MsgInfo.setTitle(MsgObject.getString("Title"));
                                MsgInfo.setBody(MsgObject.getString("body"));
                                MsgInfo.setDate(MsgObject.getString("Date"));
                                MsgInfo.setCourseNAme(jsonObject.getString("CourseName"));

                                List_MSG.add(MsgInfo);
                            }


                            if (List_MSG.size() == 0) {

                                progressDialog.dismiss();
                                alertDialog.setMessage("There is no any Message.");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
                                        startActivity(intent);
                                    }
                                });

                                alertDialog.show();


                            } else {

                                progressDialog.dismiss();
                                Collections.reverse(List_MSG); // reverse the Message to put the new one first.

                                Adapter_Message adapter = new Adapter_Message(getBaseContext(), List_MSG);
                                ListViewMSG.setAdapter(adapter);


                                // on click listener for items in the list view
                                ListViewMSG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.body_msg, null, false);
                                        setContentView(v);
                                        TextView Body = v.findViewById(R.id.editTextForBodymsg);
                                        Body.setText(List_MSG.get(i).getBody());

                                    }
                                });


                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), " There is problem , try agine ", Toast.LENGTH_LONG).show();

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
                        map.put("ID", userfile.getString(Constants.StudentID, ""));
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
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }


    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                // when discover new Beacon device will add it to the beacon array list
                BeaconID.add(eddystone.getInstanceId());
            }


            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                // when Lose Beacon device will delete it from beacon array list
                BeaconID.remove(eddystone.getInstanceId());
            }
        };
    }


}
