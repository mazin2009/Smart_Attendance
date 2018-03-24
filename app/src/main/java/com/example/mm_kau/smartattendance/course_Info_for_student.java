package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class course_Info_for_student extends AppCompatActivity implements Designable{

    TextView C_id , C_name ,Teacher_name , C_CR , STL , ETL , STA, ETA , No_absent;
    Button ViewAttendanceInfo_BTN , MakeAttendance_BTN ;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ListView listview_attendance_info ;
    private ArrayList<String> list_attendance_info;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__info_for_student);

        InitializeView();
    }

    @Override
    public void InitializeView() {
        sharedPreferences = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.progressDialog = new ProgressDialog(course_Info_for_student.this);

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


    }

    @Override
    public void Desing() {

    }

    @Override
    public void HandleAction() {

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


}
