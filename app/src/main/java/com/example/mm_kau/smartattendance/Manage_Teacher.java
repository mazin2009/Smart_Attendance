package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Manage_Teacher extends AppCompatActivity implements Designable {


    private EditText T_F_name, T_L_name, T_email, Pass;
    private TextView T_ID;
    private Button Update, DLT;
    private ProgressDialog progressDialog;
    private String PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__teacher);

        InitializeView();
    }

    @Override
    public void InitializeView() {

        this.progressDialog = new ProgressDialog(Manage_Teacher.this);

        this.Update = findViewById(R.id.buttonOfUpdateTeacher);
        this.DLT = findViewById(R.id.buttonOfDeleteTeacher);

        this.T_ID = findViewById(R.id.TextForViewTeacherID);
        this.T_ID.setText(getIntent().getStringExtra("Teacher_ID"));

        this.T_F_name = findViewById(R.id.editTextFor_T_Fname_manage);
        this.T_F_name.setText(getIntent().getStringExtra("Fname"));

        this.T_L_name = findViewById(R.id.editTextFor_T_Lname_Manage);
        this.T_L_name.setText(getIntent().getStringExtra("Lname"));

        this.T_email = findViewById(R.id.editText_T_Email_Manage);
        this.T_email.setText(getIntent().getStringExtra("Email"));

        this.Pass = findViewById(R.id.editText_T_Pass);
        PASSWORD = getIntent().getStringExtra("Pass");


        Design();
    }

    @Override
    public void Design() {

        HandleAction();
    }

    @Override
    public void HandleAction() {

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (T_F_name.getText().toString().trim().isEmpty() || T_L_name.getText().toString().trim().isEmpty() || T_email.getText().toString().trim().isEmpty()) {
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


                        // call server to update teacher info.
                        StringRequest request = new StringRequest(Request.Method.POST, Constants.updateTeacher, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");

                                    if (status.equals("yes")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), " Teacher Updated.", Toast.LENGTH_LONG).show();
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
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                // HTTP request parameters
                                HashMap<String, String> map = new HashMap<>();

                                map.put("T_id", T_ID.getText().toString());
                                map.put("T_Fname", T_F_name.getText().toString());
                                map.put("T_Lname", T_L_name.getText().toString());
                                map.put("T_Email", T_email.getText().toString());
                                map.put("t_pass", PASSWORD);
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

        // call server to delete this student.
        DLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                StringRequest request = new StringRequest(Request.Method.POST, Constants.DeleteTecherByID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("state");
                            if (status.equals("yes")) {

                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), " Teacher Deleted.", Toast.LENGTH_LONG).show();
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
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        // HTTP request parameters
                        HashMap<String, String> map = new HashMap<>();
                        map.put("TE_id", T_ID.getText().toString());
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

        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);

    }

}
