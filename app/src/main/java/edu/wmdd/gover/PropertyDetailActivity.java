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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PropertyDetailActivity extends AppCompatActivity {

    EditText addressEdit;
    EditText zipEdit;
    EditText unitEdit;
    EditText ownerEdit;
    EditText contactEdit;
    EditText notesEdit;
    TextView txtPropertyType;
    Button btSaveProperty;

    private static Integer propertyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_property_detail);

        txtPropertyType = findViewById(R.id.txtPropertyType);
        txtPropertyType.setText(Statics.propertyType);
        Statics.propertyType = "";

        addressEdit = findViewById(R.id.address);
        zipEdit = findViewById(R.id.zipcode);
        unitEdit = findViewById(R.id.unit);
        ownerEdit = findViewById(R.id.owner);
        contactEdit = findViewById(R.id.contact);
        notesEdit = findViewById(R.id.notes);

        addressEdit.setHint(getString(R.string.property_detail_address_hint));
        zipEdit.setHint(getString(R.string.property_detail_zip_hint));
        unitEdit.setHint(getString(R.string.property_detail_unit_hint));
        ownerEdit.setHint(getString(R.string.property_detail_owner_hint));
        contactEdit.setHint(getString(R.string.property_detail_contact_hint));
        notesEdit.setHint(getString(R.string.property_detail_notes_hint));

        btSaveProperty = (Button) findViewById(R.id.btSaveProperty);
        btSaveProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProperty();
            }
        });

        Intent intent = getIntent();
        this.propertyId = intent.getIntExtra("property_id", 0);
        if (propertyId != 0) {
            fetchProperty(propertyId);
        }
    }

    private void fetchProperty(Integer propertyId) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_property_url) + propertyId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        loadProperty(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PropertyDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Volley", error.networkResponse.toString());
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

    public void loadProperty(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String notes = jsonObject.getString("notes");
            String address = jsonObject.getString("address");
            String unit = jsonObject.getString("unit");
            String zipcode = jsonObject.getString("zipcode");
            String city = jsonObject.getString("city");
            String province = jsonObject.getString("province");
            String thumbnail = jsonObject.getString("thumbnail");
            String owner = jsonObject.getString("owner");
            String contact = jsonObject.getString("contact");

            addressEdit.setText(address);
            zipEdit.setText(zipcode);
            unitEdit.setText(unit);
            ownerEdit.setText(owner);
            contactEdit.setText(contact);
            notesEdit.setText(notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveProperty() {

        ImageView propertyImage = (ImageView) findViewById(R.id.propertyImage);
        //propertyImage.setImageResource(R.drawable.building);

        JSONObject postparams = new JSONObject();
        try {
            if (this.propertyId != 0)
                postparams.put("id", this.propertyId);
            postparams.put("address", addressEdit.getText());
            postparams.put("zipcode", zipEdit.getText());
            postparams.put("unit", unitEdit.getText());
            postparams.put("owner", ownerEdit.getText());
            postparams.put("contact", contactEdit.getText());
            postparams.put("notes", notesEdit.getText());
            postparams.put("property_type", "PL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Integer method;
        String url;
        if (this.propertyId != 0) {
            method = Request.Method.PUT;
            url = getString(R.string.api_property_url) + this.propertyId.toString() + "/";
            //Log.d("Volley", url);
        }
        else {
            method = Request.Method.POST;
            url = getString(R.string.api_property_url);
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(method, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        Toast.makeText(PropertyDetailActivity.this, "Property saved!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PropertyDetailActivity.this, PropertyActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley", error.toString());
                        Toast.makeText(PropertyDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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


