package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    ImageView propImage;

    static final int REQUEST_TAKE_PHOTO = 1;

    private static Integer propertyId;

    Bitmap myBitmap;
    String encodedImg;
    Bitmap decodeImg;

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
        propImage = findViewById(R.id.propertyImage);

        addressEdit.setHint(getString(R.string.property_detail_address_hint));
        zipEdit.setHint(getString(R.string.property_detail_zip_hint));
        unitEdit.setHint(getString(R.string.property_detail_unit_hint));
        ownerEdit.setHint(getString(R.string.property_detail_owner_hint));
        contactEdit.setHint(getString(R.string.property_detail_contact_hint));
        notesEdit.setHint(getString(R.string.property_detail_notes_hint));


        Button btTakepic = findViewById(R.id.takePicButton);
        btTakepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        btSaveProperty = (Button) findViewById(R.id.btSaveProperty);
        btSaveProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bitmapToBase64();
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

            if (thumbnail != null) {
                new DownloadImageTask(propImage).execute(thumbnail);
            }

//            try {
//                URL url = new URL(thumbnail);
//                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                propImage.setImageBitmap(bmp);
//            }
//            catch (MalformedURLException ex) {
//                // Error occurred while creating the File
//            }
//            catch (IOException ex) {
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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

    //convert bitmap to base64==============
    public void bitmapToBase64() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d("base64", Base64.encodeToString(byteArray, Base64.DEFAULT));
    }

//    public void base64ToImage() {
//        byte[] imageAsBytes = Base64.decode(encodedImg.getBytes(), Base64.DEFAULT);
//        decodeImg = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//
//        ImageView pastedImg = (ImageView) findViewById(R.id.imageView2);
//        pastedImg.setImageBitmap(decodeImg);
//
//
//    }

//========================

    private void saveProperty() {


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
                        try {
                            JSONObject jsonObject = response;
                            propertyId = jsonObject.getInt("id");
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

    private void saveThumbnail() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PATCH, getString(R.string.api_property_url)+ this.propertyId.toString() + "/", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d("Volley", resultResponse);
                Toast.makeText(PropertyDetailActivity.this, "Property saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PropertyDetailActivity.this, PropertyActivity.class);
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
                params.put("id", propertyId.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("thumbnail", new DataPart("property_thumbnail.png", AppHelper.getFileDataFromDrawable(getBaseContext(), propImage.getDrawable()), "image/png"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);

    }
}


