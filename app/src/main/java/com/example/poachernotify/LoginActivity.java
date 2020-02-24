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

public class LoginActivity extends AppCompatActivity {

    EditText userNameEditText, passwordEditText;
    Button signInButton;
    ProgressDialog progressDialog;
    String email, password, access_token;
    String TAG = "";
    SharedPreferences object;
    SharedPreferences.Editor objectedit;

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
                if((userNameEditText.getText().toString().equals("")) || (passwordEditText.getText().toString().equals("")))
                {
                    Toast.makeText(LoginActivity.this, "Password or Username field is missing", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    email = userNameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    getAccessToken(email, password);
                }
            }
        });
    }

    public String url = URL.domain + "login";
    public String JSONResponse;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public boolean IS_RESULT_RECEIVED=false;

    public void getAccessToken(String email, final String password)
    {
        final String id = email;
        progressDialog.setMessage("Getting your data...");
        Log.d("url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("TAG", response);
                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        progressDialog.setMessage("Response Received");
                        Log.d("Email:", "This is email " + id);
                        JSONResponse = response;
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            access_token = jsonObject.getString("token");
                            editor.putString("access_token", access_token);
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
            protected Map<String,String> getParams() throws AuthFailureError
            {
                Map<String,String> params=new HashMap<String, String>();
                params.put("email", id);
                params.put("password", password);
                return params;
            }
        };
        int socketTimeout=60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }

    public void updateUI(String req)
    {
        if(req.equals("in"))
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
            progressDialog.dismiss();
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,"Oops!! Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

}
