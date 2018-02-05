package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class adminHome extends AppCompatActivity implements Designable {


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    TextView welcomeTextView;
    private Button LogOutBTN;
    private Button AddCourseBTN;
    private Button AddTeacherBTN;
    private Button AddStudentBTN;
    private ProgressDialog progressDialog;

    //for add new course
    private EditText CourseID_AD, CourseName_AD, TeacherID_AD, ClassRommID_AD, STL_AD, ETL_AD, STA_AD, ETA_AD;
    private String TeacherID, ClassroomID;
    private Button Add_NewCourse_BTN;

    //for add new Teacher

    private EditText T_TeacherID_AD, T_Fname_AD, T_Lname_AD, T_Password_AD, T_email;
    private Button Add_NewTeacher_BTN;

    //for add new Student

    private EditText S_StudentID_AD, S_Fname_AD, S_Lname_AD, S_Password_AD, S_email;
    private Button Add_NewStudent_BTN;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        InitializeView();
    }

    @Override
    public void InitializeView() {

        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        welcomeTextView = findViewById(R.id.textView_NameOfAdmin);
        this.LogOutBTN = findViewById(R.id.button_Log_OUT);
        this.AddCourseBTN = findViewById(R.id.buttonOfAddCourse);
        this.AddTeacherBTN = findViewById(R.id.button2ForAddTeacher);
        this.AddStudentBTN = findViewById(R.id.button3ForAddStudent);
        this.progressDialog = new ProgressDialog(adminHome.this);


        Desing();
    }

    @Override
    public void Desing() {

        String NAMEOfADD = "Welcome Mr. " + sharedPreferences.getString(Constants.UserName, "");
        welcomeTextView.setText(NAMEOfADD);

        HandleAction();
    }

    @Override
    public void HandleAction() {


        AddStudentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_new_student, null, false);
                setContentView(v);

                Add_NewStudent_BTN = v.findViewById(R.id.buttonOfAddNewStudent);

                S_StudentID_AD = v.findViewById(R.id.editTextOfStudentID_inS);
                S_Fname_AD = v.findViewById(R.id.editTextFor_S_Fname);
                S_Lname_AD = v.findViewById(R.id.editTextFor_S_Lname);
                S_Password_AD = v.findViewById(R.id.editText_S_Pass);
                S_email = v.findViewById(R.id.editText_S_Email);


                Add_NewStudent_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {


                            if (S_StudentID_AD.getText().toString().trim().isEmpty() || S_Fname_AD.getText().toString().trim().isEmpty() || S_Lname_AD.getText().toString().trim().isEmpty() || S_Password_AD.getText().toString().trim().isEmpty() || S_email.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Please fill up all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No Internet ", Toast.LENGTH_LONG).show();

                            } else if (!IsEmailValid(S_email.getText().toString().trim())) {
                                Toast.makeText(getBaseContext(), "Please enter valid email.", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();


                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewStudent, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                     //     Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                                        try {


                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");

                                            if (status.equals("yes")) {

                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), "Student Added.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                                startActivity(intent);


                                            } else {
                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), "There is problem , try again ", Toast.LENGTH_LONG).show();

                                                S_StudentID_AD.setText("");
                                                S_Fname_AD.setText("");
                                                S_Lname_AD.setText("");
                                                S_Password_AD.setText("");
                                                S_email.setText("");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getBaseContext(), "  There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        /*** Here you put the HTTP request parameters **/

                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("S_StudentID", S_StudentID_AD.getText().toString());
                                        map.put("S_Fname", S_Fname_AD.getText().toString());
                                        map.put("S_Lname", S_Lname_AD.getText().toString());
                                        map.put("S_Password", S_Password_AD.getText().toString());
                                        map.put("S_Email", S_email.getText().toString());

                                        return map;
                                    }
                                };
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                            }


                        } catch (Exception e) {

                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        });








        AddTeacherBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_new_teacher, null, false);
                setContentView(v);

                Add_NewTeacher_BTN = v.findViewById(R.id.buttonOfAddNewTeacher);
                T_TeacherID_AD = v.findViewById(R.id.editTextOfTeacherID_inT);
                T_Fname_AD = v.findViewById(R.id.editTextFor_T_Fname);
                T_Lname_AD = v.findViewById(R.id.editTextFor_T_Lname);
                T_Password_AD = v.findViewById(R.id.editText_T_Pass);
                T_email = v.findViewById(R.id.editText_T_Email);


                Add_NewTeacher_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {


                            if (T_TeacherID_AD.getText().toString().trim().isEmpty() || T_Fname_AD.getText().toString().trim().isEmpty() || T_Lname_AD.getText().toString().trim().isEmpty() || T_Password_AD.getText().toString().trim().isEmpty() || T_email.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "الرجاء تعبئة جميع الحقول", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "لا يوجد أتصال بالانترنت", Toast.LENGTH_LONG).show();

                            } else if (!IsEmailValid(T_email.getText().toString().trim())) {
                                Toast.makeText(getBaseContext(), "البريد الألكتروني مدخل بطريقة غير صحيحة", Toast.LENGTH_LONG).show();
                            } else {
                                progressDialog.setMessage("الرجاء الأنتظار ...");
                                progressDialog.show();


                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewTeacher, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    //      Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                                        try {


                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");

                                            if (status.equals("yes")) {

                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), " تمت الأضافة بنجاح.", Toast.LENGTH_LONG).show();


                                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                                startActivity(intent);


                                            } else {
                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), " هناك مشكلة , الرجاء المحاولة مرة أخرى ", Toast.LENGTH_LONG).show();

                                                T_TeacherID_AD.setText("");
                                                T_Fname_AD.setText("");
                                                T_Lname_AD.setText("");
                                                T_Password_AD.setText("");
                                                T_email.setText("");

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

                                        map.put("Teacher_ID", T_TeacherID_AD.getText().toString());
                                        map.put("Fname", T_Fname_AD.getText().toString());
                                        map.put("Lname", T_Lname_AD.getText().toString());
                                        map.put("Password", T_Password_AD.getText().toString());
                                        map.put("Email", T_email.getText().toString());

                                        return map;
                                    }
                                };
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                            }


                        } catch (Exception e) {

                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        });
        LogOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder ConfirmationDialog = new AlertDialog.Builder(adminHome.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("هل تريد بالفعل تسجيل الخروج ؟");
                ConfirmationDialog.setTitle("تأكيد");
                ConfirmationDialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
                        } else {
                            editor.putBoolean(Constants.UserIsLoggedIn, false);
                            editor.commit();
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


        this.AddCourseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_add_new_course, null, false);
                setContentView(v);

                Add_NewCourse_BTN = v.findViewById(R.id.AddCourseBTN);
                CourseID_AD = v.findViewById(R.id.editTextForCourseID);
                CourseName_AD = v.findViewById(R.id.editTextForCourseName);
                TeacherID_AD = v.findViewById(R.id.editTextForTeacherIdOfCourse);
                ClassRommID_AD = v.findViewById(R.id.editTextForCLassRoomOfCourse);
                STL_AD = v.findViewById(R.id.editTextForSTL);
                ETL_AD = v.findViewById(R.id.editTextForETL);
                STA_AD = v.findViewById(R.id.editText2ForSTA);
                ETA_AD = v.findViewById(R.id.editTextForETA);


                Add_NewCourse_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {


                            if (CourseID_AD.getText().toString().trim().isEmpty() || CourseName_AD.getText().toString().trim().isEmpty() || STL_AD.getText().toString().trim().isEmpty() || ETL_AD.getText().toString().trim().isEmpty() || STA_AD.getText().toString().trim().isEmpty() || ETA_AD.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();

                            } else {
                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();


                                if (TeacherID_AD.getText().length() == 0) {
                                    TeacherID = "NULL";

                                } else {
                                    TeacherID = TeacherID_AD.getText().toString();

                                }

                                if (ClassRommID_AD.getText().length() == 0) {
                                    ClassroomID = "NULL";

                                } else {
                                    ClassroomID = ClassRommID_AD.getText().toString();
                                }


                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewCourse, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        //   Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                                        try {


                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");

                                            if (status.equals("yes")) {

                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), " Course Added.", Toast.LENGTH_LONG).show();


                                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                                startActivity(intent);


                                            } else {
                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), " Tehre is problem , try agine ", Toast.LENGTH_LONG).show();

                                                CourseID_AD.setText("");
                                                CourseName_AD.setText("");
                                                TeacherID_AD.setText("");
                                                ClassRommID_AD.setText("");
                                                STL_AD.setText("");
                                                ETL_AD.setText("");
                                                STA_AD.setText("");
                                                ETA_AD.setText("");

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

                                        map.put("course_id", CourseID_AD.getText().toString());
                                        map.put("course_name", CourseName_AD.getText().toString());
                                        map.put("teacherID", TeacherID);
                                        map.put("classroomID", ClassroomID);
                                        map.put("STL", STL_AD.getText().toString());
                                        map.put("ETL", ETL_AD.getText().toString());
                                        map.put("STA", STA_AD.getText().toString());
                                        map.put("ETA", ETA_AD.getText().toString());


                                        return map;
                                    }
                                };
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                            }


                        } catch (Exception e) {

                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });
            }
        });


    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);

    }


    public Boolean IsEmailValid(String Email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (Email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

}
