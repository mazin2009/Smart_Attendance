package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 31/01/18.
 */

public class Constants {



    public static final String LOGIN_URL = "http://10.0.2.2/SA_Project/ChekLogin.php";
    public static final String ADDnewCourse = "http://10.0.2.2/SA_Project/AddNewCourse.php";
    public static final String ADDnewTeacher = "http://10.0.2.2/SA_Project/AddNewTeacher.php";
    public static final String ADDnewStudent = "http://10.0.2.2/SA_Project/AddNewStudent.php";
    public static final String ADDnewClassRoom = "http://10.0.2.2/SA_Project/AddNewClassroom.php";
    public static final String AddLecture = "http://10.0.2.2/SA_Project/addLectureOfcourse.php";
    public static final String GetCourses = "http://10.0.2.2/SA_Project/getAllCourses.php";
    public static final String updateCourse = "http://10.0.2.2/SA_Project/Update_Course.php";
    public static final String DeleteAllCourse = "http://10.0.2.2/SA_Project/DeleteAllCourses.php";
    public static final String DeleteCourseByID = "http://10.0.2.2/SA_Project/DelteCourseByID.php";
    public static final String GetTeachers = "http://10.0.2.2/SA_Project/getAllTeacher.php";
    public static final String updateTeacher = "http://10.0.2.2/SA_Project/Update_Teacher.php";
    public static final String DeleteTecherByID = "http://10.0.2.2/SA_Project/Delete_Teacher_by_ID.php";



// for all user (admin , teacher and student)
    public static final String UserFile = "Userfile";
    public static final String UserIsLoggedIn = "isLoggedIn";
    public static final String UserType = "UserType";


    // for admin
    public static final String adminID = "AdminID";
    public static final String adminName = "adminName";
    public static final String adminpass = "adminPass";

    //for Teacher
    public static final String TeacherID = "TeacherID";
    public static final String T_Fname = "T_Fname";
    public static final String T_Lname = "T_Lname";
    public static final String T_Pass = "T_Pass";
    public static final String T_email = "T_email";

    //for Teacher
    public static final String StudentID = "StudentID";
    public static final String s_Fname = "s_Fname";
    public static final String s_Lname = "s_Lname";
    public static final String s_Pass = "s_Pass";
    public static final String s_email = "s_email";





}
