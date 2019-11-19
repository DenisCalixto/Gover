package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InspectionDetailActivity extends AppCompatActivity {

    private static Integer inspectionId = null;
    private static Integer propertyId;

    private ArrayList<InspectionSection> sections;

    private ListView sectionsList;
    private InspectionSectionListAdapter sectionsListAdapter;

    Button btSaveInspection;
    Button btReport;
    Button btAddSection;
    Button btAddTenant;
    Button btnAddSignature;
    final Context context = this;

    //signature instances
//    private SignaturePad mSignaturePad;
//    private Button mClearButton;
//    private Button mSaveButton;

    String encodedSignature;

    //================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_inspection_detail);

        sections = new ArrayList<InspectionSection>();

        sectionsList = (ListView) findViewById(R.id.sectionsList);
        sectionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                InspectionSection section = (InspectionSection) adapter.getItemAtPosition(position);
                Intent intent = new Intent(InspectionDetailActivity.this, InspectionSectionDetailActivity.class);
                intent.putExtra("inspectionId", inspectionId);
                intent.putExtra("sectionId", section.getId());
                intent.putExtra("sectionName", section.getName());
                startActivity(intent);
            }
        });

        btAddTenant = (Button) findViewById(R.id.btAddTenant);
        btAddTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTenant();
            }
        });

        btAddSection = (Button) findViewById(R.id.btAddSection);
        btAddSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSection();
            }
        });

        btSaveInspection = (Button) findViewById(R.id.btReport);
        btSaveInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspection();
            }
        });



        btReport = (Button) findViewById(R.id.btReport);
        btReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InspectionDetailActivity.this, ReportDetailActivity.class);
                intent.putExtra("inspectionId", inspectionId);
                startActivity(intent);
            }
        });

        manageScreenState();
    }
//    public void bitmapToBase64() {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        SignatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream .toByteArray();
//        encodedSignature = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        Log.d("base64", Base64.encodeToString(byteArray, Base64.DEFAULT));
//    }

    //check if an inspection was sent (from Inspection List) or a property (from Create Inspection property list)
    private void manageScreenState() {
        Intent intent = getIntent();

        //id an inspection was sent
        if (intent.getExtras().containsKey("inspectionId")) {
            this.inspectionId = intent.getIntExtra("inspectionId", 0);
            if (this.inspectionId != 0) {
                //load inspection
                loadInspection();
            } else {
                Toast.makeText(InspectionDetailActivity.this, "Error loading inspection", Toast.LENGTH_LONG).show();
            }
        }
        else {
            //property was sent
            this.propertyId = intent.getIntExtra("property_id", 0);
            if (this.propertyId != 0) {
                //manage draft
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
                    public void onResponse(JSONObject response) {
                        parseInspection(response);
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

    //Load a inspection draft of the property if it exists or create one if not
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
                    //if there is not draft for the current Property, create one
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
            postparams.put("user_id", Auth.userId);
            postparams.put("property_id", this.propertyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.api_inspection_url) + "create_draft/", postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley createDraft", response.toString());
                        parseInspection(response);
                        setupSectionsList();
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

    public void parseInspection(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            this.inspectionId = jsonObject.getInt("id");

            JSONArray templateSections = jsonObject.getJSONArray("sections");
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
                    item.setStatus(templateItems.getJSONObject(k).getString("status"));
                    items.add(item);
                }
                section.setItems(items);
                sections.add(section);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addSection() {
        Intent intent = new Intent(InspectionDetailActivity.this, InspectionDetailActivity.class);
        intent.putExtra("inspectionId", inspectionId);
        startActivity(intent);
    }

    private void addTenant() {
        Intent intent = new Intent(InspectionDetailActivity.this, TenantDetailActivity.class);
        intent.putExtra("inspectionId", inspectionId);
        startActivity(intent);
    }

    private void editSection(Integer section_id) {
        Intent intent = new Intent(InspectionDetailActivity.this, InspectionDetailActivity.class);
        intent.putExtra("inspectionId", inspectionId);
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
                        Log.e("Volley", e.toString());
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
