package com.example.mm_kau.smartattendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginPage extends Activity implements Designable {


    private Button LoginBTN, SendPass;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private EditText id, password, email;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    private CheckBox RemaindMe;
    private TextView forgetPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        InitializeView();
    }


    @Override
    public void InitializeView() {

        //Intilizing The Cache File
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();


        this.RemaindMe = findViewById(R.id.checkBoxKeepLogIn);
        this.LoginBTN = findViewById(R.id.button_log_in);
        this.id = findViewById(R.id.editTextOfusername);
        this.password = findViewById(R.id.editTextOfpassword);
        this.forgetPass = findViewById(R.id.forget_password);


        this.progressDialog = new ProgressDialog(LoginPage.this);
        alertDialog = new AlertDialog.Builder(LoginPage.this).create();

        //Call Design Function
        Design();
    }

    @Override
    public void Design() {

        // make (forget password textview) underline
        String FP = "Forgot Password";
        SpannableString content = new SpannableString(FP);
        content.setSpan(new UnderlineSpan(), 0, FP.length(), 0);
        TextView err = findViewById(R.id.forget_password);
        err.setText(content);

        //Call HandleAction Function
        HandleAction();


    }

    @Override
    public void HandleAction() {


        // check in cach file , if user logged in or not.
        if (userfile.getBoolean(Constants.UserIsLoggedIn, false)) {

            // check what the type of user is logger in.
            String Type = userfile.getString(Constants.UserType, "");


            if (Type.equals("admin")) {

                Intent intent = new Intent(getBaseContext(), adminHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } else if (Type.equals("teacher")) {

                Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {

                Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

        }


        //Forgot password Button Click Event Listener

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.forgot_pass, null, false);
                setContentView(v);

                email = v.findViewById(R.id.editTextEmailToSendNewPass);
                SendPass = v.findViewById(R.id.button2SendNewPass);

                SendPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // title of alret dialog
                        alertDialog.setTitle("Attention");

                        if (email.getText().toString().equals("")) {

                            Toast.makeText(getBaseContext(), "Please fill in The Email", Toast.LENGTH_SHORT).show();

                        } else if (!IsEmailValid(email.getText().toString())) {

                            Toast.makeText(getBaseContext(), "Please Enter Valid Email", Toast.LENGTH_SHORT).show();

                        } else {

                            //Start progress Dialog
                            progressDialog.setMessage("Plase Wait ...");
                            progressDialog.show();

                            // call the server to get new password and send it to the email of user

                            StringRequest request = new StringRequest(Request.Method.POST, Constants.forgotPass, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {

                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("state");

                                        // check the state of response

                                        if (status.equals("yes")) {

                                            alertDialog.setMessage("The new password has been sent to your email");
                                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(getBaseContext(), LoginPage.class);
                                                    startActivity(intent);
                                                }
                                            });

                                            alertDialog.show();

                                        } else {

                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "The Email does not exist", Toast.LENGTH_SHORT).show();
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
                                    map.put("email", email.getText().toString());
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

        // End Forgot password Button Click Event Listener


        //Login Button Click Event Listener

        this.LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if Edit text is empty or not
                if (id.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else if (Network.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog.setMessage("Please wait ...");
                    progressDialog.show();
                    //Call Server to check the login Information.
                    StringRequest request = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("state");
                                if (status.equals("yes")) {
                                    progressDialog.dismiss();
                                    String Type = jsonObject.getString("type");
                                    if (Type.equals("admin")) {
                                        JSONObject UserOB = jsonObject.getJSONObject("user");
                                        //get Information of admin
                                        String ADminID = UserOB.getString("admin_id");
                                        String AdminName = UserOB.getString("name");
                                        String password = UserOB.getString("password");
                                        // save information in cach file (SharedPreferences)
                                        userfileEditer.putString(Constants.adminID, ADminID);
                                        userfileEditer.putString(Constants.adminName, AdminName);
                                        userfileEditer.putString(Constants.adminpass, password);
                                        userfileEditer.putString(Constants.UserType, "admin");
                                        if (RemaindMe.isChecked()) {
                                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, true);
                                        }
                                        userfileEditer.commit();
                                        // go To Admin Page.
                                        Intent intent = new Intent(getBaseContext(), adminHome.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else if (Type.equals("teacher")) {
                                        JSONObject UserOB = jsonObject.getJSONObject("user");
                                        //get Information of Teacher
                                        String T_ID = UserOB.getString("Teacher_ID");
                                        String T_Fname = UserOB.getString("Fname");
                                        String T_Lname = UserOB.getString("Lname");
                                        String T_Pass = UserOB.getString("Password");
                                        String T_Email = UserOB.getString("Email");
                                        // save information in cach file (SharedPreferences)
                                        userfileEditer.putString(Constants.TeacherID, T_ID);
                                        userfileEditer.putString(Constants.T_Fname, T_Fname);
                                        userfileEditer.putString(Constants.T_Lname, T_Lname);
                                        userfileEditer.putString(Constants.T_Pass, T_Pass);
                                        userfileEditer.putString(Constants.T_email, T_Email);
                                        if (RemaindMe.isChecked()) {
                                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, true);
                                        }
                                        userfileEditer.putString(Constants.UserType, "teacher");
                                        userfileEditer.commit();
                                        // go To Teacher Page.
                                        Intent intent = new Intent(getBaseContext(), Teacher_HomePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {

                                        JSONObject UserOB = jsonObject.getJSONObject("user");
                                        //get Information of Student
                                        String s_ID = UserOB.getString("S_StudentID");
                                        String s_Fname = UserOB.getString("S_Fname");
                                        String s_Lname = UserOB.getString("S_Lname");
                                        String s_Pass = UserOB.getString("S_Password");
                                        String s_Email = UserOB.getString("S_Email");
                                        // save information in cach file (SharedPreferences)
                                        userfileEditer.putString(Constants.StudentID, s_ID);
                                        userfileEditer.putString(Constants.s_Fname, s_Fname);
                                        userfileEditer.putString(Constants.s_Lname, s_Lname);
                                        userfileEditer.putString(Constants.s_Pass, s_Pass);
                                        userfileEditer.putString(Constants.s_email, s_Email);
                                        if (RemaindMe.isChecked()) {
                                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, true);
                                        }
                                        userfileEditer.putString(Constants.UserType, "student");
                                        userfileEditer.commit();
                                        // go To Student Page.
                                        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), "Please make sure you enter your ID or password correctly", Toast.LENGTH_LONG).show();
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
                            map.put("id", id.getText().toString());
                            map.put("password", password.getText().toString());
                            return map;
                        }
                    };
                    // Add The volly request to the Singleton Queue.
                    Singleton_Queue.getInstance(getBaseContext()).Add(request);

                    // End of Volly http request
                }
            }

        });
        // End Login Button Click Event Listener
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
