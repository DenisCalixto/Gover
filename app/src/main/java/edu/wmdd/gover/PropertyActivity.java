package edu.wmdd.gover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyActivity extends AppCompatActivity {

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Property> propertyList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.property_list_activity);

        mList = findViewById(R.id.property_list);

        propertyList = new ArrayList<>();

        RecyclerViewClickListener listener = (view, position) -> {
            //Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_SHORT).show();
            //Log.d("Volley", propertyList.get(position).notes);
            Intent intent = new Intent(PropertyActivity.this, PropertyDetailActivity.class);
            intent.putExtra("property_id", propertyList.get(position).id);
            startActivity(intent);
        };

        adapter = new PropertyAdapter(getApplicationContext(), propertyList, listener);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        Button btAddProperty = (Button) findViewById(R.id.btAddProperty);
        btAddProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyActivity.this, PropertyOptionActivity.class);
                startActivity(intent);
            }
        });

        Button btCreateInspection = (Button) findViewById(R.id.btCreateInspection);
        btCreateInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyActivity.this, InspectionCreateActivity.class);
                startActivity(intent);
            }
        });

        Button btInspections = (Button) findViewById(R.id.btInspections);
        btInspections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyActivity.this, InspectionListActivity.class);
                startActivity(intent);
            }
        });

        Button btReports = (Button) findViewById(R.id.btReports);
        btReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyActivity.this, ReportListActivity.class);
                startActivity(intent);
            }
        });

        Button btProfile = (Button) findViewById(R.id.btProfile);
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d("Volley", response.toString());
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                JSONObject jsonObject = response.getJSONObject(i);
//
//                                Property property = new Property();
//                                property.setDescription(jsonObject.getString("description"));
//                                property.setId(jsonObject.getInt("id"));
//                                property.setImage_url(jsonObject.getString("thumbnail"));
//
//                                propertyList.add(property);
//                            } catch (JSONException e) {
//                                Log.d("Test", "Calling FAB");
//                                e.printStackTrace();
//                                progressDialog.dismiss();
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                        progressDialog.dismiss();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(PropertyActivity.this, "Timeout", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(PropertyActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(PropertyActivity.this, "Server is unavailable", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(PropertyActivity.this, "Internet problem", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(PropertyActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//                        }
//                        Log.e("Volley", error.toString());
//                        Log.e("Volley", error.networkResponse.toString());
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String,String>();
//                headers.put("Authorization", "Bearer "+ Auth.accessToken);
//                return headers;
//            };
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonArrayRequest);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_property_url), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                Property property = new Property();
                                property.setName(jsonObject.getString("name"));
                                property.setNotes(jsonObject.getString("notes"));
                                property.setId(jsonObject.getInt("id"));
                                property.setImage_url(jsonObject.getString("thumbnail"));

                                propertyList.add(property);
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
        requestQueue.add(jsonArrayRequest);

    }
}

