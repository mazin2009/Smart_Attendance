package com.example.mm_kau.smartattendance;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Test;


/**
 * Created by Mez on 10/04/18.
 */

public class MoveToAppropriateActivity extends ActivityInstrumentationTestCase2<LoginPage> {

    private LoginPage MyActivity;
    private Button BTN;
    private EditText USER, PASS;
    private static int TIMEOUT_IN_MS = 10000;
    private Instrumentation mInstrumentation;

    public MoveToAppropriateActivity() {
        super(LoginPage.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);
        mInstrumentation = getInstrumentation();
        MyActivity = getActivity();
        BTN = MyActivity.findViewById(R.id.button_log_in);
        USER = MyActivity.findViewById(R.id.editTextOfusername);
        PASS = MyActivity.findViewById(R.id.editTextOfpassword);
    }

    @Test
    public void testIsMoveToAdminActivity() throws Exception {

        Instrumentation.ActivityMonitor ActivityMonitor = mInstrumentation.addMonitor(adminHome.class.getName(), null, false);
        getInstrumentation().waitForIdleSync();

        MyActivity.runOnUiThread(new Runnable() {
            public void run() {
                USER.setText("1");
                PASS.setText("1");
            }
        });
        android.test.TouchUtils.clickView(this, BTN);
        adminHome receiverActivity = (adminHome) ActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNotNull("admin Home is Null", receiverActivity);
        assertEquals(" Monter Not called", 1, ActivityMonitor.getHits());
        assertEquals(" Monter Not called0000", adminHome.class, receiverActivity.getClass());
        getInstrumentation().removeMonitor(ActivityMonitor);
    }


    @Test
    public void testIsMoveToTeacherActivity() throws Exception {

        Instrumentation.ActivityMonitor ActivityMonitor = mInstrumentation.addMonitor(Teacher_HomePage.class.getName(), null, false);
        getInstrumentation().waitForIdleSync();

        MyActivity.runOnUiThread(new Runnable() {
            public void run() {
                USER.setText("1475963");
                PASS.setText("1");
            }
        });

        android.test.TouchUtils.clickView(this, BTN);

        Teacher_HomePage receiverActivity = (Teacher_HomePage) ActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("Teacher Home is Null", receiverActivity);
        assertEquals(" Monter Not called", 1, ActivityMonitor.getHits());
        assertEquals(" Monter Not called0000", Teacher_HomePage.class, receiverActivity.getClass());
        getInstrumentation().removeMonitor(ActivityMonitor);
    }


    @Test
    public void testIsMoveToStudentActivity() throws Exception {

        Instrumentation.ActivityMonitor ActivityMonitor = mInstrumentation.addMonitor(Student_HomePage.class.getName(), null, false);
        getInstrumentation().waitForIdleSync();

        MyActivity.runOnUiThread(new Runnable() {
            public void run() {
                USER.setText("1407702");
                PASS.setText("1");
            }
        });

        android.test.TouchUtils.clickView(this, BTN);
        Student_HomePage receiverActivity = (Student_HomePage) ActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNotNull("Student Home is Null", receiverActivity);
        assertEquals(" ActivityMonitor is not called", 1, ActivityMonitor.getHits());
        assertEquals(" Activity is wrong type ", Student_HomePage.class, receiverActivity.getClass());
        getInstrumentation().removeMonitor(ActivityMonitor);

    }


}
