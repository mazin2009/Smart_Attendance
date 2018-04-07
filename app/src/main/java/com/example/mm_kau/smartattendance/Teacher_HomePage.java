package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Teacher_HomePage extends AppCompatActivity implements Designable {
    private ListView listView_course  ;
    private ArrayList<course> list_course;
    private TextView Name;
    private Bitmap decodedByte;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditer;
    Button LogOUT ;
    ImageView requestExcuse_BTN , SettingBTN ;
    private ArrayList<excuse> List_Excuse;
    EditText newPass1 , newPass2 , prevPass;
    ListView ListViewExcuse;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__home_page);
        setTitle("Teacher Home Page");
        InitializeView();
    }

    @Override
    public void InitializeView() {
        SettingBTN = findViewById(R.id.imageViewSetting);
        this.progressDialog = new ProgressDialog(Teacher_HomePage.this);
        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        sharedPreferencesEditer = sharedPreferences.edit();
        requestExcuse_BTN = findViewById(R.id.imageViewNewRequesttExcuse);
        LogOUT = findViewById(R.id.buttonLogOUT_Teacher);
        listView_course = findViewById(R.id.listCourseForStudent);
        list_course = new ArrayList<>();
        Name = findViewById(R.id.textViewForNAmeOfTEACHER);
        Name.setText(sharedPreferences.getString(Constants.T_Fname,"") +" "+ sharedPreferences.getString(Constants.T_Lname,""));

        FirebaseMessaging.getInstance().subscribeToTopic(sharedPreferences.getString(Constants.TeacherID," "));

        StringRequest request = new StringRequest(Request.Method.POST, Constants.getCourseByID_forTeacher, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        course Course = new course();
                        Course.setCourse_id(jsonObject.getString("Course_ID"));
                        Course.setCourse_Name(jsonObject.getString("Course_name"));
                        Course.setNumberOfStudent(Integer.parseInt(jsonObject.getString("No.of_Std")));
                        Course.setSTL(jsonObject.getString("S_T_L"));
                        Course.setETL(jsonObject.getString("E_T_L"));
                        Course.setSTA(jsonObject.getString("S_T_A"));
                        Course.setETA(jsonObject.getString("E_T_A"));

                        if (!jsonObject.getString("Teacher_ID").isEmpty()) {
                            Course.setTeacher_ID(jsonObject.getString("Teacher_ID"));
                        }
                        if (!jsonObject.getString(	"room_ID").isEmpty()) {
                            Course.setRoom_ID(jsonObject.getString("room_ID"));
                        }

                        list_course.add(Course);
                    }



                    if (list_course.size() == 0) {
                 //       No_courses.setText("There is no clases ");
                    } else {
                        MyCoursAdpt adapter = new MyCoursAdpt(getBaseContext(), list_course);
                        listView_course.setAdapter(adapter);

                        listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent=new Intent(getBaseContext(),course_info_for_teacher.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("course_ID",list_course.get(i).getCourse_id());
                                intent.putExtra("name",list_course.get(i).getCourse_Name());
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
                map.put("ID",sharedPreferences.getString(Constants.TeacherID," "));
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
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPreferences.getString(Constants.TeacherID,""));
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
                                            map.put("ID", sharedPreferences.getString(Constants.TeacherID," "));
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



                AlertDialog.Builder ConfirmationDialog = new AlertDialog.Builder(Teacher_HomePage.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("Do you want to logout ?");
                ConfirmationDialog.setTitle("sure");
                ConfirmationDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "no connection", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPreferences.getString(Constants.TeacherID,""));
                            sharedPreferencesEditer.putBoolean(Constants.UserIsLoggedIn, false);
                            sharedPreferencesEditer.commit();
                            Intent intent = new Intent(getBaseContext(), LoginPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
                ConfirmationDialog.setNegativeButton("لا", null);
                ConfirmationDialog.show();


            }
        });





requestExcuse_BTN.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.excuse_list, null, false);
        setContentView(v);
        setTitle("List Of Excuse ");
        List_Excuse = new ArrayList<>();
        ListViewExcuse = v.findViewById(R.id.listExcuse);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.getListOfExcuse, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        excuse excuse_Info = new excuse();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject ExcuseObject = jsonObject.getJSONObject("Excuse");
                        String StudentName = jsonObject.getString("Student_name");
                        String CourseName = jsonObject.getString("CourseName");

                        excuse_Info.setEx_ID(ExcuseObject.getString("Exuse_id"));
                        excuse_Info.setTeacher_ID(ExcuseObject.getString("Teacher_ID"));
                        excuse_Info.setStudent_ID(ExcuseObject.getString("studentID"));
                        excuse_Info.setCourse_ID(ExcuseObject.getString("Course_id"));
                        excuse_Info.setDate(ExcuseObject.getString("Date"));
                        excuse_Info.setText(ExcuseObject.getString("Text"));
                        excuse_Info.setState(ExcuseObject.getString("State"));
                        excuse_Info.setImage(ExcuseObject.getString("Image"));
                        excuse_Info.setStudent_name(StudentName);
                        excuse_Info.setCourse_name(CourseName);

                            List_Excuse.add(excuse_Info);


                    }



                    if (List_Excuse.size() == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "There is no Excuse requset", Toast.LENGTH_LONG).show();

                    } else {

                        Collections.reverse(List_Excuse);
                        Excuse_apdt adapter = new Excuse_apdt(getBaseContext(), List_Excuse);
                        ListViewExcuse.setAdapter(adapter);
                        progressDialog.dismiss();

                        ListViewExcuse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                                final String Ex_ID = List_Excuse.get(i).getEx_ID();
                                final String Date = List_Excuse.get(i).getDate();
                                final String Course_ID = List_Excuse.get(i).getCourse_ID();
                                final String StudentID = List_Excuse.get(i).getStudent_ID();

                                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_excuse, null, false);
                                setContentView(v);
                                setTitle("Excuse from :  "+List_Excuse.get(i).getStudent_name());

                            ImageView IMG_View = v.findViewById(R.id.imageViewForShowExcueInTeacher);

                                byte[] decodedString = Base64.decode(List_Excuse.get(i).getImage(), Base64.DEFAULT);
                                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                IMG_View.setImageBitmap(decodedByte);

                                TextView TEXT = v.findViewById(R.id.textView15OfMessageOfExcuse);
                                TEXT.setText(List_Excuse.get(i).getText());

                               Button FullImage = v.findViewById(R.id.buttonOfFullImage);
                                FullImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(Teacher_HomePage.this);
                                        View VIEW = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_image_dialog, null, false);
                                        ImageView imageView =  VIEW.findViewById(R.id.image_dialog);
                                        imageView.setImageBitmap(decodedByte);
                                        dialog.setView(VIEW);
                                        dialog.setCancelable(true);
                                        dialog.show();

                                    }
                                });


                Button approve = v.findViewById(R.id.button2Aprrove);
                Button reject = v.findViewById(R.id.button3Reject);


                                approve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        StringRequest request = new StringRequest(Request.Method.POST, Constants.approveExcue, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {

                                                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String status = jsonObject.getString("state");


                                                    if (status.equals("yes")) {


                                                        Toast.makeText(getBaseContext(), " Excuse Approved", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(getBaseContext(), " There is problem , try agine ", Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {

                                                    e.printStackTrace();

                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                /*** Here you put the HTTP request parameters **/

                                                HashMap<String, String> map = new HashMap<>();

                                                map.put("Excuse_id",Ex_ID );
                                                map.put("Date",Date);
                                                map.put("CourseID",Course_ID);
                                                map.put("Student_ID", StudentID);
                                                return map;
                                            }
                                        };
                                        Singleton_Queue.getInstance(getBaseContext()).Add(request);


                                    }
                                });

                                reject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        StringRequest request = new StringRequest(Request.Method.POST, Constants.rejectExcuse, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {

                                                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String status = jsonObject.getString("state");


                                                    if (status.equals("yes")) {


                                                        Toast.makeText(getBaseContext(), " Excuse Rejected", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(getBaseContext(), " There is problem , try agine ", Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {

                                                    e.printStackTrace();

                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                /*** Here you put the HTTP request parameters **/

                                                HashMap<String, String> map = new HashMap<>();

                                                map.put("Excuse_id",Ex_ID );
                                                return map;
                                            }
                                        };
                                        Singleton_Queue.getInstance(getBaseContext()).Add(request);

                                    }
                                });

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
                map.put("ID",sharedPreferences.getString(Constants.TeacherID,""));
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);






    }
});



    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }






}
