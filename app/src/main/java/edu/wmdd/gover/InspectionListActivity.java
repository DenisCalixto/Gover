package edu.wmdd.gover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectionListActivity extends AppCompatActivity {

        private String url = "http://10.0.2.2:8000/api/inspection";

        private RecyclerView mList;

        private LinearLayoutManager linearLayoutManager;
        private DividerItemDecoration dividerItemDecoration;
        private List<Inspection> inspectionList;
        private RecyclerView.Adapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inspection_list);

            mList = findViewById(R.id.inspection_list);

            inspectionList = new ArrayList<>();
            adapter = new InspectionAdapter(getApplicationContext(),inspectionList);

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
                            Log.d("Json", jsonObject.getJSONObject("inspector").toString());
                            Inspection inspection = new Inspection();
                            inspection.setInspectorName(jsonObject.getJSONObject("inspector").getString("first_name") + " " +
                                                        jsonObject.getJSONObject("inspector").getString("last_name"));
                            inspection.setPropertyName(jsonObject.getJSONObject("inspected_property").getString("name"));
                            if (jsonObject.getString("inspection_date") != "") {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Date d = sdf.parse(jsonObject.getString("inspection_date"));
                                    inspection.setInspectionDate(d);
                                } catch (ParseException ex) {
                                    Log.d("Exception", ex.getLocalizedMessage());
                                }
                            }
                            inspection.setNotes(jsonObject.getString("notes"));
                            inspection.setId(jsonObject.getInt("id"));
                            inspection.setImage_url(jsonObject.getJSONObject("inspected_property").getString("thumbnail"));

                            inspectionList.add(inspection);
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

