package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;

    private String get_auth_user_url = URL.domain + "user";
    private String get_cameras_url = URL.domain + "getCameraList";

    private String access_token;
    private List<Camera> Cameras;
    SharedPreferences object;
    SharedPreferences.Editor objecteditor;

    private static final String CHANNEL_ID = "trail_tracker_backend";
    private static final String CHANNEL_NAME = "Trail Tracker: Anti-poaching Intelligence";
    private static final String CHANNEL_DESC = "Trail Tracker: Anti-poaching Intelligence Notifications";

    public interface VolleyCallBack {
        void onSuccess();
    }

    public List<Camera> getCameras(final VolleyCallBack callback)
    {
        final List<Camera> LocalCameras = new ArrayList<>();
        StringRequest cameraStringRequest = new StringRequest(Request.Method.GET, get_cameras_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.setMessage("Response Received.");
                        Log.d("Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONArray cameraJsonArray = jsonArray.getJSONArray(0);
                            JSONArray zoneJsonArray = jsonArray.getJSONArray(1);
                            JSONObject cameraJsonObject = null;
                            for(int i = 0; i < cameraJsonArray.length(); i++)
                            {
                                cameraJsonObject = cameraJsonArray.getJSONObject(i);
                                int camera_id = cameraJsonObject.getInt("camera_id");
                                int longitude = cameraJsonObject.getInt("longitude");
                                int latitude = cameraJsonObject.getInt("latitude");

                                JSONArray intermediateZoneArray = zoneJsonArray.getJSONArray(i);
                                JSONObject zoneJsonObject = intermediateZoneArray.getJSONObject(0);
                                String zone = zoneJsonObject.getString("zone_name");
                                LocalCameras.add(new Camera(camera_id, latitude, longitude, zone));
                                callback.onSuccess();
                            }
                            Log.d("Camera", Integer.toString(Cameras.size()));
                        } catch(JSONException e) {e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("Error", "Volley Error");
                Toast.makeText(getApplicationContext(), "Volley Error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            // Send headers instead of parameters
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + access_token);
                return headers;
            }
        };
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(cameraStringRequest);
        return LocalCameras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Retrieving User Data...");
        progressDialog.show();

        /*
        Initialize a shared preferences object and retrieve the user's access token saved in it.
         */
        object = getSharedPreferences("user", Context.MODE_PRIVATE);
        access_token = object.getString("access_token", null);

        /*
        Due to slow JSON parsing of list of cameras and their details, callback is ensured
        */
        Cameras = getCameras(new VolleyCallBack() {
            @Override
            public void onSuccess()
            {
                recyclerView = (RecyclerView) findViewById(R.id.camera_recycler_view);

                /* use this setting to improve performance if you know that changes
                 in content do not change the layout size of the RecyclerView */
                recyclerView.setHasFixedSize(true);

                // use a linear layout manager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);

                // specify an adapter (see also next example)
                mAdapter = new CameraListAdapter(MainActivity.this, Cameras);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                /*
                Listen to notification channel provided Android OS is
                Android 8.0 Oreo or better.
                */
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(CHANNEL_DESC);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }

                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_layout_options, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         automatically handle clicks on the Home/Up button, so long
         as you specify a parent activity in AndroidManifest.xml. */

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            objecteditor.remove("access_token");
            objecteditor.commit();
            finish();
            startActivity(intent);
        }

        if (id == R.id.profile)
        {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        if(id == R.id.notify)
        {
            displayNotification();
        }

        if(id == R.id.reload)
        {
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Code to display notification
     */
    private void displayNotification()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.alert)
                .setContentTitle("Alert!!!")
                .setContentText("Camera 1, Longitude: 35, Latitude: 25, Zone: North")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, mBuilder.build());
    }
}
