package com.example.mm_kau.smartattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Student_HomePage extends AppCompatActivity implements Designable {

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    ImageView MsgBTN ;
    private ArrayList<Message> List_MSG;
    ListView ListViewMSG;
    Button LogOUT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__home_page);


        this.userfile=getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

InitializeView();

    }

    @Override
    public void InitializeView() {

        MsgBTN = findViewById(R.id.imageViewMessage);
        LogOUT = findViewById(R.id.buttonLogOUT_ST);


        Desing();

    }

    @Override
    public void Desing() {


        HandleAction();
    }

    @Override
    public void HandleAction() {



        LogOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder ConfirmationDialog = new AlertDialog.Builder(Student_HomePage.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("Do you want to logout ?");
                ConfirmationDialog.setTitle("sure");
                ConfirmationDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "no connection", Toast.LENGTH_LONG).show();
                        } else {

                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, false);
                            userfileEditer.commit();
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


        MsgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.msg_list, null, false);
                setContentView(v);


                List_MSG = new ArrayList<>();
                ListViewMSG = v.findViewById(R.id.listMsg);


                StringRequest request = new StringRequest(Request.Method.POST, Constants.GetMSG, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Message MsgInfo = new Message();

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject MsgObject = jsonObject.getJSONObject("Msg");
                                String TeacherName = jsonObject.getString("TeacherName");

                                MsgInfo.setM_ID(MsgObject.getString("ID"));
                                MsgInfo.setTeacherID(MsgObject.getString("Teacher_ID"));
                                MsgInfo.setTeacheName(TeacherName);
                                MsgInfo.setCourseID(MsgObject.getString("CourseID"));
                                MsgInfo.setTitle(MsgObject.getString("Title"));
                                MsgInfo.setBody(MsgObject.getString("body"));
                                MsgInfo.setDate(MsgObject.getString("Date"));
                                MsgInfo.setCourseNAme(jsonObject.getString("CourseName"));

                                List_MSG.add(MsgInfo);
                            }



                            if (List_MSG.size() == 0) {

                            Toast.makeText(getBaseContext(), "There isf no Message", Toast.LENGTH_LONG).show();

                            } else {
                                Collections.reverse(List_MSG);
                                Msg_Adpt adapter = new Msg_Adpt(getBaseContext(), List_MSG);
                                ListViewMSG.setAdapter(adapter);

                                ListViewMSG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                                        View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.body_msg, null, false);
                                        setContentView(v);

                                        TextView Body = v.findViewById(R.id.editTextForBodymsg);
                                        Body.setText(List_MSG.get(i).getBody());


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
                        map.put("ID",userfile.getString(Constants.StudentID,""));
                        return map;
                    }
                };
                Singleton_Queue.getInstance(getBaseContext()).Add(request);





            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
        startActivity(intent);

    }



}
