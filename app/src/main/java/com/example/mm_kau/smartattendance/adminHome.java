package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class adminHome extends AppCompatActivity implements Designable  {


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    TextView welcomeTextView;
    private Button LogOutBTN;
    private Button AddCourseBTN;
    private Button AddTeacherBTN;
    private Button AddStudentBTN;
    private Button AddClassRoomBTN;
    private Button ManageCoursesBTN;
    private Button ManageTeacherBTN;
    private ProgressDialog progressDialog;
    private ListView listView_course , listview_Teacher ;
    private ArrayList<course> list_course;
    private ArrayList<teacher> list_teacher;
    TextView No_courses , No_Teachers ;
    Button DeleteAllCourseBTN , DeleteAllTeacherBTN;


    //for add new course
    private EditText CourseID_AD, CourseName_AD, TeacherID_AD, ClassRommID_AD, STL_AD, ETL_AD, STA_AD, ETA_AD , NoOf_week;
    private CheckBox S,M,Tu,W,TH;
    private DatePicker StartDayinWeek;
    private String TeacherID, ClassroomID;
    private Button Add_NewCourse_BTN;

    //for add new Teacher

    private EditText T_TeacherID_AD, T_Fname_AD, T_Lname_AD, T_Password_AD, T_email;
    private Button Add_NewTeacher_BTN;

    //for add new Student

    private EditText S_StudentID_AD, S_Fname_AD, S_Lname_AD, S_Password_AD, S_email;
    private Button Add_NewStudent_BTN;

    //for add new classroom

    private EditText C_ClassRoomID, C_ClassRoomName,C_capacity , C_Beacons ;
    private Button Add_NewClassRoom_BTN;




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
        this.AddClassRoomBTN = findViewById(R.id.buttonOfAddClassroom);
        this.ManageCoursesBTN = findViewById(R.id.button4MangeCourse);
        ManageTeacherBTN = findViewById(R.id.button5ManageTeacher);
        this.progressDialog = new ProgressDialog(adminHome.this);


        Desing();
    }

    @Override
    public void Desing() {

        String NAMEOfADD = "Welcome Mr. " + sharedPreferences.getString(Constants.adminName, "");
        welcomeTextView.setText(NAMEOfADD);

        HandleAction();
    }

    @Override
    public void HandleAction() {



        ManageTeacherBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.teacher_list, null, false);
                setContentView(v);
                list_teacher = new ArrayList<>();
                listview_Teacher = v.findViewById(R.id.listTheTeacher);
                No_Teachers = v.findViewById(R.id.no_Teacher);
                DeleteAllTeacherBTN = v.findViewById(R.id.button3ForDeleteAllTeacher);


            }
        });





        ManageCoursesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.course_list, null, false);
                setContentView(v);

                list_course = new ArrayList<>();
                listView_course = v.findViewById(R.id.listTheCourse);
                No_courses = v.findViewById(R.id.no_Courses);
               DeleteAllCourseBTN = v.findViewById(R.id.button3ForDeleteAllCourses);
                StringRequest  request = new StringRequest(Request.Method.POST, Constants.GetCourses, new Response.Listener<String>() {
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
                                 No_courses.setText("There is no clases ");
                            } else {
                                MyCoursAdpt adapter = new MyCoursAdpt(getBaseContext(), list_course);
                                listView_course.setAdapter(adapter);

                                listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Intent intent=new Intent(getBaseContext(),Manage_Course.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("course_ID",list_course.get(i).getCourse_id());
                                        intent.putExtra("name",list_course.get(i).getCourse_Name());
                                        intent.putExtra("TeacherID",list_course.get(i).getTeacher_ID());
                                        intent.putExtra("Room_ID",list_course.get(i).getRoom_ID());
                                        intent.putExtra("STL",list_course.get(i).getSTL());
                                        intent.putExtra("ETL",list_course.get(i).getETL());
                                        intent.putExtra("STA",list_course.get(i).getSTA());
                                        intent.putExtra("ETA",list_course.get(i).getETA());
                                        intent.putExtra("E","D");
                                        startActivity(intent);


                                    }
                                });




                            }
                        } catch (JSONException e) {

                            No_courses.setText("There is no clases ");



                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();


                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);



                DeleteAllCourseBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        StringRequest request = new StringRequest(Request.Method.POST, Constants.DeleteAllCourse, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");
                                    if (status.equals("yes")) {

                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "All Courses deleted.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), adminHome.class);
                                        startActivity(intent);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), " Tehre is problem , try agine ", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {

                                    Toast.makeText(getBaseContext(), "There is an error , try agine.", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);

                    }
                });



            }
        });



        AddClassRoomBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_new_classroom, null, false);
                setContentView(v);

                Add_NewClassRoom_BTN = v.findViewById(R.id.buttonOfAddNewclassRoom);

                C_ClassRoomID = v.findViewById(R.id.editTextOfclassroom_ID);
                C_ClassRoomName = v.findViewById(R.id.editTextFor_classroom_Name);
                C_capacity = v.findViewById(R.id.editText_Capacity);
                C_Beacons = v.findViewById(R.id.editText_Beacons);


                Add_NewClassRoom_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {


                            if (C_ClassRoomID.getText().toString().trim().isEmpty() || C_ClassRoomName.getText().toString().trim().isEmpty() || C_Beacons.getText().toString().trim().isEmpty() || C_capacity.getText().toString().trim().isEmpty() ) {

                                Toast.makeText(getBaseContext(), "Please fill up all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No Internet ", Toast.LENGTH_LONG).show();

                            } else {
                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();

                                final String [] arr_Beacon = C_Beacons.getText().toString().split(",");

                                final JSONArray BeaconJSONArray = new JSONArray(Arrays.asList(arr_Beacon));

                             //   Toast.makeText(getBaseContext(), BeaconJSONArray.toString(), Toast.LENGTH_LONG).show();


                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewClassRoom, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                      //      Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                                        try {


                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");

                                            if (status.equals("yes")) {

                                                progressDialog.dismiss();

                                                Toast.makeText(getBaseContext(), "classRoom Added.", Toast.LENGTH_LONG).show();
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
                                    protected Map<String, String > getParams() throws AuthFailureError {
                                        /*** Here you put the HTTP request parameters **/

                                        HashMap<String , String > map = new HashMap<>();

                                    map.put("RoomID", C_ClassRoomID.getText().toString());
                                    map.put("RoomName", C_ClassRoomName.getText().toString());
                                    map.put("capacity", C_capacity.getText().toString());
                                    map.put("BeaconIDs", BeaconJSONArray.toString());

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
                NoOf_week = v.findViewById(R.id.editTextForNumberOfweek);
                S = v.findViewById(R.id.checkBoxSun);
                M = v.findViewById(R.id.checkBox2Mon);
                Tu = v.findViewById(R.id.checkBox3Tue);
                W = v.findViewById(R.id.checkBox4Wed);
                TH = v.findViewById(R.id.checkBox5Thur);
                StartDayinWeek = v.findViewById(R.id.FirstDayonFirstWeek);




                Add_NewCourse_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                            Date date = new Date(StartDayinWeek.getYear(), StartDayinWeek.getMonth(), StartDayinWeek.getDayOfMonth()-1);
                            String dayOfWeek_ForCheek = simpledateformat.format(date);


                            if (NoOf_week.getText().toString().trim().isEmpty() || CourseID_AD.getText().toString().trim().isEmpty() || CourseName_AD.getText().toString().trim().isEmpty() || STL_AD.getText().toString().trim().isEmpty() || ETL_AD.getText().toString().trim().isEmpty() || STA_AD.getText().toString().trim().isEmpty() || ETA_AD.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();

                            }else if (!dayOfWeek_ForCheek.equals("Sunday") && !dayOfWeek_ForCheek.equals("الأحد")){

                                Toast.makeText(getBaseContext(), "The First day in week must be Sunday .", Toast.LENGTH_LONG).show();

                        }else {

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

                                            String dt = ""+StartDayinWeek.getYear()+"-"+(StartDayinWeek.getMonth()+1)+"-"+StartDayinWeek.getDayOfMonth();
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            Calendar c = Calendar.getInstance();
                                            c.setTime(sdf.parse(dt));
                                            dt = sdf.format(c.getTime());  // dt is now the new date

                                            if (status.equals("yes")) {

                                                for (int i = 0 ; i<Integer.parseInt(NoOf_week.getText().toString());i++) {

                                                    if(S.isChecked()) {
                                                        Calendar cS= Calendar.getInstance();
                                                        cS.setTime(sdf.parse(dt));
                                                        String dtS = sdf.format(cS.getTime());  // dt is now the new date
                                                        AddLecture(CourseID_AD.getText().toString(),dtS);

                                                    }
                                                    if(M.isChecked()) {
                                                        Calendar cM = Calendar.getInstance();
                                                        cM.setTime(sdf.parse(dt));
                                                        cM.add(Calendar.DATE, 1);  // number of days to add
                                                        String dtM = sdf.format(cM.getTime());  // dt is now the new date
                                                        AddLecture(CourseID_AD.getText().toString(),dtM);

                                                    }
                                                    if(Tu.isChecked()) {
                                                        Calendar cTu = Calendar.getInstance();
                                                        cTu.setTime(sdf.parse(dt));
                                                        cTu.add(Calendar.DATE, 2);  // number of days to add
                                                        String dtTu = sdf.format(cTu.getTime());  // dt is now the new date
                                                        AddLecture(CourseID_AD.getText().toString(),dtTu);
                                                    }
                                                    if(W.isChecked()) {
                                                        Calendar cW = Calendar.getInstance();
                                                        cW.setTime(sdf.parse(dt));
                                                        cW.add(Calendar.DATE, 3);  // number of days to add
                                                        String dtW = sdf.format(cW.getTime());  // dt is now the new date
                                                        AddLecture(CourseID_AD.getText().toString(),dtW);
                                                    }
                                                    if(TH.isChecked()) {
                                                        Calendar cTH = Calendar.getInstance();
                                                        cTH.setTime(sdf.parse(dt));
                                                        cTH.add(Calendar.DATE, 4);  // number of days to add
                                                        String dtTH = sdf.format(cTH.getTime());  // dt is now the new date
                                                        AddLecture(CourseID_AD.getText().toString(),dtTH);
                                                    }

                                                    c.setTime(sdf.parse(dt));
                                                    c.add(Calendar.DATE, 7);  // number of days to add
                                                    dt = sdf.format(c.getTime());  // dt is now the new date

                                                }


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
                                        } catch (ParseException e) {
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

    public void AddLecture (final String CourseID , final String Date) {


        try {


                StringRequest request = new StringRequest(Request.Method.POST, Constants.AddLecture, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //      Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("state");

                            if (status.equals("yes")) {



                            } else {
                                Toast.makeText(getBaseContext(), " هناك مشكلة , الرجاء المحاولة مرة أخرى ", Toast.LENGTH_LONG).show();
                            return;
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

                        map.put("date",Date);
                        map.put("CourseID", CourseID);

                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);


        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }



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
