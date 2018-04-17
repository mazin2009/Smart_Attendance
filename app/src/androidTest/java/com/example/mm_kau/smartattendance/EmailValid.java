package com.example.mm_kau.smartattendance;


import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Mez on 09/04/18.
 */
public class EmailValid extends ActivityInstrumentationTestCase2<adminHome> {

    private adminHome MyActivity;
    private Boolean chek;


    public EmailValid() {
        super(adminHome.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        MyActivity = getActivity();
        assertNotNull("activity is null", MyActivity);
    }


    @Test
    public void testIsValidEmail() throws Exception {

        chek =  MyActivity.IsEmailValid("example@gmail.com");
            assertTrue(chek);

    }
    @Test
    public void testIsNotVailEmail() throws Exception {
        chek =  MyActivity.IsEmailValid("XXXXXX.com");
        assertFalse(chek);
    }

}