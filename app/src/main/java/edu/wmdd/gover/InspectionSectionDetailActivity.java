package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
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
import java.util.Map;

public class InspectionSectionDetailActivity extends AppCompatActivity {

    private Integer inspectionId = null, sectionId;

    private ArrayList<InspectionSectionItem> sectionsItems;

    private ListView itemsList;
    private InspectionSectionItemListAdapter sectionItemsListAdapter;

    TextView sectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

//        setAnimation();

        setContentView(R.layout.activity_inspection_section_detail);

        Intent intent = getIntent();
        this.inspectionId = intent.getIntExtra("inspectionId", 0);
        this.sectionId = intent.getIntExtra("sectionId", 0);

        sectionName = (TextView) findViewById(R.id.sectionName);
        sectionName.setText(intent.getStringExtra("sectionName"));

        sectionsItems = new ArrayList<InspectionSectionItem>();

        itemsList = (ListView) findViewById(R.id.itemsList);
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                InspectionSectionItem sectionItem = (InspectionSectionItem) adapter.getItemAtPosition(position);
                Intent intent = new Intent(InspectionSectionDetailActivity.this, InspectionItemDetailActivity.class);
                intent.putExtra("inspectionId", inspectionId);
                intent.putExtra("sectionId", sectionId);
                intent.putExtra("sectionName", sectionName.getText());
                intent.putExtra("sectionItemName", sectionItem.getName());
                intent.putExtra("templateItemId", sectionItem.getId());
                intent.putExtra("origin", "template");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InspectionSectionDetailActivity.this);
                startActivity(intent, options.toBundle());

//                startActivity(intent);
            }
        });

        loadTemplate();
    }

    private void loadTemplate() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_inspection_template_item_url), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject itemObject = response.getJSONObject(i);
                        InspectionSectionItem item = new InspectionSectionItem();
                        item.setId(itemObject.getInt("id"));
                        item.setName(itemObject.getString("name"));
                        sectionsItems.add(item);
                    } catch (JSONException e) {
                        Log.e("Volley", e.toString());
                        e.printStackTrace();
                    }
                }

                setupSectionItemsList();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InspectionSectionDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Volley", error.toString());
                        Log.e("Volley", error.networkResponse.toString());
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

    private void setupSectionItemsList(){
        sectionItemsListAdapter = new InspectionSectionItemListAdapter(this, sectionsItems);
        itemsList.setAdapter(sectionItemsListAdapter);
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
