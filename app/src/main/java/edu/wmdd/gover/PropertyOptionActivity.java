package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PropertyOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_property_option);

        final Button btRealState = (Button) findViewById(R.id.btRealState);
        btRealState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btRealState.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);
                startActivity(intent);
            }
        });

        final Button btVehicle = (Button) findViewById(R.id.btVehicle);
        btVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btVehicle.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);
                startActivity(intent);
            }
        });

        final Button btEquipment = (Button) findViewById(R.id.btEquipment);
        btEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btEquipment.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
