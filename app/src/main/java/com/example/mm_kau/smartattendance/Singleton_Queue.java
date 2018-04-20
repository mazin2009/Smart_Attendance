package com.example.mm_kau.smartattendance;

/**
 * Created by Mez on 29/01/18.
 */



import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Singleton_Queue {

    private RequestQueue queue;

    private static Singleton_Queue singleton;

    private Context context;

    private Singleton_Queue(Context context) {
        this.context = context;
    }

    public static synchronized Singleton_Queue getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton_Queue(context);
        }
        return singleton;

    }

    public RequestQueue getRequestQueu() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public void Add(StringRequest request) {

        getRequestQueu().add(request);

    }
}
