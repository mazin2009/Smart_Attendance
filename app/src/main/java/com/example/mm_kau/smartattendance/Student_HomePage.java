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

import static com.example.mm_kau.smartattendance.course_Info_for_student.REQUEST_CODE_PERMISSIONS;

public class Student_HomePage extends AppCompatActivity implements Designable {
    private ListView listView_course  ;
    private ArrayList<course> list_course;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditer;
    private ArrayList<Message> List_MSG;
    public static ArrayList<String> BeaconID;
    ListView ListViewMSG;
    Button LogOUT;
    ImageView MsgBTN , SettingBTN;
    TextView Name;
    EditText newPass1 , newPass2 , prevPass;
    private ProgressDialog progressDialog;
    private ProximityManager proximityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__home_page);
        setTitle("Student Home Page");
        InitializeView();

    }

    @Override
    public void InitializeView() {
        BeaconID =  new ArrayList<>();
        this.progressDialog = new ProgressDialog(Student_HomePage.this);
        this.sharedPreferences=getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.sharedPreferencesEditer=sharedPreferences.edit();
        Name = findViewById(R.id.textViewForNAmeOfSTU);
        Name.setText(sharedPreferences.getString(Constants.s_Fname,"") +" "+ sharedPreferences.getString(Constants.s_Lname,""));

        MsgBTN = findViewById(R.id.imageViewMessage);
        LogOUT = findViewById(R.id.buttonLogOUT_ST);
        SettingBTN = findViewById(R.id.imageViewSetting);

        listView_course = findViewById(R.id.listCoursesInStudent);
        list_course = new ArrayList<>();

        KontaktSDK.initialize("wAJUXvCsDfWLqhkwYIiixyaNqeyBikIo");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setEddystoneListener(createEddystoneListener());
        checkPermissions();
        startScanning();

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
                        if (!CourseObject.getString(	"room_ID").isEmpty()) {
                            Course.setRoom_ID(CourseObject.getString("room_ID"));
                        }

                        FirebaseMessaging.getInstance().subscribeToTopic(Course.getCourse_id());
                        list_course.add(Course);
                    }


                    if (list_course.size() == 0) {
                        //       No_courses.setText("There is no clases ");
                    } else {

                        Constants.list_course_of_Student = list_course;

                        MyCoursAdpt adapter = new MyCoursAdpt(getBaseContext(), list_course);
                        listView_course.setAdapter(adapter);

                        listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent=new Intent(getBaseContext(),course_Info_for_student.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("course_ID",list_course.get(i).getCourse_id());
                                intent.putExtra("name",list_course.get(i).getCourse_Name());
                                intent.putExtra("T_name",list_course.get(i).getTeacher_name());
                                intent.putExtra("T_ID",list_course.get(i).getTeacher_ID());
                                intent.putExtra("Room_ID",list_course.get(i).getRoom_ID());
                                intent.putExtra("STL",list_course.get(i).getSTL());
                                intent.putExtra("ETL",list_course.get(i).getETL());
                                intent.putExtra("STA",list_course.get(i).getSTA());
                                intent.putExtra("ETA",list_course.get(i).getETA());
                                startActivity(intent);

                            }
                        });




                    }
                } catch (JSONException e) {


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ID",sharedPreferences.getString(Constants.StudentID," "));
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);




        Design();

    }

    @Override
    public void Design() {


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

                                    StringRequest request = new StringRequest(Request.Method.POST, Constants.changePass, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {


                                            try {


                                                JSONObject jsonObject = new JSONObject(response);
                                                String status = jsonObject.getString("state");

                                                if (status.equals("yes")) {

                                                    progressDialog.dismiss();

                                                    Toast.makeText(getBaseContext(), "the password has been changed", Toast.LENGTH_LONG).show();
                                                    sharedPreferencesEditer.putBoolean(Constants.UserIsLoggedIn, false);
                                                    sharedPreferencesEditer.commit();
                                                    for (int i = 0 ; i<Constants.list_course_of_Student.size() ; i++){
                                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.list_course_of_Student.get(i).getCourse_id());
                                                    }

                                                    Intent intent = new Intent(getBaseContext(), LoginPage.class);
                                                    startActivity(intent);

                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "The previous Password is incorrect ", Toast.LENGTH_LONG).show();
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
                                            map.put("ID", sharedPreferences.getString(Constants.StudentID," "));
                                            map.put("prevPass", prevPass.getText().toString());
                                            map.put("password", newPass1.getText().toString());
                                            map.put("UserType", sharedPreferences.getString(Constants.UserType," "));
                                            return map;
                                        }
                                    };
                                    Singleton_Queue.getInstance(getBaseContext()).Add(request);
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
                ConfirmationDialog.setMessage("Do you want log out ?");
                ConfirmationDialog.setTitle("Confirm");
                ConfirmationDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "no connection", Toast.LENGTH_LONG).show();
                        } else {

                            for (int i = 0 ; i<Constants.list_course_of_Student.size() ; i++){

                                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.list_course_of_Student.get(i).getCourse_id());
                            }


                            sharedPreferencesEditer.putBoolean(Constants.UserIsLoggedIn, false);
                            sharedPreferencesEditer.commit();
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
                setTitle("Message box");

                List_MSG = new ArrayList<>();
                ListViewMSG = v.findViewById(R.id.listMsg);


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

                            Toast.makeText(getBaseContext(), "There is no Message", Toast.LENGTH_LONG).show();

                            } else {
                                Collections.reverse(List_MSG);
                                Msg_Adpt adapter = new Msg_Adpt(getBaseContext(), List_MSG);
                                ListViewMSG.setAdapter(adapter);

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

                            Toast.makeText(getBaseContext(), "There is no students"+e.getMessage(), Toast.LENGTH_LONG).show();

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
                        map.put("ID",sharedPreferences.getString(Constants.StudentID,""));
                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);





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
                Toast.makeText(getBaseContext(), "Start Scan: ", Toast.LENGTH_SHORT).show();
                proximityManager.startScanning();
            }
        });
    }

    private void checkPermissions() {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
             BeaconID.add(eddystone.getInstanceId());
                Toast.makeText(getBaseContext(), "Add: "+eddystone.getInstanceId(), Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                BeaconID.remove(eddystone.getInstanceId());
                Toast.makeText(getBaseContext(), "Remove: "+eddystone.getInstanceId(), Toast.LENGTH_SHORT).show();

            }
        };
    }


}
