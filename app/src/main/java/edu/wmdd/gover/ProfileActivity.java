package edu.wmdd.gover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.Menu;

public class ProfileActivity extends AppCompatActivity {

    EditText user_name;
    EditText user_email;
    EditText user_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setAnimation();

        setContentView(R.layout.activity_profile);

        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);

        Button btLogOut = (Button) findViewById(R.id.btLogOut);
        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.refreshToken = null;
                Auth.accessToken = null;

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this);
                startActivity(intent, options.toBundle());

//                startActivity(intent);
            }
        });

        //Start Bottom Nav

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if( item.getItemId() == R.id.btProperties){
                    Intent intent = new Intent(ProfileActivity.this, PropertyActivity.class);
                    startActivity(intent);
                }
                else if( item.getItemId() == R.id.btInspections){
                    Intent intent = new Intent(ProfileActivity.this, InspectionListActivity.class);
                    startActivity(intent);
                }
                else if( item.getItemId() == R.id.btReports){
                    Intent intent = new Intent(ProfileActivity.this, ReportListActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
//End Bottom Nav

        getData();
    }



    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.api_user_url) + "get_by_username/?username=" + Auth.username,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    user_name.setText(response.getString("first_name") + " " + response.getString("last_name"));
                    user_email.setText(response.getString("email"));
                    user_password.setText(response.getInt("password"));
//                    property.setImage_url(response.getString("thumbnail"));
                } catch (JSONException e) {
                    Log.d("Volley", e.getStackTrace().toString());
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        progressDialog.dismiss();
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
    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(400);
            slide.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }
}
