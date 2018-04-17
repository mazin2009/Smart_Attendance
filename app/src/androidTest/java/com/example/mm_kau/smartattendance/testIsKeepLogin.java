package com.example.mm_kau.smartattendance;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.*;

/**
 * Created by Mez on 09/04/18.
 */
public class testIsKeepLogin extends ActivityInstrumentationTestCase2<LoginPage> {

    private LoginPage MyActivity;
    private Button BTN;
    private CheckBox CH;
    private EditText USER ,PASS;
    private SharedPreferences userfile;


    public testIsKeepLogin() {
        super(LoginPage.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        MyActivity = getActivity();
        BTN = (Button) MyActivity.findViewById(R.id.button_log_in);
        CH = (CheckBox) MyActivity.findViewById(R.id.checkBoxKeepLogIn);
        USER = MyActivity.findViewById(R.id.editTextOfusername);
        PASS = MyActivity.findViewById(R.id.editTextOfpassword);
        userfile = MyActivity.getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        assertNotNull("mClickFunActivity is null", MyActivity);
        assertNotNull("mClickMeButton is null", BTN);
        assertNotNull("mInfoTextView is null", CH);

    }
    @Test
    public void testIsKeepLogin() throws Exception {

        MyActivity.runOnUiThread(new Runnable() {
            public void run() {
                USER.setText("1");
                PASS.setText("1");
                CH.setChecked(true);
            }
        });

        android.test.TouchUtils.clickView(this,BTN);
        assertEquals(true,userfile.getBoolean(Constants.UserIsLoggedIn,false));
        assertEquals("admin",userfile.getString(Constants.UserType,"admin"));
    }



}