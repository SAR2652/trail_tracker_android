package com.example.poachernotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

abstract class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userNameEditText, passwordEditText;
    Button signInButton;
    public ProgressDialog progressDialog;
    public String email, password;
    public String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Signing you in.....");
                progressDialog.show();
                if((userNameEditText.getText().toString() == "") || (passwordEditText.getText().toString() == ""))
                {
                    Toast.makeText(LoginActivity.this, "Password or Username field is missing", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    email = userNameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    getInfo(email, password);
                }
            }
        });
    }

    public String get_url = URL.domain;
    public String JSONResponse;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public boolean IS_RESULT_RECEIVED=false;

    public void getInfo(String email, final String password)
    {
        final String id = email;
        progressDialog.setMessage("Getting your data...");
        Log.d("url", get_url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONResponse = response;
                        Log.d("TAG", response);
                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        progressDialog.setMessage("Response Received");
                        Log.d("Email:", "This is email" + id);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int id = jsonObject.getInt("id");
                            String first_name = jsonObject.getString("first_name");
                            String last_name = jsonObject.getString("last_name");
                            editor.putInt("id", id);
                            editor.putString("first_name", first_name);
                            editor.putString("last_name", last_name);
                            editor.commit();
                        }
                        catch (JSONException e){}
                        updateUI("in");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        }) {

            @Override
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<String, String>();
                String query = String.format("mutation{login(data:{email:\"%s\" password:\"%s\"}){token user{name role email}}}",id,password);

                params.put("email", id);
                params.put("password", password);
                return params;
            }
        };
        int socketTimeout=60000;
        RetryPolicy policy=new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }

    public void updateUI(String req)
    {
        if(req.equals("in"))
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            progressDialog.dismiss();
            finish();
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,"Oops!! Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }


}
