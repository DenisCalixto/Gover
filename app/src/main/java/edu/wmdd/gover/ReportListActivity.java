package edu.wmdd.gover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class ReportListActivity extends AppCompatActivity {

    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Report> reportList;
    private RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_report_list);

        mList = findViewById(R.id.report_list);

        reportList = new ArrayList<>();

        RecyclerViewClickListener listener = (view, position) -> {
            Intent intent = new Intent(ReportListActivity.this, ReportDetailActivity.class);
            intent.putExtra("reportId", reportList.get(position).getId());
            startActivity(intent);
        };
        
        adapter = new ReportAdapter(getApplicationContext(),reportList, listener);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        SearchView searchViewInspections = findViewById(R.id.searchViewReports);
        searchViewInspections.setQueryHint(getString(R.string.reports_search_hint));

//        EditText txtPropertySearch = findViewById(R.id.txtPropertySearch);
//        txtPropertySearch.setHint(getString(R.string.property_search_hint));

        //Start Bottom Nav

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if( item.getItemId() == R.id.btProperties){
                    Intent intent = new Intent(ReportListActivity.this, PropertyActivity.class);
                    startActivity(intent);
                }
                else if( item.getItemId() == R.id.btInspections){
                    Intent intent = new Intent(ReportListActivity.this, InspectionListActivity.class);
                    startActivity(intent);
                }
                else if( item.getItemId() == R.id.btReports){
                    Intent intent = new Intent(ReportListActivity.this, ReportListActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ReportListActivity.this, ProfileActivity.class);
                    startActivity(intent);
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

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_report_url), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Report report = new Report();
                        report.setPropertyName(jsonObject.getJSONObject("inspection_obj")
                                                .getJSONObject("inspected_property")
                                                .getString("address"));
                        if (jsonObject.getString("created") != "") {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date d = sdf.parse(jsonObject.getString("created"));
                                report.setReportDate(d);
                            } catch (ParseException ex) {
                                Log.d("Exception", ex.getLocalizedMessage());
                            }
                        }
                        report.setNotes(jsonObject.getString("notes"));
                        report.setId(jsonObject.getInt("id"));

                        reportList.add(report);
                    } catch (JSONException e) {
                        Log.e("ReportListActivity", e.toString());
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

