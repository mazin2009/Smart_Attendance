package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class Manage_Student extends AppCompatActivity implements Designable {


    private EditText S_F_name , S_L_name , S_email , Pass , CRS_ID;
    private TextView S_ID;
    private Button Update , DLT , AddCRS , AddNewCourse4ST ;
    private ProgressDialog progressDialog;
    private String PASSWORD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__student);

        InitializeView();
    }

    @Override
    public void InitializeView() {



        this.progressDialog = new ProgressDialog(Manage_Student.this);

        this.Update = findViewById(R.id.buttonOfUpdateStudent);
        this.DLT =  findViewById(R.id.buttonOfDeleteStudent);
        this.AddCRS = findViewById(R.id.buttonOfAddNewCourseForStudent);

        this.S_ID = findViewById(R.id.TextForViewStudentID);
        this.S_ID.setText(getIntent().getStringExtra("S_ID"));

        this.S_F_name = findViewById(R.id.editTextFor_S_Fname_manage);
        this.S_F_name.setText(getIntent().getStringExtra("Fname"));

        this.S_L_name = findViewById(R.id.editTextFor_S_Lname_Manage);
        this.S_L_name.setText(getIntent().getStringExtra("Lname"));

        this.S_email = findViewById(R.id.editText_S_Email_Manage);
        this.S_email.setText(getIntent().getStringExtra("Email"));

        this.Pass = findViewById(R.id.editText_S_Pass);
        PASSWORD = getIntent().getStringExtra("Pass");

        Design();
    }

    @Override
    public void Design() {

        HandleAction();

    }

    @Override
    public void HandleAction() {


        DLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog.setMessage("Please wait ...");
                progressDialog.show();
                // call server to delete this student.

                StringRequest request = new StringRequest(Request.Method.POST, Constants.DeleteStByID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("state");


                            if (status.equals("yes")) {

                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), " student Deleted.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), adminHome.class);
                                startActivity(intent);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

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
                        map.put("st_id",S_ID.getText().toString());
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.

                Singleton_Queue.getInstance(getBaseContext()).Add(request);
                // End of Volly http request

            }
        });



        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (S_F_name.getText().toString().trim().isEmpty() || S_L_name.getText().toString().trim().isEmpty() || S_email.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else if (Network.isConnected(getBaseContext()) == false) {
                        Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                    } else {



                  // if admin did not add new password , here we set the old password as a new.
                        if (!Pass.getText().toString().trim().isEmpty()) {
                            PASSWORD = Pass.getText().toString();
                        }

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();


                        // call server to update student info.
                        StringRequest request = new StringRequest(Request.Method.POST, Constants.UpdateStudent, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");

                                    if (status.equals("yes")) {

                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), " Student Updated.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), adminHome.class);
                                        startActivity(intent);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {

                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

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
                                map.put("S_id", S_ID.getText().toString());
                                map.put("S_Fname",S_F_name.getText().toString());
                                map.put("S_Lname", S_L_name.getText().toString());
                                map.put("S_Email", S_email.getText().toString());
                                map.put("S_pass", PASSWORD);
                                return map;

                            }
                        };

                        // Add The volly request to the Singleton Queue.
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);
                        // End of Volly http request


                    }


                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();
                }




            }
        });




        AddCRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_course_for_student, null, false);
                setContentView(v);


                CRS_ID = v.findViewById(R.id.editTextForCRSid4ST);
                AddNewCourse4ST = v.findViewById(R.id.buttonAddCrs4ST);


                AddNewCourse4ST.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(CRS_ID.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getBaseContext(), "Please Enter The Course ID.", Toast.LENGTH_SHORT).show();

                        }else{

                            progressDialog.setMessage("Please wait ...");
                            progressDialog.show();

                            StringRequest request = new StringRequest(Request.Method.POST, Constants.ADD_CRS4ST, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("state");

                                        if (status.equals("yes")) {

                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "Course Added.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getBaseContext(), adminHome.class);
                                            startActivity(intent);

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(),"Course ID incorrect",Toast.LENGTH_SHORT).show();

                                        }
                                    } catch (JSONException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();

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
                                    map.put("CRS_ID",CRS_ID.getText().toString());
                                    map.put("ST_ID",S_ID.getText().toString());
                                    return map;
                                }
                            };


                            // Add The volly request to the Singleton Queue.
                            Singleton_Queue.getInstance(getBaseContext()).Add(request);
                            // End of Volly http request

                        }

                    }
                });
                // End Of final add course button.

            }
        });
        // End Of first add course button .

    } // End Of Handle action

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);

    }

}
