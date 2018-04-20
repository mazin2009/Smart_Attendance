package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import static android.graphics.Color.*;

/**
 * Created by Mez on 20/03/18.
 */

public class Adapter_LectureList extends ArrayAdapter {

    private Context context;
    private ArrayList<lecture> LECture;
    String STUTUS = ""; // state of lecture


    public Adapter_LectureList(Context context, ArrayList<lecture> LECture) {

        super(context, R.layout.custom_list_for_lecture, R.id.buttonForActionTheLecture, LECture);
        this.context = context;
        this.LECture = LECture;

    }

    public lecture getItem(int position) {
        return LECture.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_for_lecture, parent, false);

        TextView DATE = view.findViewById(R.id.TextView_DateOfLecture);
        DATE.setText(LECture.get(position).getDate());

        TextView State = view.findViewById(R.id.TextView_StateOfLec);
        State.setText(LECture.get(position).getState());

        Button ActionBTN = view.findViewById(R.id.buttonForActionTheLecture);

        // if the lecture in upcoming state make the action button as cancel button
        if (LECture.get(position).getState().equals("upcoming")) {
            ActionBTN.setText("Cancel");
            // if the lecture in canceld state make the action button as uncancel button
        } else if (LECture.get(position).getState().equals("Canceled")) {
            ActionBTN.setText("uncancel");
            ActionBTN.setBackgroundColor(ActionBTN.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            // if the lecture in completed state make the action button as Cancel button
            ActionBTN.setText("Cancel");
        }


        ActionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (LECture.get(position).getState().equals("upcoming")) {
                    STUTUS = "Canceled";
                } else if (LECture.get(position).getState().equals("Canceled")) {
                    STUTUS = "upcoming";
                } else {
                    STUTUS = "Canceled";
                }


                StringRequest request = new StringRequest(Request.Method.POST, Constants.CancelLecByCourseID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("state");

                            if (status.equals("yes")) {

                                Toast.makeText(context, "The state of lecture has changed ", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context.getApplicationContext(), Teacher_HomePage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.getApplicationContext().startActivity(intent);

                            } else {

                                Toast.makeText(context, "There is problem try agine.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                            Toast.makeText(context, "There is problem try agine.", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, "There is an error at connecting to server .", Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        // HTTP request parameters

                        HashMap<String, String> map = new HashMap<>();
                        map.put("ID", LECture.get(position).getCourseID());
                        map.put("Date", LECture.get(position).getDate());
                        map.put("state", STUTUS);
                        return map;
                    }
                };

                // Add The volly request to the Singleton Queue.
                Singleton_Queue.getInstance(context).Add(request);
                // End of Volly http request

            }
        });

        return view;
    }


}
