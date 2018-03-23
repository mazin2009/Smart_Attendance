package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 23/03/18.
 */

public class Message {

    String M_ID ;
    String TeacherID;
    String TeacheName;
    String CourseID;
    String CourseNAme;
    String Date;
    String Title ;
    String Body;

    public void setM_ID(String m_ID) {
        M_ID = m_ID;
    }

    public void setTeacherID(String teacherID) {
        TeacherID = teacherID;
    }

    public void setTeacheName(String teacheName) {
        TeacheName = teacheName;
    }

    public void setCourseID(String courseID) {
        CourseID = courseID;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setBody(String body) {
        Body = body;
    }

    public void setCourseNAme(String courseNAme) {
        CourseNAme = courseNAme;
    }

    public String getCourseNAme() {
        return CourseNAme;
    }

    public String getM_ID() {
        return M_ID;
    }

    public String getTeacherID() {
        return TeacherID;
    }

    public String getTeacheName() {
        return TeacheName;
    }

    public String getCourseID() {
        return CourseID;
    }

    public String getDate() {
        return Date;
    }

    public String getTitle() {
        return Title;
    }

    public String getBody() {
        return Body;
    }

}
