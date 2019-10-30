package edu.wmdd.gover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
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


public class InspectionCreateActivity extends AppCompatActivity {

        private String url = "http://159.65.44.135/api/property";

        private RecyclerView mList;

        private LinearLayoutManager linearLayoutManager;
        private DividerItemDecoration dividerItemDecoration;
        private List<Property> propertyList;
        private RecyclerView.Adapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            getSupportActionBar().hide(); // hide the title bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
            setContentView(R.layout.activity_inspection_create);

            mList = findViewById(R.id.property_list);

            propertyList = new ArrayList<>();

            RecyclerViewClickListener listener = (view, position) -> {
                //Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_SHORT).show();
                //Log.d("Volley", propertyList.get(position).notes);
                Intent intent = new Intent(InspectionCreateActivity.this, InspectionDetailActivity.class);
                intent.putExtra("property_id", propertyList.get(position).id);
                startActivity(intent);
            };

            adapter = new PropertyAdapter(getApplicationContext(), propertyList, listener);

            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

            mList.setHasFixedSize(true);
            mList.setLayoutManager(linearLayoutManager);
            mList.addItemDecoration(dividerItemDecoration);
            mList.setAdapter(adapter);

            getData();
        }

        private void getData() {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Property property = new Property();
                            property.setName(jsonObject.getString("name"));
                            property.setNotes(jsonObject.getString("notes"));
                            property.setId(jsonObject.getInt("id"));
                            property.setImage_url(jsonObject.getString("thumbnail"));

                            propertyList.add(property);
                        } catch (JSONException e) {
                            Log.d("Test", "Calling FAB");
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", error.toString());
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
            requestQueue.add(jsonArrayRequest);

        }
}

