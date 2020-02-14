package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Camera> Cameras;
    private ProgressDialog progressDialog;
    private String get_cameras_url = URL.domain + "getCameraList";
    private String get_auth_user_url = URL.domain + "user";
    public String JSONResponse, user_type, zone;
    SharedPreferences object;
    SharedPreferences.Editor objectedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Retrieving User Data...");
        progressDialog.show();

        object = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String access_token = object.getString("access_token", null);

        // call API to obtain user data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_auth_user_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.setMessage("Response Received.");
                        JSONResponse = response;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject userJSONObject = jsonObject.getJSONObject("user");
                            String first_name = userJSONObject.getString("first_name");
                            String middle_name = userJSONObject.getString("middle_name");
                            String last_name = userJSONObject.getString("last_name");
                            String email = userJSONObject.getString("email");
                            int type_id = userJSONObject.getInt("type_id");
                            int zone_id = userJSONObject.getInt("zone_id");
                            if(type_id == 1)
                            {
                                user_type = "admin";
                            }
                            else if(type_id == 2)
                            {
                                user_type = "officer";
                            }
                            else
                            {
                                user_type = "ranger";
                            }

                            if (zone_id == 1)
                            {
                                zone = "North";
                            }
                            else if(zone_id == 2)
                            {
                                zone = "South";
                            }
                            else if(zone_id == 3)
                            {
                                zone = "East";
                            }
                            else
                            {
                                zone = "West";
                            }
                            objectedit = object.edit();
                            objectedit.putString("first_name", first_name);
                            objectedit.putString("middle_name", middle_name);
                            objectedit.putString("last_name", last_name);
                            objectedit.putString("email", email);
                            objectedit.putString("Type", user_type);
                            objectedit.putString("Zone", zone);
                            objectedit.commit();
                        } catch(JSONException e) {}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                //Log.d("Volley Error","Volley Error");
                //Toast.makeText(LoginActivity.this, "Volley error", Toast.LENGTH_SHORT).show();
                if(error instanceof ServerError)
                {Log.d("Error","Server error");
                    error.printStackTrace();}
                if(error instanceof NetworkError)
                {Log.d("Error","Network error");}
                if (error instanceof NoConnectionError)
                {Log.d("Error","No Connection error");}
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

        // call API to return a list of all cameras
        progressDialog.setMessage("Getting the list of available cameras...");
        StringRequest cameraStringRequest = new StringRequest(Request.Method.GET, get_cameras_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        progressDialog.setMessage("Response Received.");
                        JSONResponse = response;
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONArray cameraJsonArray = jsonArray.getJSONArray(0);
                            JSONArray zoneJsonArray = jsonArray.getJSONArray(1);
                            for(int i = 0; i < cameraJsonArray.length(); i++)
                            {
                                JSONObject jsonObject = cameraJsonArray.getJSONObject(i);
                                int camera_id = jsonObject.getInt("camera_id");
                                int latitude = jsonObject.getInt("latitude");
                                int longitude = jsonObject.getInt("longitude");

                                JSONArray intermediateZoneArray = zoneJsonArray.getJSONArray(i);
                                JSONObject zoneJsonObject = intermediateZoneArray.getJSONObject(0);
                                String zone = zoneJsonObject.getString("zone");
                                Cameras.add(new Camera(camera_id, latitude, longitude, zone));
                            }
                        } catch(JSONException e) {}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("Error", "Volley Error");
                Toast.makeText(MainActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        }) {

            // Send headers instead of parameters
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + access_token);
                return headers;
            }
        };
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

        recyclerView = (RecyclerView) findViewById(R.id.camera_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CameraListAdapter(MainActivity.this, Cameras);
        recyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }
}
