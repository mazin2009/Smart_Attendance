package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 28/03/18.
 */

public class excuse {


    private String Ex_ID;
    private String Teacher_ID;
    private String Student_ID;
    private String Student_name;
    private String Course_ID;
    private String Course_name;
    private String Date;
    private String Text;
    private String State;
    private String Image;

    public String getStudent_name() {
        return Student_name;
    }

    public void setStudent_name(String student_name) {

        Student_name = student_name;
    }

    public String getCourse_name() {
        return Course_name;
    }

    public void setCourse_name(String course_name) {

        Course_name = course_name;
    }

    public void setEx_ID(String ex_ID) {
        Ex_ID = ex_ID;
    }

    public void setTeacher_ID(String teacher_ID) {
        Teacher_ID = teacher_ID;
    }

    public void setStudent_ID(String student_ID) {
        Student_ID = student_ID;
    }

    public void setCourse_ID(String course_ID) {
        Course_ID = course_ID;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setText(String text) {
        Text = text;
    }

    public void setState(String state) {
        State = state;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getEx_ID() {
        return Ex_ID;
    }

    public String getTeacher_ID() {
        return Teacher_ID;
    }

    public String getStudent_ID() {
        return Student_ID;
    }

    public String getCourse_ID() {
        return Course_ID;
    }

    public String getDate() {
        return Date;
    }

    public String getText() {
        return Text;
    }

    public String getState() {
        return State;
    }

    public String getImage() {
        return Image;
    }
}
