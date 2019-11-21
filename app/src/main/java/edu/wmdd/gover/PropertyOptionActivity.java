package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

public class PropertyOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setAnimation();

        setContentView(R.layout.activity_property_option);

        final Button btRealState = (Button) findViewById(R.id.btRealState);
        btRealState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btRealState.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PropertyOptionActivity.this);
                startActivity(intent, options.toBundle());

//                startActivity(intent);
            }
        });

        final Button btVehicle = (Button) findViewById(R.id.btVehicle);
        btVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btVehicle.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PropertyOptionActivity.this);
                startActivity(intent, options.toBundle());

//                startActivity(intent);
            }
        });

        final Button btEquipment = (Button) findViewById(R.id.btEquipment);
        btEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.propertyType = btEquipment.getText().toString();
                Intent intent = new Intent(PropertyOptionActivity.this, PropertyDetailActivity.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PropertyOptionActivity.this);
                startActivity(intent, options.toBundle());

//                startActivity(intent);
            }
        });
    }

    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(400);
            slide.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }
}
