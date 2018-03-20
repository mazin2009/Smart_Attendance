package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Manage_classRoom extends AppCompatActivity implements Designable {

    TextView ID ;
    EditText Name , Cap , Becons;
    private String BeaconList = "";
    private ProgressDialog progressDialog;
Button update , DLT ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class_room);
        InitializeView();
    }

    @Override
    public void InitializeView() {

        this.progressDialog = new ProgressDialog(Manage_classRoom.this);
        ID = findViewById(R.id.editTextForClassRoomID_InManage);
        ID.setText( getIntent().getStringExtra("ID"));
        Name = findViewById(R.id.editTextForCR_Name_InManage);
        Name.setText( getIntent().getStringExtra("name"));
        Cap = findViewById(R.id.editTextForCapcty_CR_onManage);
        Cap.setText( getIntent().getStringExtra("Cap"));
        Becons = findViewById(R.id.editTextForBeacons_InManage);

        update = findViewById(R.id.UpdateClassRoomBTN);
        DLT = findViewById(R.id.DeleteClassRoomBTN);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.GetBeaconForCR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BeaconList += jsonObject.getString("BeaconID")+"\n";
                        }

                        Becons.setText(BeaconList);



                Desing();


                } catch (JSONException e) {

                    Becons.setText("There Is No Becon");

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
                map.put("RoomID", ID.getText().toString());
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);


    }

    @Override
    public void Desing() {

        HandleAction();
    }

    @Override
    public void HandleAction() {


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                try {

                    if (Name.getText().toString().trim().isEmpty() || Cap.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else if (Network.isConnected(getBaseContext()) == false) {
                        Toast.makeText(getBaseContext(), "No connection with Internet", Toast.LENGTH_LONG).show();
                    } else {

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        String Beacons[] = Becons.getText().toString().split("\\r?\\n");
                        final JSONArray BeaconJSONArray = new JSONArray(Arrays.asList(Beacons));

                        StringRequest request = new StringRequest(Request.Method.POST, Constants.UpdateCR, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

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
                                            Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                                            Toast.makeText(getBaseContext(), " Tehre is problem , try agine ", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                } catch (JSONException e) {



                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                /*** Here you put the HTTP request parameters **/

                                HashMap<String, String> map = new HashMap<>();

                                map.put("RoomID", ID.getText().toString());
                                map.put("RoomName", Name.getText().toString());
                                map.put("capacity", Cap.getText().toString());
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getBaseContext(), adminHome.class);
        startActivity(intent);

    }

}
