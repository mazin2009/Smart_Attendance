package com.example.mm_kau.smartattendance;

import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.ArrayList;

/**
 * Created by Mez on 31/01/18.
 */

public class Constants {


    public static final String IP = "192.168.1.9";
    public static final String LOGIN_URL = "http://"+IP+"/SA_Project/ChekLogin.php";
    public static final String ADDnewCourse = "http://"+IP+"/SA_Project/AddNewCourse.php";
    public static final String ADDnewTeacher = "http://"+IP+"/SA_Project/AddNewTeacher.php";
    public static final String ADDnewStudent = "http://"+IP+"/SA_Project/AddNewStudent.php";
    public static final String ADDnewClassRoom = "http://"+IP+"/SA_Project/AddNewClassroom.php";
    public static final String AddLecture = "http://"+IP+"/SA_Project/addLectureOfcourse.php";
    public static final String GetCourses = "http://"+IP+"/SA_Project/getAllCourses.php";
    public static final String updateCourse = "http://"+IP+"/SA_Project/Update_Course.php";
    public static final String DeleteAllCourse = "http://"+IP+"/SA_Project/DeleteAllCourses.php";
    public static final String DeleteCourseByID = "http://"+IP+"/SA_Project/DelteCourseByID.php";
    public static final String GetTeachers = "http://"+IP+"/SA_Project/getAllTeacher.php";
    public static final String updateTeacher = "http://"+IP+"/SA_Project/Update_Teacher.php";
    public static final String DeleteTecherByID = "http://"+IP+"/SA_Project/Delete_Teacher_by_ID.php";
    public static final String GetStudents = "http://"+IP+"/SA_Project/getAllStudent.php";
    public static final String ADD_CRS4ST = "http://"+IP+"/SA_Project/add_CRS_4_ST.php";
    public static final String UpdateStudent = "http://"+IP+"/SA_Project/update_student.php";
    public static final String DeleteStByID = "http://"+IP+"/SA_Project/Delete_ST_ByID.php";
    public static final String GetAllClassroom = "http://"+IP+"/SA_Project/getAllCR.php";
    public static final String GetBeaconForCR = "http://"+IP+"/SA_Project/GetBeaconForCR.php";
    public static final String UpdateCR = "http://"+IP+"/SA_Project/UpdateClassRoom.php";
    public static final String getCourseByID_forTeacher = "http://"+IP+"/SA_Project/GetCoursesByID_forTeacher.php";
    public static final String Update_TimeOdAttendance = "http://"+IP+"/SA_Project/update_TimeOfAttendance.php";
    public static final String Get_numberOfST = "http://"+IP+"/SA_Project/Get_NumberOfStudent.php";
    public static final String Get_attend_INFO = "http://"+IP+"/SA_Project/GetStudentInfo_forCourse_inTeacherInterface.php";
    public static final String Get_Lecture_for_course = "http://"+IP+"/SA_Project/GetLecture_ByCourseID.php";
    public static final String CancelLecByCourseID = "http://"+IP+"/SA_Project/CancelTheLecture.php";
    public static final String GetAttendInfoForEachLec = "http://"+IP+"/SA_Project/GetAttendanceInfo_foreacheLecture.php";
    public static final String ChangeAttendaceForSTudent = "http://"+IP+"/SA_Project/ChangeStateOfStudent.php";
    public static final String AddNewAnnouncment = "http://"+IP+"/SA_Project/AddNewMessage.php";
    public static final String GetMSG = "http://"+IP+"/SA_Project/GetMessages.php";
    public static final String GetCoursesForStudent = "http://"+IP+"/SA_Project/GetCoursesByID_forStu.php";
    public static final String GetNumberOfabsent = "http://"+IP+"/SA_Project/GetNumberOfAbsent_bySTid.php";
    public static final String GetBeacons = "http://"+IP+"/SA_Project/GetBeacons_byClassroomID.php";
    public static final String MakeAttendance = "http://"+IP+"/SA_Project/MakeAttendance.php";
    public static final String GetLecture_forStudent = "http://"+IP+"/SA_Project/GetAttendanceInfo_ForStudent.php";
    public static final String SendExcuse = "http://"+IP+"/SA_Project/InsertExcuse.php";
    public static final String getListOfExcuse = "http://"+IP+"/SA_Project/GetListOfExcuse.php";
    public static final String approveExcue = "http://"+IP+"/SA_Project/approveExcuse.php";
    public static final String rejectExcuse = "http://"+IP+"/SA_Project/RejectExcuse.php";
    public static final String getRandomST = "http://"+IP+"/SA_Project/GetRandom.php";
    public static final String forgotPass = "http://"+IP+"/SA_Project/forgotPass.php";
    public static final String changePass = "http://"+IP+"/SA_Project/changePass.php";


    // cach file for all user (admin , teacher and student)
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

    //for Student
    public static final String StudentID = "StudentID";
    public static final String s_Fname = "s_Fname";
    public static final String s_Lname = "s_Lname";
    public static final String s_Pass = "s_Pass";
    public static final String s_email = "s_email";


    // store the list of course of student to use it when student logout.
    // to unsubscribe From Topic
    public static  ArrayList<course> list_course_of_Student = new ArrayList<>();


}
