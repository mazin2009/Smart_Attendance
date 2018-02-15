package com.example.mm_kau.smartattendance;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mez on 11/02/18.
 */

public class course {

private String Course_id;
private String Course_Name;
private int NumberOfStudent;
private String Teacher_ID;
private String Room_ID;
private ArrayList<student> Students;
private ArrayList<lecture> Lecture;
private String STL;
private String ETL;
private String STA;
private String ETA;

    public void setCourse_id(String course_id) {
        Course_id = course_id;
    }

    public void setCourse_Name(String course_Name) {
        Course_Name = course_Name;
    }

    public void setNumberOfStudent(int numberOfStudent) {
        NumberOfStudent = numberOfStudent;
    }

    public void setTeacher_ID(String teacher_ID) {
        Teacher_ID = teacher_ID;
    }

    public void setRoom_ID(String room_ID) {
        Room_ID = room_ID;
    }

    public void setStudents(ArrayList<student> students) {
        Students = students;
    }

    public void setLecture(ArrayList<lecture> lecture) {
        Lecture = lecture;
    }

    public void setSTL(String STL) {
        this.STL = STL;
    }

    public void setETL(String ETL) {
        this.ETL = ETL;
    }

    public void setSTA(String STA) {
        this.STA = STA;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }


    public String getCourse_id() {
        return Course_id;
    }

    public String getCourse_Name() {
        return Course_Name;
    }

    public int getNumberOfStudent() {
        return NumberOfStudent;
    }

    public String getTeacher_ID() {
        return Teacher_ID;
    }

    public String getRoom_ID() {
        return Room_ID;
    }

    public ArrayList<student> getStudents() {
        return Students;
    }

    public ArrayList<lecture> getLecture() {
        return Lecture;
    }

    public String getSTL() {
        return STL;
    }

    public String getETL() {
        return ETL;
    }

    public String getSTA() {
        return STA;
    }

    public String getETA() {
        return ETA;
    }
}
