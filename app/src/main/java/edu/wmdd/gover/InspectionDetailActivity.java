package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
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

public class InspectionDetailActivity extends AppCompatActivity {

    private static Integer inspectionId;
    private static Integer propertyId;
    private Inspection inspection = null;
    private ArrayList<InspectionSection> sections;
    private ListView sectionsList;
    private InspectionSectionListAdapter sectionsListAdapter;

    Button btSaveInspection;

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

        btSaveInspection = (Button) findViewById(R.id.btSaveInspection);
        btSaveInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspection();
            }
        });

        Intent intent = getIntent();
        this.propertyId = intent.getIntExtra("property_id", 0);
        if (propertyId != 0) {
            //loadInspection();
            loadTemplate();
        } else {
            Toast.makeText(InspectionDetailActivity.this, "Error selecting property", Toast.LENGTH_LONG).show();
        }
    }

    private void loadInspection() {

        //if there is an draft inspection
        if (inspection != null) {

        }

    }

    private void checkDraftInspectionExists() {

        //get inspection by property

    }

    private void loadSectionsDropDown() {

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

    private void loadItems() {

    }

    private void createDraftInspection() {

    }

    private void saveDraftInspection() {

    }

    private void saveInspection() {

    }

}
