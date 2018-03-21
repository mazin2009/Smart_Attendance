package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 11/02/18.
 */

public class lecture {

private String Date;
private String State;
private String CourseID;


    public void setDate(String date) {
        Date = date;
    }

    public void setState(String state) {
        State = state;
    }

    public void setCourseID(String courseID) {
        CourseID = courseID;
    }


    public String getDate() {
        return Date;
    }

    public String getState() {
        return State;
    }

    public String getCourseID() {
        return CourseID;
    }

}
