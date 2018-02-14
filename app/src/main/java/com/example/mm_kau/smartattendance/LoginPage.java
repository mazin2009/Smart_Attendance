package com.example.mm_kau.smartattendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends Activity implements Designable {



    private Button LoginBTN ;
    private ProgressDialog progressDialog;
    private EditText id, password;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    private admin admin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);



        InitializeView();
    }


    @Override
    public void InitializeView() {
        this.admin = new admin();

        this.userfile=getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

        this.LoginBTN =  findViewById(R.id.button_log_in);
        this.id =  findViewById(R.id.editTextOfusername);
        this.password =  findViewById(R.id.editTextOfpassword);
        this.progressDialog=new ProgressDialog(LoginPage.this);

        Desing();
    }

    @Override
    public void Desing() {

        // make (forget password textview) underline
        String FP="Forgot Password";
        SpannableString content = new SpannableString(FP);
        content.setSpan(new UnderlineSpan(), 0, FP.length(), 0);
        TextView  err = (TextView)findViewById(R.id.forget_password);
        err.setText(content);


        this.progressDialog.setCancelable(false);

        HandleAction();

    }

    @Override
    public void HandleAction() {




        if(userfile.getBoolean("isLoggedIn",false)==true){
            /*** Go To Home Page */
            Intent intent=new Intent(getBaseContext(),adminHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }


        this.LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                //check if edittext is empty or not

               if (id.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {

                    Toast.makeText(getBaseContext(),"Please fill in all fields",Toast.LENGTH_LONG ).show();
                } else if (Network.isConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(),"No connection with Internet",Toast.LENGTH_LONG ).show();

                } else {
                    progressDialog.setMessage("Please wait ...");
                    progressDialog.show();

                   StringRequest request=new StringRequest(Request.Method.POST,Constants.LOGIN_URL, new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {


                          //   Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                           try {


                               JSONObject jsonObject = new JSONObject(response);
                               String status=jsonObject.getString("state");

                              // Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();

                               if(status.equals("yes")){
                                   progressDialog.dismiss();

                                   JSONObject UserOB=jsonObject.getJSONObject("user");
                                   String Name = UserOB.getString("name");

                                   userfileEditer.putBoolean(Constants.UserIsLoggedIn,true);
                                   userfileEditer.putString(Constants.UserName,Name);
                                   userfileEditer.commit();

                                   Intent intent=new Intent(getBaseContext(),adminHome.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(intent);

                               }else{
                                   progressDialog.dismiss();
                                   Toast.makeText(getBaseContext(),"الرجاء التأكد من البريد الالكتروني او كلمة المرور .",Toast.LENGTH_LONG).show();
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }


                       }
                   }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           progressDialog.dismiss();
                           Toast.makeText(getBaseContext(),"There is an error at connecting to server .",Toast.LENGTH_SHORT).show();
                       }
                   }){
                       @Override
                       protected Map<String, String> getParams() throws AuthFailureError {
                           /*** Here you put the HTTP request parameters **/

                           HashMap<String,String> map=new HashMap<>();
                           map.put("id",id.getText().toString());
                           map.put("password",password.getText().toString());
                           return map;
                       }
                   };
                 Singleton_Queue.getInstance(getBaseContext()).Add(request);
               }
            }

        });

    }



    /*
    public String streamToString (InputStream IN){

        BufferedReader redear = new BufferedReader(new InputStreamReader(IN));
        String line;
        String Text="";
        try {

            while ((line=redear.readLine())!=null){
                Text += line;
            }

            IN.close();
        } catch (Exception ex){
            return Text;
        }
        return Text;
    }*/

}
