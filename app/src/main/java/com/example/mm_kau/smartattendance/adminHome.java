package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class adminHome extends AppCompatActivity implements Designable {

    private Button DeleteAllCourseBTN, LogOutBTN, AddCourseBTN, AddTeacherBTN, AddStudentBTN, AddClassRoomBTN, ManageCoursesBTN, ManageTeacherBTN, ManageStudentBTN, ManageCRtBTN;
    private ListView listView_course, listview_Teacher, listview_student, listview_CR;
    private ArrayList<course> list_course;
    private ArrayList<teacher> list_teacher;
    private ArrayList<student> list_student;
    private ArrayList<classroom> list_classroom;
    private TextView welcomeTextView;
    private ProgressDialog progressDialog;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;


    //for add new course
    private EditText CourseID_AD, CourseName_AD, TeacherID_AD, ClassRommID_AD, NoOf_week;
    private TimePicker STL_AD, ETL_AD, STA_AD, ETA_AD;
    private String STL, ETL, STA, ETA;


    private CheckBox S, M, Tu, W, TH;
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
    private EditText C_ClassRoomID, C_ClassRoomName, C_capacity, C_Beacons;
    private Button Add_NewClassRoom_BTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        InitializeView();
    }

    @Override
    public void InitializeView() {

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();
        this.progressDialog = new ProgressDialog(adminHome.this);
        this.welcomeTextView = findViewById(R.id.textView_NameOfAdmin);

        this.LogOutBTN = findViewById(R.id.button_Log_OUT);
        this.AddCourseBTN = findViewById(R.id.buttonOfAddCourse);
        this.AddTeacherBTN = findViewById(R.id.button2ForAddTeacher);
        this.AddStudentBTN = findViewById(R.id.button3ForAddStudent);
        this.AddClassRoomBTN = findViewById(R.id.buttonOfAddClassroom);
        this.ManageCoursesBTN = findViewById(R.id.button4MangeCourse);
        this.ManageTeacherBTN = findViewById(R.id.button5ManageTeacher);
        this.ManageStudentBTN = findViewById(R.id.button6ManageStudent);
        this.ManageCRtBTN = findViewById(R.id.button11M_C_R);


        //Call Design Function
        Design();
    }

    @Override
    public void Design() {


        String NAMEOfADD = "Name :" + userfile.getString(Constants.adminName, "");
        welcomeTextView.setText(NAMEOfADD);


        //Call HandleAction Function
        HandleAction();
    }

    @Override
    public void HandleAction() {

        //Manage classroom  Button Click Event Listener

        ManageCRtBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.classroom_list, null, false);
                setContentView(v);

                list_classroom = new ArrayList<>();
                listview_CR = v.findViewById(R.id.listTheClassRoom);

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                // Call Server To Get All Classrooms.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetAllClassroom, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // put all classrooms in the arrayList.
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                classroom CR = new classroom();
                                CR.setID(jsonObject.getString("room_ID"));
                                CR.setName(jsonObject.getString("room_Name"));
                                CR.setCap(jsonObject.getString("capacity"));
                                list_classroom.add(CR);
                            }


                            if (list_classroom.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is no any classroom.", Toast.LENGTH_LONG).show();

                            } else {

                                progressDialog.dismiss();

                                // Generate custom adapter with arrayList of classrooms and put it in List view.
                                MyClassRoomAdpt adapter = new MyClassRoomAdpt(getBaseContext(), list_classroom);
                                listview_CR.setAdapter(adapter);

                                // on click listener for items in the list view
                                listview_CR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        // go to Manage classroom page
                                        Intent intent = new Intent(getBaseContext(), Manage_classRoom.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        // put information of classroom in intent to send it to manage page.
                                        intent.putExtra("ID", list_classroom.get(i).getID());
                                        intent.putExtra("name", list_classroom.get(i).getName());
                                        intent.putExtra("Cap", list_classroom.get(i).getCap());
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
                });

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request

            }
        });

        //End Manage classroom  Button Click Event Listener


        // Manage Student Button Click Event Listener
        ManageStudentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.student_list, null, false);
                setContentView(v);

                // array list to store all students come from server.
                list_student = new ArrayList<>();
                listview_student = v.findViewById(R.id.listTheStudent);

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                // call server to get all students.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetStudents, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // put all students in arrayList
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                student ST = new student();

                                ST.setId(jsonObject.getString("S_StudentID"));
                                ST.setFname(jsonObject.getString("S_Fname"));
                                ST.setLname(jsonObject.getString("S_Lname"));
                                ST.setEmail(jsonObject.getString("S_Email"));
                                ST.setPass(jsonObject.getString("S_Password"));

                                list_student.add(ST);
                            }


                            if (list_student.size() == 0) {

                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There Is no Any Student", Toast.LENGTH_LONG).show();

                            } else {

                                progressDialog.dismiss();

                                // send array list of student to the custom adapter.
                                MystudentAdpt adapter = new MystudentAdpt(getBaseContext(), list_student);
                                listview_student.setAdapter(adapter);

                                // on click listener for items in the list view
                                listview_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Intent intent = new Intent(getBaseContext(), Manage_Student.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        intent.putExtra("S_ID", list_student.get(i).getId());
                                        intent.putExtra("Fname", list_student.get(i).getFname());
                                        intent.putExtra("Lname", list_student.get(i).getLname());
                                        intent.putExtra("Email", list_student.get(i).getEmail());
                                        intent.putExtra("Pass", list_student.get(i).getPass());
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

                });


                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request
            }
        });


        ManageTeacherBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.teacher_list, null, false);
                setContentView(v);


                list_teacher = new ArrayList<>();
                listview_Teacher = v.findViewById(R.id.listTheTeacher);


                progressDialog.setMessage("Please wait ...");
                progressDialog.show();


                // call server to get all teachers.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetTeachers, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // put all teacher in array list.
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                teacher TE = new teacher();

                                TE.setId(jsonObject.getString("Teacher_ID"));
                                TE.setFname(jsonObject.getString("Fname"));
                                TE.setLname(jsonObject.getString("Lname"));
                                TE.setEmail(jsonObject.getString("Email"));
                                TE.setPass(jsonObject.getString("Password"));
                                list_teacher.add(TE);
                            }


                            if (list_teacher.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There Is no Any Teacher", Toast.LENGTH_LONG).show();

                            } else {


                                MyTeacherAdpt adapter = new MyTeacherAdpt(getBaseContext(), list_teacher);
                                progressDialog.dismiss();
                                listview_Teacher.setAdapter(adapter);


                                // on click listener for items in the list view
                                listview_Teacher.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Intent intent = new Intent(getBaseContext(), Manage_Teacher.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        intent.putExtra("Teacher_ID", list_teacher.get(i).getId());
                                        intent.putExtra("Fname", list_teacher.get(i).getFname());
                                        intent.putExtra("Lname", list_teacher.get(i).getLname());
                                        intent.putExtra("Email", list_teacher.get(i).getEmail());
                                        intent.putExtra("Pass", list_teacher.get(i).getPass());
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

                });


                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request


            }
        });


        ManageCoursesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.course_list, null, false);
                setContentView(v);

                list_course = new ArrayList<>();
                listView_course = v.findViewById(R.id.listTheCourse);
                DeleteAllCourseBTN = v.findViewById(R.id.button3ForDeleteAllCourses);


                progressDialog.setMessage("Please wait ...");
                progressDialog.show();
                // call server to get all courses.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetCourses, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONArray jsonArray = new JSONArray(response);

                            // put all courses in array List.
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

                                // check first if Teacher and classroom are set for this course or not.
                                if (!jsonObject.getString("Teacher_ID").isEmpty()) {
                                    Course.setTeacher_ID(jsonObject.getString("Teacher_ID"));
                                }
                                if (!jsonObject.getString("room_ID").isEmpty()) {
                                    Course.setRoom_ID(jsonObject.getString("room_ID"));
                                }

                                list_course.add(Course);
                            }


                            if (list_course.size() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is no any course.", Toast.LENGTH_SHORT).show();
                            } else {


                                progressDialog.dismiss();
                                MyCoursAdpt adapter = new MyCoursAdpt(getBaseContext(), list_course);
                                listView_course.setAdapter(adapter);


                                // on click listener for items in the list view
                                listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Intent intent = new Intent(getBaseContext(), Manage_Course.class);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("course_ID", list_course.get(i).getCourse_id());
                                        intent.putExtra("name", list_course.get(i).getCourse_Name());
                                        intent.putExtra("TeacherID", list_course.get(i).getTeacher_ID());
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
                });

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request


                // Delete All Courses Button On Click Listener
                DeleteAllCourseBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        // call server to delete all courses.
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
                                        Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();
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
                        });

                        // Add The volly request to the Singleton Queue.
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);
                        // End of Volly http request


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


                            if (C_ClassRoomID.getText().toString().trim().isEmpty() || C_ClassRoomName.getText().toString().trim().isEmpty() || C_Beacons.getText().toString().trim().isEmpty() || C_capacity.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Please fill up all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No Internet ", Toast.LENGTH_LONG).show();
                            } else {

                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();

                                // get beacons ID
                                final String[] arr_Beacon = C_Beacons.getText().toString().split(",");

                                // parse the array string to json array
                                final JSONArray BeaconJSONArray = new JSONArray(Arrays.asList(arr_Beacon));

                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewClassRoom, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

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

                                            }
                                        } catch (JSONException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "There is problem , try again ", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "  There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        // HTTP request parameters

                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("RoomID", C_ClassRoomID.getText().toString());
                                        map.put("RoomName", C_ClassRoomName.getText().toString());
                                        map.put("capacity", C_capacity.getText().toString());
                                        map.put("BeaconIDs", BeaconJSONArray.toString());

                                        return map;
                                    }
                                };

                                // Add The volly request to the Singleton Queue.
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                // End of Volly http request

                            }


                        } catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "  There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
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
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "There is problem , try again ", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "  There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        // HTTP request parameters

                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("S_StudentID", S_StudentID_AD.getText().toString());
                                        map.put("S_Fname", S_Fname_AD.getText().toString());
                                        map.put("S_Lname", S_Lname_AD.getText().toString());
                                        map.put("S_Password", S_Password_AD.getText().toString());
                                        map.put("S_Email", S_email.getText().toString());

                                        return map;
                                    }
                                };

                                // Add The volly request to the Singleton Queue.
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                // End of Volly http request
                            }


                        } catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "There is problem , try again ", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No Internet ", Toast.LENGTH_LONG).show();
                            } else if (!IsEmailValid(T_email.getText().toString().trim())) {
                                Toast.makeText(getBaseContext(), "Please enter valid email.", Toast.LENGTH_LONG).show();
                            } else {

                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();

                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewTeacher, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {

                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");

                                            if (status.equals("yes")) {

                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), "Teacher Added", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                                startActivity(intent);

                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();

                                                T_TeacherID_AD.setText("");
                                                T_Fname_AD.setText("");
                                                T_Lname_AD.setText("");
                                                T_Password_AD.setText("");
                                                T_email.setText("");

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

                                        map.put("Teacher_ID", T_TeacherID_AD.getText().toString());
                                        map.put("Fname", T_Fname_AD.getText().toString());
                                        map.put("Lname", T_Lname_AD.getText().toString());
                                        map.put("Password", T_Password_AD.getText().toString());
                                        map.put("Email", T_email.getText().toString());
                                        return map;
                                    }
                                };

                                // Add The volly request to the Singleton Queue.
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                // End of Volly http request
                            }


                        } catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();
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
                ConfirmationDialog.setMessage("Do you want log out ?");
                ConfirmationDialog.setTitle("Confirm");
                ConfirmationDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                        } else {

                            // make the admin logged out in cach file.
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

                STL_AD = v.findViewById(R.id.TimePicker_STL_AD);
                ETL_AD = v.findViewById(R.id.TimePicker_ETL_AD);
                STA_AD = v.findViewById(R.id.TimePicker_STA_AD);
                ETA_AD = v.findViewById(R.id.TimePicker_ETA_AD);

                // Make the time picker as 24 hour view
                STL_AD.setIs24HourView(true);
                ETL_AD.setIs24HourView(true);
                STA_AD.setIs24HourView(true);
                ETA_AD.setIs24HourView(true);


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

                            // Get The date of first day in Semester.
                            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                            Date date = new Date(StartDayinWeek.getYear(), StartDayinWeek.getMonth(), StartDayinWeek.getDayOfMonth() - 1);
                            String dayOfWeek_ForCheek = simpledateformat.format(date);


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // get the time from time picker to add it.
                                // make the format hh:mm:ss a
                                STL = String.valueOf(STL_AD.getHour()) + ":" + String.valueOf(STL_AD.getMinute()) + ":00";
                                ETL = String.valueOf(ETL_AD.getHour()) + ":" + String.valueOf(ETL_AD.getMinute()) + ":00";
                                STA = String.valueOf(STA_AD.getHour()) + ":" + String.valueOf(STA_AD.getMinute()) + ":00";
                                ETA = String.valueOf(ETA_AD.getHour()) + ":" + String.valueOf(ETA_AD.getMinute()) + ":00";
                            }


                            if (NoOf_week.getText().toString().trim().isEmpty() || CourseID_AD.getText().toString().trim().isEmpty() || CourseName_AD.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                            } else if (Network.isConnected(getBaseContext()) == false) {
                                Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                            } else if (!dayOfWeek_ForCheek.equals("Sunday") && !dayOfWeek_ForCheek.equals("الأحد")) {
                                Toast.makeText(getBaseContext(), "The First day in week must be Sunday .", Toast.LENGTH_LONG).show();
                            } else {

                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();

                                // check if the admin did not set the teacher or classroom , so make it as null.

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

                                // call server to add new course.
                                StringRequest request = new StringRequest(Request.Method.POST, Constants.ADDnewCourse, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {

                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("state");


                                            if (status.equals("yes")) {

                                                // make the date format as yyyy-mm-dd
                                                String dt = "" + StartDayinWeek.getYear() + "-" + (StartDayinWeek.getMonth() + 1) + "-" + StartDayinWeek.getDayOfMonth();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                Calendar c = Calendar.getInstance();
                                                c.setTime(sdf.parse(dt));
                                                dt = sdf.format(c.getTime()); // dt now as yyy:mm:dd

                                                // to add all lecture of course
                                                // make for loop to make lecture for each week , as number of week the admin defined.
                                                for (int i = 0; i < Integer.parseInt(NoOf_week.getText().toString()); i++) {

                                                    // check what the days in week of the course.
                                                    if (S.isChecked()) {

                                                        Calendar cS = Calendar.getInstance();
                                                        cS.setTime(sdf.parse(dt));
                                                        String dtS = sdf.format(cS.getTime());  // dt now as yyy:mm:dd
                                                        // add new lecture for this course in this date.
                                                        AddLecture(CourseID_AD.getText().toString(), dtS);

                                                    }
                                                    if (M.isChecked()) {
                                                        Calendar cM = Calendar.getInstance();
                                                        cM.setTime(sdf.parse(dt));
                                                        cM.add(Calendar.DATE, 1);  // number of days to add (add 1)
                                                        String dtM = sdf.format(cM.getTime());  // dt now as yyy:mm:dd
                                                        // add new lecture for this course in this date.
                                                        AddLecture(CourseID_AD.getText().toString(), dtM);

                                                    }

                                                    if (Tu.isChecked()) {
                                                        Calendar cTu = Calendar.getInstance();
                                                        cTu.setTime(sdf.parse(dt));
                                                        cTu.add(Calendar.DATE, 2);  // number of days to add (add 2)
                                                        String dtTu = sdf.format(cTu.getTime());  // dt now as yyy:mm:dd
                                                        // add new lecture for this course in this date.
                                                        AddLecture(CourseID_AD.getText().toString(), dtTu);
                                                    }

                                                    if (W.isChecked()) {
                                                        Calendar cW = Calendar.getInstance();
                                                        cW.setTime(sdf.parse(dt));
                                                        cW.add(Calendar.DATE, 3);  // number of days to add (add 3)
                                                        String dtW = sdf.format(cW.getTime());  // dt now as yyy:mm:dd
                                                        // add new lecture for this course in this date.
                                                        AddLecture(CourseID_AD.getText().toString(), dtW);
                                                    }


                                                    if (TH.isChecked()) {
                                                        Calendar cTH = Calendar.getInstance();
                                                        cTH.setTime(sdf.parse(dt));
                                                        cTH.add(Calendar.DATE, 4);  // number of days to add (add 4)
                                                        String dtTH = sdf.format(cTH.getTime());  // dt now as yyy:mm:dd
                                                        // add new lecture for this course in this date.
                                                        AddLecture(CourseID_AD.getText().toString(), dtTH);
                                                    }

                                                    // add 7 days to maove to next week

                                                    c.setTime(sdf.parse(dt));
                                                    c.add(Calendar.DATE, 7);  // number of days to add (add 7)
                                                    dt = sdf.format(c.getTime());  // dt now as yyy:mm:dd
                                                }


                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), " Course Added.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                                startActivity(intent);


                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();
                                                CourseID_AD.setText("");
                                                CourseName_AD.setText("");
                                                TeacherID_AD.setText("");
                                                ClassRommID_AD.setText("");

                                            }
                                        } catch (JSONException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();
                                        } catch (ParseException e) {
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

                                        map.put("course_id", CourseID_AD.getText().toString());
                                        map.put("course_name", CourseName_AD.getText().toString());
                                        map.put("teacherID", TeacherID);
                                        map.put("classroomID", ClassroomID);
                                        map.put("STL", STL);
                                        map.put("ETL", ETL);
                                        map.put("STA", STA);
                                        map.put("ETA", ETA);

                                        return map;
                                    }
                                };

                                // Add The volly request to the Singleton Queue.
                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                // End of Volly http request
                            }


                        } catch (Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();

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

    public void AddLecture(final String CourseID, final String Date) {


        try {

            // call server to add new lecture for course.

            StringRequest request = new StringRequest(Request.Method.POST, Constants.AddLecture, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("state");

                        if (status.equals("yes")) {


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "There is problem please try again", Toast.LENGTH_SHORT).show();
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
                    map.put("date", Date);
                    map.put("CourseID", CourseID);
                    return map;
                }
            };

            // Add The volly request to the Singleton Queue.
            Singleton_Queue.getInstance(getBaseContext()).Add(request);
// End of Volly http request

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
