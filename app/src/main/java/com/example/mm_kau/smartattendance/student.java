package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 11/02/18.
 */

public class student {

    private String id ;
    private String Fname;
    private String Lname;
    private String Pass ;
    private String Email;
    private String Mac_Address ;


    public void setId(String id) {
        this.id = id;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public void setPass(String pass) {
        Pass = pass;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setMac_Address(String mac_Address) {
        Mac_Address = mac_Address;
    }


    public String getId() {
        return id;
    }

    public String getFname() {
        return Fname;
    }

    public String getLname() {
        return Lname;
    }

    public String getPass() {
        return Pass;
    }

    public String getEmail() {
        return Email;
    }

    public String getMac_Address() {
        return Mac_Address;
    }
}
