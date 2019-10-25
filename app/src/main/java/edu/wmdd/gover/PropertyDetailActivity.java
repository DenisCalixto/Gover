package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

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

    EditText address;
    EditText zip;
    EditText unit;
    EditText owner;
    EditText contact;
    EditText notes;
    TextView txtPropertyType;
    Button btSaveProperty;

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

        address = findViewById(R.id.address);
        zip = findViewById(R.id.zipcode);
        unit = findViewById(R.id.unit);
        owner = findViewById(R.id.owner);
        contact = findViewById(R.id.contact);
        notes = findViewById(R.id.notes);

        address.setHint(getString(R.string.property_detail_address_hint));
        zip.setHint(getString(R.string.property_detail_zip_hint));
        unit.setHint(getString(R.string.property_detail_unit_hint));
        owner.setHint(getString(R.string.property_detail_owner_hint));
        contact.setHint(getString(R.string.property_detail_contact_hint));
        notes.setHint(getString(R.string.property_detail_notes_hint));

        btSaveProperty = (Button) findViewById(R.id.btSaveProperty);
        btSaveProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProperty();
            }
        });
    }

    private void saveProperty() {

        String url = "http://10.0.2.2:8000/api/property/";

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("address", address.getText());
            postparams.put("zipcode", zip.getText());
            postparams.put("unit", unit.getText());
            postparams.put("owner", owner.getText());
            postparams.put("contact", contact.getText());
            postparams.put("notes", notes.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", response.toString());
                        Toast.makeText(PropertyDetailActivity.this, "Property saved!", Toast.LENGTH_LONG).show();
                        //parseData(response);
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
//                headers.put("Charset", "UTF-8");
//                headers.put("Content-Type", "application/json");
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


