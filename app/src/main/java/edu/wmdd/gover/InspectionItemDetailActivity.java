package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InspectionItemDetailActivity extends AppCompatActivity {

    private static Integer inspectionId = null, sectionId = null, sectionItemId = null, templateItemId = null;
    private static String origin = null;

    TextView sectionName, itemName;
    EditText notesEdit;
    RadioGroup radioGroupState;
    Button btSaveItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_inspection_item_detail);

        Intent intent = getIntent();
        this.inspectionId = intent.getIntExtra("inspectionId", 0);
        this.sectionId = intent.getIntExtra("sectionId", 0);
        this.origin = intent.getStringExtra("origin");

        sectionName = (TextView) findViewById(R.id.sectionName);
        sectionName.setText(intent.getStringExtra("sectionName"));
        itemName = (TextView) findViewById(R.id.itemName);
        itemName.setText(intent.getStringExtra("sectionItemName"));

        notesEdit = findViewById(R.id.notes);
        notesEdit.setHint(getString(R.string.property_detail_notes_hint));

        radioGroupState = findViewById(R.id.radioGroupState);

        btSaveItem = (Button) findViewById(R.id.btSaveItem);
        btSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSectionItem();
            }
        });
    }

    private void manageScreenState() {
        Intent intent = getIntent();

        if (intent.getExtras().containsKey("sectionItemId")) {
            this.sectionItemId = intent.getIntExtra("sectionItemId", 0);
            loadSectionItem();
        } else {
            //if the template item id was sent, it is CREATE mode
            if (!intent.getExtras().containsKey("templateItemId")) {
                loadSectionItem();
            }
            else {
                Toast.makeText(InspectionItemDetailActivity.this, "Error loading the item!", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(InspectionItemDetailActivity.this, InspectionDetailActivity.class);
                startActivity(intent2);
            }
        }
    }

    private void loadSectionItem() {

    }

    private void saveSectionItem() {
        JSONObject postparams = new JSONObject();
        try {
            if (this.sectionItemId != null)
                postparams.put("id", this.sectionItemId);
            postparams.put("inspection_section", this.sectionId);
            postparams.put("name", itemName.getText());
            postparams.put("status", getSelectedStatus());
            postparams.put("notes", notesEdit.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Integer method;
        String url;
        if (this.sectionItemId != null) {
            method = Request.Method.PUT;
            url = getString(R.string.api_inspection_item_url) + this.sectionItemId.toString() + "/";
        }
        else {
            method = Request.Method.POST;
            url = getString(R.string.api_inspection_item_url);
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(method, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        Toast.makeText(InspectionItemDetailActivity.this, "Item saved!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(InspectionItemDetailActivity.this, InspectionDetailActivity.class);
                        intent.putExtra("inspectionId", inspectionId);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", error.toString());
                        Toast.makeText(InspectionItemDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    private String getSelectedStatus() {
        int selectedRadioButtonID = radioGroupState.getCheckedRadioButtonId();

        // If nothing is selected from Radio Group, then it return -1
        if (selectedRadioButtonID != -1) {

            RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
            String selectedRadioButtonText = selectedRadioButton.getText().toString();

            return selectedRadioButtonText;
        }
        return "not found";
    }
}
