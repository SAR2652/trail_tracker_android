package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView camera_id_val, latitude_val, longitude_val, zone_val;
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
        int camera_id = obj.getInt("camera_id");
        int latitude = obj.getInt("latitude");
        int longitude = obj.getInt("longitude");
        String zone = obj.getString("zone");

        camera_id_val = (TextView) findViewById(R.id.camera_id_val);
        latitude_val = (TextView) findViewById(R.id.latitude_val);
        longitude_val = (TextView) findViewById(R.id.longitude_val);
        zone_val = (TextView) findViewById(R.id.zone_val);

        camera_id_val.setText(Integer.toString(camera_id));
        latitude_val.setText(Integer.toString(latitude));
        longitude_val.setText(Integer.toString(longitude));
        zone_val.setText((zone));

        VideoView videoView = (VideoView) findViewById(R.id.video_player);
        String video_url = URL.domain + "stream";
        Uri vidUri = Uri.parse(video_url);

        videoView.setVideoURI(vidUri, getHeaders());
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(videoView);
        videoView.setMediaController(vidControl);
        progressDialog.dismiss();
        videoView.start();
    }

    protected Map<String, String> getHeaders() {
        object = getSharedPreferences("user", Context.MODE_PRIVATE);
        String access_token = object.getString("access_token", null);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + access_token);
        return headers;
    }
}
