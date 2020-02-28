package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private String JSONResponse, access_token, full_name;
    SharedPreferences object;
    SharedPreferences.Editor editor;
    TextView name, email, type, zone;

    private String get_auth_user_url = URL.domain + "user";


    // API call to obtain user data
    public void getUserData(String access_token)
    {
        final String token = access_token;
        StringRequest userStringRequest = new StringRequest(Request.Method.GET, get_auth_user_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                            String user_type, zone;
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
                            editor = object.edit();
                            editor.putString("first_name", first_name);
                            editor.putString("middle_name", middle_name);
                            editor.putString("last_name", last_name);
                            editor.putString("email", email);
                            editor.putString("type", user_type);
                            editor.putString("zone", zone);
                            editor.commit();
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
                {
                    Log.d("Error","Server error");
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(userStringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        object = getSharedPreferences("user", Context.MODE_PRIVATE);
        access_token = object.getString("access_token", null);

        // retrieve and save user data
        getUserData(access_token);

        name = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.email);
        type = (TextView) findViewById(R.id.type);
        zone = (TextView) findViewById(R.id.zone);

        String first_name = object.getString("first_name", null);
        String middle_name = object.getString("middle_name", null);
        String last_name = object.getString("last_name", null);

        if (middle_name.equals(null))
        {
            full_name = first_name + " " + last_name;
        }
        else
        {
            full_name = first_name + " " + middle_name + " " + last_name;
        }

        name.setText(full_name);
        email.setText(object.getString("email", null));
        type.setText(object.getString("user_type", null));
        zone.setText(object.getString("zone", null));
    }
}
