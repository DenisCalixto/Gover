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

public class TenantDetailActivity extends AppCompatActivity {

    EditText tenantEdit;
    EditText phoneEdit;
    EditText emailEdit;
    EditText notesEdit;
    TextView txtPropertyAddress;
    Button btSaveTenant;

    private static Integer inspectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_tenant_detail);

        txtPropertyAddress = findViewById(R.id.txtPropertyType);

        tenantEdit = findViewById(R.id.tenant);
        phoneEdit = findViewById(R.id.phone);
        emailEdit = findViewById(R.id.email);
        notesEdit = findViewById(R.id.notes);

//        addressEdit.setHint(getString(R.string.property_detail_address_hint));
//        zipEdit.setHint(getString(R.string.property_detail_zip_hint));
//        unitEdit.setHint(getString(R.string.property_detail_unit_hint));
//        ownerEdit.setHint(getString(R.string.property_detail_owner_hint));
//        contactEdit.setHint(getString(R.string.property_detail_contact_hint));
        notesEdit.setHint(getString(R.string.property_detail_notes_hint));

        btSaveTenant = (Button) findViewById(R.id.btSaveTenant);
        btSaveTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspection();
            }
        });

        Intent intent = getIntent();
        this.inspectionId = intent.getIntExtra("inspectionId", 0);
        if (inspectionId != 0) {
            fetchInspection(inspectionId);
        }
    }

    private void fetchInspection(Integer inspectionId) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_inspection_url) + inspectionId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        loadInspection(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TenantDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Volley", error.toString());
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
        requestQueue.add(jsObjRequest);
    }

    private void loadInspection(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            String notes = jsonObject.getString("tenant_notes");
            String name = jsonObject.getString("tenant_name");
            String phone = jsonObject.getString("tenant_phone");
            String email = jsonObject.getString("tenant_email");

            tenantEdit.setText(name);
            phoneEdit.setText(phone);
            emailEdit.setText(email);
            notesEdit.setText(notes);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveInspection() {


        JSONObject postparams = new JSONObject();
        try {
            postparams.put("id", this.inspectionId);
            postparams.put("tenant_name", tenantEdit.getText());
            postparams.put("tenant_phone", phoneEdit.getText());
            postparams.put("tenant_email", emailEdit.getText());
            postparams.put("tenant_notes", notesEdit.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Integer method = Request.Method.PATCH;
        String url = getString(R.string.api_inspection_url) + this.inspectionId.toString() + "/";

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(method, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            inspectionId = jsonObject.getInt("id");
                            Toast.makeText(TenantDetailActivity.this, "Tenant saved!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(TenantDetailActivity.this, InspectionDetailActivity.class);
                            intent.putExtra("inspectionId", inspectionId);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", error.toString());
                        Toast.makeText(TenantDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
}
