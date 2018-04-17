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

    private ListView listView_course, ListViewExcuse;
    private ArrayList<course> list_course;
    private TextView Name;
    private Bitmap decodedByte;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditer;
    private Button LogOUT;
    private ImageView requestExcuse_BTN, SettingBTN;
    private ArrayList<excuse> List_Excuse;
    private EditText newPass1, newPass2, prevPass;
    private ProgressDialog progressDialog;
    private android.app.AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__home_page);

        InitializeView();
    }

    @Override
    public void InitializeView() {


        this.progressDialog = new ProgressDialog(Teacher_HomePage.this);
        this.sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.sharedPreferencesEditer = sharedPreferences.edit();
        alertDialog = new android.app.AlertDialog.Builder(Teacher_HomePage.this).create();

        this.SettingBTN = findViewById(R.id.imageViewSetting);
        this.requestExcuse_BTN = findViewById(R.id.imageViewNewRequesttExcuse);
        this.LogOUT = findViewById(R.id.buttonLogOUT_Teacher);

        this.listView_course = findViewById(R.id.listCourseForStudent);
        this.list_course = new ArrayList<>();
        this.Name = findViewById(R.id.textViewForNAmeOfTEACHER);

        // subscribe the teacher to topic (The topic is teacher id)
        FirebaseMessaging.getInstance().subscribeToTopic(sharedPreferences.getString(Constants.TeacherID, " "));


        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        // cal server to get all courses belong to teahcer.

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
                        if (!jsonObject.getString("room_ID").isEmpty()) {
                            Course.setRoom_ID(jsonObject.getString("room_ID"));
                        }

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

                        MyCoursAdpt adapter = new MyCoursAdpt(getBaseContext(), list_course);
                        listView_course.setAdapter(adapter);

                        // on click listener for items in the list view
                        listView_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent = new Intent(getBaseContext(), course_info_for_teacher.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("course_ID", list_course.get(i).getCourse_id());
                                intent.putExtra("name", list_course.get(i).getCourse_Name());
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
                HashMap<String, String> map = new HashMap<String, String>();

                // HTTP request parameters
                map.put("ID", sharedPreferences.getString(Constants.TeacherID, " "));
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

        setTitle("Teacher Home Page");
        this.Name.setText(sharedPreferences.getString(Constants.T_Fname, "") + " " + sharedPreferences.getString(Constants.T_Lname, ""));
        HandleAction();
    }

    @Override
    public void HandleAction() {

        SettingBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.setting, null, false);
                setContentView(v);
                // set title of action bar.
                setTitle("Setting");

                Button ChangePass = v.findViewById(R.id.button2ChangePass);

                ChangePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.change_pass, null, false);
                        setContentView(v);

                        // set title of action bar.
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
                                    Toast.makeText(getBaseContext(), newPass1.getText().toString()+"Two passwords are not the same"+newPass2.getText().toString(), Toast.LENGTH_LONG).show();
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
                                                    // log out the teacher
                                                    sharedPreferencesEditer.putBoolean(Constants.UserIsLoggedIn, false);
                                                    sharedPreferencesEditer.commit();
                                                    // unsubscribe From Topic (Topic is the teacher ID)
                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPreferences.getString(Constants.TeacherID, ""));
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
                                            map.put("ID", sharedPreferences.getString(Constants.TeacherID, " "));
                                            map.put("prevPass", prevPass.getText().toString());
                                            map.put("password", newPass1.getText().toString());
                                            map.put("UserType", sharedPreferences.getString(Constants.UserType, " "));
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
        }); // End OF Setting button


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
                            Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                        } else {
                            // unsubscribe From Topic (Topic is the teacher ID)
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPreferences.getString(Constants.TeacherID, ""));
                            // logout teacher in cach file.
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


        requestExcuse_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.excuse_list, null, false);
                setContentView(v);
                // set title of action bar.
                setTitle("List Of Excuse ");


                List_Excuse = new ArrayList<>();
                ListViewExcuse = v.findViewById(R.id.listExcuse);


                progressDialog.setMessage("Please wait ...");
                progressDialog.show();
                // call server to get all Request excuse.
                StringRequest request = new StringRequest(Request.Method.POST, Constants.getListOfExcuse, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                excuse excuse_Info = new excuse();

                                // get The object of one excuse.
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // get the excuse object , student name and course name of excuse.
                                JSONObject ExcuseObject = jsonObject.getJSONObject("Excuse");
                                String StudentName = jsonObject.getString("Student_name");
                                String CourseName = jsonObject.getString("CourseName");

                                // put the excuse information in the array list.
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
                                alertDialog.setMessage("There is no any excuse.");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                        startActivity(intent);
                                    }
                                });

                                alertDialog.show();

                            } else {

                                Collections.reverse(List_Excuse); // reverse the excuse to put the new one first.


                                progressDialog.dismiss();
                                Excuse_apdt adapter = new Excuse_apdt(getBaseContext(), List_Excuse);
                                ListViewExcuse.setAdapter(adapter);

                                // on click listener for items in the list view
                                ListViewExcuse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                                        final String Ex_ID = List_Excuse.get(i).getEx_ID();
                                        final String Date = List_Excuse.get(i).getDate();
                                        final String Course_ID = List_Excuse.get(i).getCourse_ID();
                                        final String StudentID = List_Excuse.get(i).getStudent_ID();

                                        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_excuse, null, false);
                                        setContentView(v);
                                        // set title of action bar.
                                        setTitle("Excuse from :  " + List_Excuse.get(i).getStudent_name());

                                        ImageView IMG_View = v.findViewById(R.id.imageViewForShowExcueInTeacher);
                                        // decode the image of excuse.
                                        byte[] decodedString = Base64.decode(List_Excuse.get(i).getImage(), Base64.DEFAULT);
                                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        IMG_View.setImageBitmap(decodedByte);
                                        // set the message of excuse.
                                        TextView TEXT = v.findViewById(R.id.textView15OfMessageOfExcuse);
                                        TEXT.setText(List_Excuse.get(i).getText());

                                        // make dialog if teacher want to show full image.
                                        Button FullImage = v.findViewById(R.id.buttonOfFullImage);
                                        FullImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(Teacher_HomePage.this);
                                                View VIEW = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_image_dialog, null, false);
                                                ImageView imageView = VIEW.findViewById(R.id.image_dialog);
                                                imageView.setImageBitmap(decodedByte);
                                                dialog.setView(VIEW);
                                                dialog.setCancelable(true);
                                                dialog.show();

                                            }
                                        });


                                        Button approve = v.findViewById(R.id.button2Aprrove);
                                        Button reject = v.findViewById(R.id.button3Reject);

                                        // IF TEACHER APRROVE THE EXCUSE.
                                        approve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                progressDialog.setMessage("Please wait ...");
                                                progressDialog.show();

                                                //CALL SERVER TO APPROVE THE STUDENT EXCUSE.
                                                StringRequest request = new StringRequest(Request.Method.POST, Constants.approveExcue, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {

                                                        try {

                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String status = jsonObject.getString("state");

                                                            if (status.equals("yes")) {

                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), " Excuse Approved", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                                                startActivity(intent);

                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), " There is problem , try agine ", Toast.LENGTH_LONG).show();
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
                                                        HashMap<String, String> map = new HashMap<>();
                                                        map.put("Excuse_id", Ex_ID);
                                                        map.put("Date", Date);
                                                        map.put("CourseID", Course_ID);
                                                        map.put("Student_ID", StudentID);
                                                        return map;
                                                    }
                                                };
                                                // Add The volly request to the Singleton Queue.
                                                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                                                // End of Volly http request

                                            }
                                        });


                                        // IF TEACHER REJECT THE EXCUSE.
                                        reject.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                progressDialog.setMessage("Please wait ...");
                                                progressDialog.show();

                                                StringRequest request = new StringRequest(Request.Method.POST, Constants.rejectExcuse, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {

                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String status = jsonObject.getString("state");

                                                            if (status.equals("yes")) {

                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), " Excuse Rejected", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                                                startActivity(intent);

                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getBaseContext(), " There is problem , try agine ", Toast.LENGTH_LONG).show();
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
                                                        HashMap<String, String> map = new HashMap<>();

                                                        map.put("Excuse_id", Ex_ID);
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
                        } catch (JSONException e) {

                            progressDialog.dismiss();
                            alertDialog.setMessage("There is no any excuse.");
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                    startActivity(intent);
                                }
                            });

                            alertDialog.show();

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
                        map.put("ID", sharedPreferences.getString(Constants.TeacherID, ""));
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
        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
        startActivity(intent);

    }

}
