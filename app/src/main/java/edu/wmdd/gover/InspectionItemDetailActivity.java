package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InspectionItemDetailActivity extends AppCompatActivity {

    private static Integer inspectionId = null, sectionId = null, sectionItemId = null, templateItemId = null;
    private static String origin = null;

    TextView sectionName, itemName;
    EditText notesEdit;
    RadioGroup radioGroupState;
    Button btSaveItem;
    ImageView propImage;

    static final int REQUEST_TAKE_PHOTO = 1;

    Bitmap myBitmap;
    String encodedImg;

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

        propImage = findViewById(R.id.propertyImage);

        Button btTakepic = findViewById(R.id.takePicButton);
        btTakepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

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

    ///==============take picture
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {


            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                propImage.setImageBitmap(myBitmap);
            }
        }
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
                        try {
                            JSONObject jsonObject = response;
                            sectionItemId = jsonObject.getInt("id");
                            saveThumbnail();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void saveThumbnail() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PATCH, getString(R.string.api_inspection_item_url)+ this.sectionItemId.toString() + "/", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d("Volley", resultResponse);
                Toast.makeText(InspectionItemDetailActivity.this, "Item saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(InspectionItemDetailActivity.this, InspectionDetailActivity.class);
                intent.putExtra("inspectionId", inspectionId);
                startActivity(intent);
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
                params.put("id", inspectionId.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("picture", new DataPart("inspection_thumbnail.png", AppHelper.getFileDataFromDrawable(getBaseContext(), propImage.getDrawable()), "image/png"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);

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
