package edu.wmdd.gover;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);

        Button loginWithTokenButton = (Button) findViewById(R.id.loginButton);
        loginWithTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(username.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        username.setHint(getString(R.string.username_hint));
        password.setHint(getString(R.string.password_hint));
    }

    //    @Override
//    protected void onNewIntent(Intent intent) {
//        if (WebAuthProvider.resume(intent)) {
//            return;
//        }
//        super.onNewIntent(intent);
//    }
//
    private void login(String username, String password) {

        //final String username = etUname.getText().toString().trim();
        //final String password = etPass.getText().toString().trim();
//        final String username = "deniscalixto";
//        final String password = "Infopuc1";

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("username", username);
            postparams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:8000/api/token/",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();
//                        //parseData(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
//                        Log.d("Volley", error.getStackTrace().toString());
//                    }
//                }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("username",username);
//                params.put("password",password);
//
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.api_token_url), postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(LoginActivity.this, "Timeout", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(LoginActivity.this, "Server is unavailable", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(LoginActivity.this, "Internet problem", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                        Log.d("Volley", error.networkResponse.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/json");
                return headers;
            };
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsObjRequest);
    }

    public void parseData(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            String refresh = jsonObject.getString("refresh");
            String access = jsonObject.getString("access");
            //Toast.makeText(LoginActivity.this, refresh + "===" + access, Toast.LENGTH_SHORT).show();

            Auth.accessToken = access;
            Auth.refreshToken = refresh;

            Intent intent = new Intent(LoginActivity.this, PropertyActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}