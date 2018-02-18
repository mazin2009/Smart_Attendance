package com.example.mm_kau.smartattendance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Student_HomePage extends AppCompatActivity {

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__home_page);


        this.userfile=getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

        Toast.makeText(getBaseContext(),userfile.getString(Constants.s_Fname,"") +" " + userfile.getString(Constants.s_Lname," "),Toast.LENGTH_LONG ).show();

    }
}
