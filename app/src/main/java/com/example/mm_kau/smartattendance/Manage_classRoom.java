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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Manage_classRoom extends AppCompatActivity implements Designable {

    private TextView ID ;
    private EditText Name , Cap , Becons;
    private String BeaconList = " ";
    private ProgressDialog progressDialog;
    private Button update  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class_room);
        InitializeView();
    }

    @Override
    public void InitializeView() {

        this.progressDialog = new ProgressDialog(Manage_classRoom.this);

        this.ID = findViewById(R.id.editTextForClassRoomID_InManage);
        this.ID.setText( getIntent().getStringExtra("ID"));

        this.Name = findViewById(R.id.editTextForCR_Name_InManage);
        this.Name.setText( getIntent().getStringExtra("name"));

        this.Cap = findViewById(R.id.editTextForCapcty_CR_onManage);
        this.Cap.setText( getIntent().getStringExtra("Cap"));

        this.Becons = findViewById(R.id.editTextForBeacons_InManage);
        this.update = findViewById(R.id.UpdateClassRoomBTN);

        progressDialog.setMessage("Please wait ...");
        progressDialog.show();



        // cal server to get all beacon id belong to this classroom
        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetBeaconForCR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                        JSONArray jsonArray = new JSONArray(response);

                        // save all beacon id in String to set it in beacon Text View
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BeaconList += jsonObject.getString("BeaconID")+"\n";
                        }


                    progressDialog.dismiss();
                        if (jsonArray.length()==0) {
                            Becons.setText("There Is No Beacons");
                        }else {
                            Becons.setText(BeaconList);
                        }
                    // call Design Function after response of get all beacon arrive.
                        Design();


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
                map.put("RoomID", ID.getText().toString());
                return map;
            }
        };

        // Add The volly request to the Singleton Queue.
        Singleton_Queue.getInstance(getBaseContext()).Add(request);
        // End of Volly http request

    }

    @Override
    public void Design() {




        HandleAction();
    }

    @Override
    public void HandleAction() {


        //Update classroom Button Click Event Listener
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    if (Name.getText().toString().trim().isEmpty() || Cap.getText().toString().trim().isEmpty() || Becons.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else if (Network.isConnected(getBaseContext()) == false) {
                        Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                    } else {

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();


                        // get the beacons ID from Edit text and split it by line.
                        String Beacons[] = Becons.getText().toString().split("\\r?\\n");

                        // parse the array to json array To send it to server.
                        final JSONArray BeaconJSONArray = new JSONArray(Arrays.asList(Beacons));


                        // call server to update classroom

                        StringRequest request = new StringRequest(Request.Method.POST, Constants.UpdateCR, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    if (!response.isEmpty()) {

                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("state");

                                        if (status.equals("yes")) {

                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(), " Class Room Updated.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getBaseContext(), adminHome.class);
                                            startActivity(intent);

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();
                                        }
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
                                map.put("RoomID", ID.getText().toString());
                                map.put("RoomName", Name.getText().toString());
                                map.put("capacity", Cap.getText().toString());
                                map.put("BeaconIDs", BeaconJSONArray.toString());
                                return map;
                            }
                        };

                        // Add The volly request to the Singleton Queue.
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);

                        // End of Volly http request
                    }


                } catch (Exception e) {
                    Toast.makeText(getBaseContext(),"There is problem please try again",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //End Update classroom Button Click Event Listener



    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);
    }

}
