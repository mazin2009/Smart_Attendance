package com.example.mm_kau.smartattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class send_Excuse extends AppCompatActivity implements Designable {


    private Button Upload, Send;
    private Bitmap ImageBitmap;
    private String Encode_img;
    private ByteArrayOutputStream img_bytes;
    private ImageView IMGVIEW;
    private TextView Text;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__excuse);
        InitializeView();
    }

    @Override
    public void InitializeView() {
        this.progressDialog = new ProgressDialog(send_Excuse.this);
        Upload = findViewById(R.id.button4ofUploadImage);
        IMGVIEW = findViewById(R.id.IMGVIEW);
        Send = findViewById(R.id.button5_SendExcuse);
        Text = findViewById(R.id.editText4OfTextOfExcuse);

        Design();
    }

    @Override
    public void Design() {
        setTitle("Send New Excuse");
        HandleAction();
    }

    @Override
    public void HandleAction() {

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IMGVIEW.getDrawable() != null) {
                    // get the image from image view.
                    ImageBitmap = ((BitmapDrawable) IMGVIEW.getDrawable()).getBitmap();
                    // create  new bytes Stream
                    img_bytes = new ByteArrayOutputStream();
                    // Compress the image
                    ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, img_bytes);
                    //  Encode the image to Base64 format
                    Encode_img = Base64.encodeToString(img_bytes.toByteArray(), Base64.DEFAULT);

                    if (Network.isConnected(getBaseContext()) == false) {
                        Toast.makeText(getBaseContext(), "No Internet ", Toast.LENGTH_LONG).show();
                    } else {

                        progressDialog.setMessage("Please wait ...");
                        progressDialog.show();

                        StringRequest request = new StringRequest(Request.Method.POST, Constants.SendExcuse, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("state");
                                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();

                                    if (status.equals("yes")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "  Excuse Sent .", Toast.LENGTH_SHORT).show();
                                        // Send Notification
                                        SendAnnouncment(getIntent().getStringExtra("Teacher_ID"), getIntent().getStringExtra("st_name"));
                                        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "  There is an error , try agine .", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), "  There is an error , try agine .", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "  There is an error at connecting to server .", Toast.LENGTH_SHORT).show();
                            }

                        }) {

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                // HTTP request parameters
                                HashMap<String, String> map = new HashMap<>();
                                map.put("Teacher_ID", getIntent().getStringExtra("Teacher_ID"));
                                map.put("studentID", getIntent().getStringExtra("studentID"));
                                map.put("Course_id", getIntent().getStringExtra("Course_id"));
                                map.put("Date", getIntent().getStringExtra("Date"));
                                map.put("Text", Text.getText().toString());
                                map.put("State", "pending");
                                map.put("Image", Encode_img);
                                return map;
                            }
                        };

                        // Add The volly request to the Singleton Queue.
                        Singleton_Queue.getInstance(getBaseContext()).Add(request);
                        // End of Volly http request

                    }

                } else {
                    Toast.makeText(getBaseContext(), "Please Uplode The Image First", Toast.LENGTH_LONG).show();
                }

            }
        });


        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

    }


    public void OpenGallery() {
        // open the gallery
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get the image when the user select it.
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Uri uri = data.getData(); //get the uri of image
            IMGVIEW.setImageURI(uri); // set the image in the image view
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), Student_HomePage.class);
        startActivity(intent);
    }

    public void SendAnnouncment(final String Topic, final String StudentName) {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("body", "You can approve it or reject.");
                    jsonData.put("title", "New excuse from :" + StudentName);
                    json.put("notification", jsonData);
                    json.put("to", "/topics/" + Topic);

                    RequestBody body = RequestBody.create(JSON, json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization", "key=AAAAjjSURVI:APA91bFYLZHZHRXlCr7bh1VHZf3ZDbu1d8ioyfIuzCR40hJks4ILEYLE1UaNqqAj7ECKbToUnEA1FL1ysGRTnD6v87g4_9iQ_81iAwhcKmAgz49G6pY8_87IkdISX899j_bQ_q6JnfCB")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    // Toast.makeText(MainActivity.this, finalResponse,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(send_Excuse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();
    }

}
