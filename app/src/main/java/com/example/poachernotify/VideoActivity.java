package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView camera_id_val, latitude_val, longitude_val, zone_val, status_val;
    SharedPreferences object;
    SharedPreferences.Editor objectedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        progressDialog = new ProgressDialog(VideoActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Retrieving User Data...");
        progressDialog.show();

        Intent extras = getIntent();
        Bundle obj=extras.getExtras();
        String camera_id = obj.getString("camera_id");
        String latitude = obj.getString("latitude");
        String longitude = obj.getString("longitude");
        String zone = obj.getString("zone");
        String status = obj.getString("status");

        camera_id_val = (TextView) findViewById(R.id.camera_id_val);
        latitude_val = (TextView) findViewById(R.id.latitude_val);
        longitude_val = (TextView) findViewById(R.id.longitude_val);
        zone_val = (TextView) findViewById(R.id.zone_val);
        status_val = (TextView) findViewById(R.id.status_value);

        camera_id_val.setText(camera_id);
        latitude_val.setText(latitude);
        longitude_val.setText(longitude);
        zone_val.setText(zone);
        status_val.setText(status);

//        camera_id_val.setText("1");
//        latitude_val.setText("50");
//        longitude_val.setText("33");
//        zone_val.setText("North");

        WebView videoView = (WebView) findViewById(R.id.video_player);
        if(status.equals("Inactive"))
        {
            videoView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Camera is Inactive!", Toast.LENGTH_LONG).show();
        }
        else
        {
            String video_url = URL.stream_domain;
            videoView.loadUrl(video_url);
        }
        progressDialog.dismiss();
    }

    protected Map<String, String> getHeaders() {
        object = getSharedPreferences("user", Context.MODE_PRIVATE);
        String access_token = object.getString("access_token", null);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + access_token);
        return headers;
    }
}
