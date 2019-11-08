package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportDetailActivity extends AppCompatActivity {

    TextView txtAddress;
    TextView txtOwner;
    TextView txtNotes;
    TextView txtTenant;
    Button btSaveReport;
    Button btSign;
    Button btShareReport;
    ImageView propertyImage;

    static final int REQUEST_TAKE_PHOTO = 1;

    private static Integer inspectionId;
    private static Integer reportId;

    Bitmap myBitmap;
    String encodedImg;
    Bitmap decodeImg;



    Button btReport;
    Button btAddSection;
    Button btnAddSignature;
    final Context context = this;
    Bitmap SignatureBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_report_detail);

        txtAddress = findViewById(R.id.address);
        txtOwner = findViewById(R.id.owner);
        txtTenant = findViewById(R.id.tenant);
        txtNotes = findViewById(R.id.notes);
        propertyImage = findViewById(R.id.propertyImage);

        btSaveReport = (Button) findViewById(R.id.btSaveReport);
        btSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
            }
        });

        btShareReport = (Button) findViewById(R.id.btShareReport);
        btShareReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareReport();
            }
        });


        Intent intent = getIntent();
        if (intent.getExtras().containsKey("reportId")) {
            this.reportId = intent.getIntExtra("reportId", 0);
            if (reportId != 0) {
                fetchReport(reportId);
            }
        } else {
            this.inspectionId = intent.getIntExtra("inspectionId", 0);
            if (inspectionId != 0) {
                fetchInspection(inspectionId);
            }
        }


        //signature
        btnAddSignature = findViewById(R.id.btSign);
        btnAddSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.signature_layout);

                Button mClearButton = (Button) dialog.findViewById(R.id.clear_button);
                Button mSaveButton = (Button) dialog.findViewById(R.id.save_button);

                SignaturePad mSignaturePad = (SignaturePad) dialog.findViewById(R.id.signature_pad);
                mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

                    @Override
                    public void onStartSigning() {
                        //Event triggered when the pad is touched


                    }

                    @Override
                    public void onSigned() {
                        //Event triggered when the pad is signed
                        mSaveButton.setEnabled(true);
                        mClearButton.setEnabled(true);
                    }

                    @Override
                    public void onClear() {
                        //Event triggered when the pad is cleared
                        mSaveButton.setEnabled(false);
                        mClearButton.setEnabled(false);
                    }
                });

                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SignatureBitmap = mSignaturePad.getSignatureBitmap();
//                        bitmapToBase64();
//                        base64ToImage();
                    }
                });
                mClearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSignaturePad.clear();
                    }
                });

                dialog.show();
            }
        });

        //=====
    }




    private void fetchReport(Integer reportId) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_report_url) + reportId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        loadReport(response);
                        try {
                            JSONObject jsonObject = response;
                            fetchInspection(jsonObject.getJSONObject("inspection").getInt("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReportDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    public void loadReport(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            Integer idReport = jsonObject.getInt("id");
            this.reportId = idReport;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchInspection(Integer inspectionId) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_inspection_url) + inspectionId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Volley", response.toString());
                        loadInspection(response);
                        try {
                            JSONObject jsonObject = response;
                            fetchProperty(jsonObject.getInt("inspected_property"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReportDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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

    public void loadInspection(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            String tenant = jsonObject.getString("tenant_name");
            txtTenant.setText(tenant);
        } catch (JSONException e) {
            e.printStackTrace();
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
                        Toast.makeText(ReportDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
            String notes = jsonObject.getString("notes");
            String address = jsonObject.getString("address");
            String thumbnail = jsonObject.getString("thumbnail");
            String owner = jsonObject.getString("owner");

            if (!address.equals(""))
                txtAddress.setText("Address: " + address);
            else
                txtAddress.setText("Address: not provided");

            if (!owner.equals(""))
                txtOwner.setText(owner);
            else
                txtOwner.setText("Owner: not provided");

            if (!notes.equals(""))
                txtNotes.setText(notes);

            if (thumbnail != null) {
                new DownloadImageTask(propertyImage).execute(thumbnail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveReport() {

    }

    private void shareReport() {

    }
}
