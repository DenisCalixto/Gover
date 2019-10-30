package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private String url = "http://100.20.115.205/properties.json";
    private String url = "http://159.65.44.135/api/jobs";
    //private String url = "http://159.65.44.135/api/jobs.json";
    //private String url = "https://gist.githubusercontent.com/aws1994/f583d54e5af8e56173492d3f60dd5ebf/raw/c7796ba51d5a0d37fc756cf0fd14e54434c547bc/anime.json";
    //private String url = "http://151.101.52.133/aws1994/f583d54e5af8e56173492d3f60dd5ebf/raw/c7796ba51d5a0d37fc756cf0fd14e54434c547bc/anime.json";

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Job> jobList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = findViewById(R.id.main_list);

        jobList = new ArrayList<>();
        adapter = new JobAdapter(getApplicationContext(),jobList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

//        Toast.makeText(MainActivity.this, Auth.accessToken + "===" + Auth.refreshToken, Toast.LENGTH_SHORT).show();

        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("Volley", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                Job job = new Job();
                                job.setSummary(jsonObject.getString("summary"));
                                job.setId(jsonObject.getInt("id"));
                                job.setImage_url(jsonObject.getString("image"));

                                jobList.add(job);
                            } catch (JSONException e) {
                                Log.d("Test", "Calling FAB");
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(MainActivity.this, "Timeout", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MainActivity.this, "Server is unavailable", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MainActivity.this, "Internet problem", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("Volley", error.toString());
                        Log.e("Volley", error.networkResponse.toString());
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
        requestQueue.add(jsonArrayRequest);

//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//
//                        Job job = new Job();
//                        job.setSummary(jsonObject.getString("summary"));
//                        job.setId(jsonObject.getInt("id"));
//                        job.setImage_url(jsonObject.getString("image"));
//
//                        jobList.add(job);
//                    } catch (JSONException e) {
//                        Log.d("Test", "Calling FAB");
//                        e.printStackTrace();
//                        progressDialog.dismiss();
//                    }
//                }
//                adapter.notifyDataSetChanged();
//                progressDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Volley", error.toString());
//                progressDialog.dismiss();
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonArrayRequest);

    }
}
