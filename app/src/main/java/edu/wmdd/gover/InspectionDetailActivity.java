package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.Optional;

public class InspectionDetailActivity extends AppCompatActivity {

    private static Integer inspectionId = null;
    private static Integer propertyId;

    private Inspection inspection = null;
    private ArrayList<InspectionSection> sections;

    private ListView sectionsList;
    private InspectionSectionListAdapter sectionsListAdapter;

    Button btSaveInspection;
    Button btAddSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_inspection_detail);

        sectionsList = findViewById(R.id.sectionsList);
        sections = new ArrayList<InspectionSection>();

        sectionsList = (ListView) findViewById(R.id.sectionsList);

        btAddSection = (Button) findViewById(R.id.btAddSection);
        btAddSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSection();
            }
        });

        btSaveInspection = (Button) findViewById(R.id.btSaveInspection);
        btSaveInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspection();
            }
        });

        manageScreenState();
    }

    private void manageScreenState() {
        Intent intent = getIntent();

        if (intent.getExtras().containsKey("inspection_id")) {
            this.inspectionId = intent.getIntExtra("inspection_id", 0);
            if (this.inspectionId != 0) {
                loadInspection();
            } else {
                Toast.makeText(InspectionDetailActivity.this, "Error loading inspection", Toast.LENGTH_LONG).show();
            }
        }
        else {
            this.propertyId = intent.getIntExtra("property_id", 0);
            if (this.propertyId != 0) {
                //loadInspection();
                manageDraft();
            } else {
                Toast.makeText(InspectionDetailActivity.this, "Error selecting property", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadInspection() {

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_inspection_url) + this.inspectionId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject templateObject) {
                        try {
                            //getting the sections
                            JSONArray templateSections = templateObject.getJSONArray("sections");
                            for (int j = 0; j < templateSections.length(); j++) {
                                InspectionSection section = new InspectionSection();
                                section.setName(templateSections.getJSONObject(j).getString("name"));
                                section.setId(templateSections.getJSONObject(j).getInt("id"));

                                //getting the items
                                JSONArray templateItems = templateSections.getJSONObject(j).getJSONArray("items");
                                ArrayList<InspectionSectionItem> items = new ArrayList<>();
                                for (int k = 0; k < templateItems.length(); k++) {
                                    InspectionSectionItem item = new InspectionSectionItem();
                                    item.setId(templateItems.getJSONObject(k).getInt("id"));
                                    item.setName(templateItems.getJSONObject(k).getString("name"));
                                    items.add(item);
                                }
                                section.setItems(items);
                                sections.add(section);
                            }
                        } catch (JSONException e) {
                            Log.d("Volley loadInspection", e.getStackTrace().toString());
                            e.printStackTrace();
                        }

                        setupSectionsList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley loadInspection", error.toString());
                        Toast.makeText(InspectionDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
        requestQueue.add(request);
    }

    private void manageDraft() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //get inspection by property
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.api_inspection_url) + "get_draft_by_property/?property_id=" + this.propertyId.toString(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //if there is not draft for the current Property
                    if (response.length() == 0) {
                        createDraft();
                    }
                    else { //there is a draft
                        inspectionId = response.getInt("id");
                        loadInspection();
                    }
                } catch (JSONException e) {
                    Log.d("Volley manageDraft", e.getStackTrace().toString());
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley manageDraft", error.toString());
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

    private void createDraft() {

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("property_id", this.propertyId);
            postparams.put("status", "DR");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.api_inspection_url), postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley createDraft", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley createDraft", error.toString());
                        Toast.makeText(InspectionDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization", "Bearer "+ Auth.accessToken);
                return headers;
            };
        };

        // Volley request policy, only one time request to avoid duplicate transaction
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void addSection() {
        Intent intent = new Intent(InspectionDetailActivity.this, InspectionDetailActivity.class);
        intent.putExtra("inspection_id", inspectionId);
        startActivity(intent);
    }

    private void editSection(Integer section_id) {
        Intent intent = new Intent(InspectionDetailActivity.this, InspectionDetailActivity.class);
        intent.putExtra("inspection_id", inspectionId);
        intent.putExtra("section_id", section_id);
        startActivity(intent);
    }

    private void loadTemplate() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_inspection_template_url), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        //getting the template
                        JSONObject templateObject = response.getJSONObject(i);
                        if (templateObject.getString("property_type").equals("PL")) {
                            //getting the sections
                            JSONArray templateSections = templateObject.getJSONArray("sections");
                            for (int j = 0; j < templateSections.length(); j++) {
                                InspectionSection section = new InspectionSection();
                                section.setName(templateSections.getJSONObject(j).getString("name"));
                                section.setId(templateSections.getJSONObject(j).getInt("id"));

                                //getting the items
                                JSONArray templateItems = templateSections.getJSONObject(j).getJSONArray("items");
                                ArrayList<InspectionSectionItem> items = new ArrayList<>();
                                for (int k = 0; k < templateItems.length(); k++) {
                                    InspectionSectionItem item = new InspectionSectionItem();
                                    item.setId(templateItems.getJSONObject(k).getInt("id"));
                                    item.setName(templateItems.getJSONObject(k).getString("name"));
                                    items.add(item);
                                }
                                section.setItems(items);
                                sections.add(section);
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("Test", "Calling FAB");
                        e.printStackTrace();
                    }
                }

                setupSectionsList();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(InspectionDetailActivity.this, "Timeout", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(InspectionDetailActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(InspectionDetailActivity.this, "Server is unavailable", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(InspectionDetailActivity.this, "Internet problem", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(InspectionDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    }

    private void setupSectionsList(){
        sectionsListAdapter = new InspectionSectionListAdapter(this, sections);
        sectionsList.setAdapter(sectionsListAdapter);
    }

    private void saveInspection() {

    }

}
