package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
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
    ImageView signImage;

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
//        setAnimation();

        setContentView(R.layout.activity_report_detail);

        txtAddress = findViewById(R.id.address);
        txtOwner = findViewById(R.id.owner);
        txtTenant = findViewById(R.id.tenant);
        txtNotes = findViewById(R.id.notes);
        propertyImage = findViewById(R.id.propertyImage);
        signImage = findViewById(R.id.signImage);

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
        this.reportId = intent.getIntExtra("reportId", 0);
        if (reportId != 0) {
            fetchReport(reportId);
        }
        else {
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
                        ImageView signatureImg = findViewById(R.id.signImage);
                        signatureImg.setImageBitmap(SignatureBitmap);
                        dialog.dismiss();
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

    public void loadInspection(JSONObject response) {

        try {
            JSONObject jsonObject = response;
            String tenant = jsonObject.getString("tenant_name");
            txtTenant.setText(tenant);
            inspectionId = jsonObject.getInt("id");
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

        Log.d("saveReport", this.reportId.toString());
        Log.d("saveReport", this.inspectionId.toString());
        JSONObject postparams = new JSONObject();
        try {
            if (this.reportId != 0)
                postparams.put("id", this.reportId);
            postparams.put("inspection", this.inspectionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Integer method;
        String url;
        if (this.reportId != 0) {
            method = Request.Method.PUT;
            url = getString(R.string.api_report_url) + this.reportId.toString() + "/";
        }
        else {
            method = Request.Method.POST;
            url = getString(R.string.api_report_url);
        }

        // Volley post request with parameters
        JsonObjectRequest request = new JsonObjectRequest(method, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response;
                            reportId = jsonObject.getInt("id");
                            saveSignature();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        // Volley request policy, only one time request to avoid duplicate transaction
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void saveSignature() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PATCH, getString(R.string.api_report_url)+ this.reportId.toString() + "/", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d("Volley", resultResponse);
                createReportPDF();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", reportId.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("signature", new DataPart("report_signature.png", AppHelper.getFileDataFromDrawable(getBaseContext(), signImage.getDrawable()), "image/png"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);

    }

    private void createReportPDF() {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_report_create_pdf_url) + reportId.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Object resultResponse = response;
                        //Log.d("Volley", resultResponse);
                        Toast.makeText(ReportDetailActivity.this, "Report saved!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ReportDetailActivity.this, PropertyActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ReportDetailActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(ReportDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//                        Log.d("Volley", error.toString());
                        Toast.makeText(ReportDetailActivity.this, "Report saved!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ReportDetailActivity.this, PropertyActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ReportDetailActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
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

    private void shareReport() {

    }

//    public void setAnimation() {
//        if (Build.VERSION.SDK_INT > 20) {
//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.LEFT);
//            slide.setDuration(400);
//            slide.setInterpolator(new DecelerateInterpolator());
//            getWindow().setExitTransition(slide);
//            getWindow().setEnterTransition(slide);
//        }
//    }
}
