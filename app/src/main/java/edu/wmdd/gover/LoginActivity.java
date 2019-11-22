package edu.wmdd.gover;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    String username;
//    Button btnGoi;
    ImageView imageView;
    Bitmap bitmap;

    TextView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText txtUsername = findViewById(R.id.user_name);
        final EditText txtPassword = findViewById(R.id.password);

//        btnGoi = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);


        //Fingerprint

        status = findViewById(R.id.tv1);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            status.setText("Permission not granted");
        }else{
            FingerprintManagerCompat fmc= FingerprintManagerCompat.from(this);

            if (!fmc.isHardwareDetected()){
                status.setText("No fingerprint sensor hardware found");

            }else if(!fmc.hasEnrolledFingerprints()){
                status.setText("No fingerprint currently enrolled");
                Button loginWithTokenButton = (Button) findViewById(R.id.loginButton);
                loginWithTokenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        username = txtUsername.getText().toString().trim();
                        login(username, txtPassword.getText().toString().trim());
                    }
                });

            }else{
                status.setText("fingerprint is ready");

                //set up authentication dialog

                BiometricPrompt biometricPrompt = new BiometricPrompt
                        .Builder(this)
                        .setTitle("Biometric Authetication")
                        .setSubtitle("Authenticate to continue")
                        .setDescription("Fingerprinting in biometric")
                        .setNegativeButton("Cancel", this.getMainExecutor(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        status.setText("Fingerprint cancelled");
                                        Button loginWithTokenButton = (Button) findViewById(R.id.loginButton);
                                        loginWithTokenButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                username = txtUsername.getText().toString().trim();
                                                login(username, txtPassword.getText().toString().trim());
                                            }
                                        });
                                    }
                                })
                        .build();
                //Authenticate with callback functions
                biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallback());
            }
        }


        //===================







//        txtUsername.setHint(getString(R.string.username_hint));
//        txtPassword.setHint(getString(R.string.password_hint));

//        btnGoi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imageUploadTest();
//            }
//        });

//        txtUsername.setHint(getString(R.string.username_hint));
//        txtPassword.setHint(getString(R.string.password_hint));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode== RESULT_OK && data !=null){
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

//    private void imageUploadTest() {
//        // loading or check internet connection or something...
//        // ... then
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, getString(R.string.api_property_url), new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                String resultResponse = new String(response.data);
//                Log.d("Volley", resultResponse);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String status = response.getString("status");
//                        String message = response.getString("message");
//
//                        Log.e("Error Status", status);
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message+" Please login again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message+ " Check your inputs";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message+" Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("address","Image Test");
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
//                params.put("thumbnail", new DataPart("property_thumbnail.png", AppHelper.getFileDataFromDrawable(getBaseContext(), imageView.getDrawable()), "image/png"));
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(multipartRequest);
//    }

    private void login(String username, String password) {

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("username", username);
            postparams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.api_token_url), postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", response.toString());
                        parseData(response);
                        fecthUserData();
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
                        Log.d("Volley", error.toString());
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
            Auth.username= username;

            Intent intent = new Intent(LoginActivity.this, PropertyActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fecthUserData() {JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
            getString(R.string.api_user_url) + "get_by_username/?username=" + Auth.username,
            null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                Auth.userId = response.getInt("id");
                Auth.user_name = response.getString("first_name") + " " + response.getString("last_name");
                Auth.user_email = response.getString("email");
            } catch (JSONException e) {
                Log.d("Volley", e.getStackTrace().toString());
                e.printStackTrace();
            }
        }
    },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                }
            }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String,String> headers = new HashMap<String,String>();
            headers.put("Authorization", "Bearer "+ Auth.accessToken);
            return headers;
        };
    };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);

    }


    //biometric method
    private BiometricPrompt.AuthenticationCallback
    getAuthenticationCallback(){
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                status.setText(status.getText() + "Fingerprint error" + errString);
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {

                super.onAuthenticationSucceeded(result);
                status.setText(status.getText() + "Fingerprint succeed");
                login("chan", "Gover!123");

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };
    }
    private CancellationSignal cancellationSignal;
    private CancellationSignal getCancellationSignal(){
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                status.setText(status.getText() + "Fingerprint cancelled by signal");
            }
        });
        return cancellationSignal;
    }

}