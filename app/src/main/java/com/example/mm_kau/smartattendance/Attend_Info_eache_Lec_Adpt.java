package com.example.mm_kau.smartattendance;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
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

/**
 * Created by Mez on 21/03/18.
 */

public class Attend_Info_eache_Lec_Adpt extends ArrayAdapter {



    private Context context;
    private ArrayList<String> students;
    Switch MySwitch ;
    String Stat;


    public Attend_Info_eache_Lec_Adpt(Context context, ArrayList<String> students) {

        super(context, R.layout.coustomlist_atten_info_lec,students);
        this.context = context;
        this.students = students;
    }

    public String getItem(int position){
        return students.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coustomlist_atten_info_lec, parent, false);

        final String Info [] = students.get(position).split(",");


        TextView ID = (TextView) view.findViewById(R.id.TextView_IDofSTU_inlec);
        ID.setText(Info[0]);

        TextView name = (TextView) view.findViewById(R.id.TextView_NameofSTU_iclec);
        name.setText(Info[1]);

          MySwitch =  view.findViewById(R.id.switch1ForAttendance);


if (Info[2].equals("present")) {
    MySwitch.setChecked(true);
}else {

    MySwitch.setChecked(false);

}

        MySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                 Stat = "present";

                }else {
                    Stat = "absent";

                }


                StringRequest request=new StringRequest(Request.Method.POST,Constants.ChangeAttendaceForSTudent, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String status =jsonObject.getString("state");

                            if(status.equals("yes")){
                                Toast.makeText(context,"The state of Student has changed To "+Stat,Toast.LENGTH_LONG).show();

                            }else{

                                Toast.makeText(context,"There is problem try agine.",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        /*** Here you put the HTTP request parameters **/

                        HashMap<String,String> map=new HashMap<>();
                        map.put("Date",Info[3]);
                        map.put("CRs_id",Info[4]);
                        map.put("Stu_id",Info[0]);
                        map.put("Stat",Stat);
                        return map;
                    }
                };

                Singleton_Queue.getInstance(context).Add(request);


            }
        });



        return view;
    }





}
