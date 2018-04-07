package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 29/01/18.
 */

public interface Designable {

    /** InitializeView function  will help the activity to initialize the view on the activity */
    void InitializeView();
    /** Design function will just take care of designing the activity */
    void Design();
    /** HandleAction function will just take care of actions will be on the activity */
    void HandleAction();

}