package com.example.mm_kau.smartattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Teacher_HomePage extends AppCompatActivity implements Designable {
    private ListView listView_course  ;
    private ArrayList<course> list_course;
    private TextView Name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__home_page);
        InitializeView();
    }

    @Override
    public void InitializeView() {
        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView_course = findViewById(R.id.listCourseForStudent);
        list_course = new ArrayList<>();
        Name = findViewById(R.id.textViewForNAmeOfTEACHER);
        Name.setText(sharedPreferences.getString(Constants.T_Fname,"") +" "+ sharedPreferences.getString(Constants.T_Lname,""));


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





    }

    @Override
    public void Desing() {

    }

    @Override
    public void HandleAction() {





    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), LoginPage.class);
        startActivity(intent);

    }


}
