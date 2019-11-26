package edu.wmdd.gover;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        private RecyclerView mList;

        private LinearLayoutManager linearLayoutManager;
        private DividerItemDecoration dividerItemDecoration;
        private List<Inspection> inspectionList;
        private RecyclerView.Adapter adapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            getSupportActionBar().hide(); // hide the title bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

//            setAnimation();

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

            SearchView searchViewInspections = findViewById(R.id.searchViewInspections);
            searchViewInspections.setQueryHint(getString(R.string.inspections_search_hint));

            //Start Bottom Nav

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_inspection);
//            bottomNavigationView.getMenu().getItem(0).setChecked(false);
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
//            bottomNavigationView.getMenu().getItem(2).setChecked(false);
//            bottomNavigationView.getMenu().getItem(3).setChecked(false);

//            bottomNavigationView.setSelectedItemId(R.id.btInspections);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if ( item.getItemId() == R.id.btInspections){
                        Intent intent = new Intent(InspectionListActivity.this, InspectionListActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InspectionListActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
                    }
                    else if ( item.getItemId() == R.id.btProperties){
                        Intent intent = new Intent(InspectionListActivity.this, PropertyActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InspectionListActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
                    }
                    else if( item.getItemId() == R.id.btReports){
                        Intent intent = new Intent(InspectionListActivity.this, ReportListActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InspectionListActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(InspectionListActivity.this, ProfileActivity.class);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InspectionListActivity.this);
                        startActivity(intent, options.toBundle());

//                        startActivity(intent);
                    }
                    return false;
                }
            });

//End Bottom Nav

            getData();
        }

        private void getData() {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_inspection_url), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Log.d("InspectionListActivity", response.getJSONObject(i).toString());
                            Inspection inspection = new Inspection();
                            inspection.setPropertyName(jsonObject.getJSONObject("inspected_property_obj").getString("address"));
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
                            inspection.setImage_url(jsonObject.getJSONObject("inspected_property_obj").getString("thumbnail"));

                            inspectionList.add(inspection);
                        } catch (JSONException e) {
                            Log.e("Volley", e.toString());
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

