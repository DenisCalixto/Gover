package edu.wmdd.gover;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class PropertyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_property_detail);

        TextView txtPropertyType = findViewById(R.id.txtPropertyType);
        txtPropertyType.setText(Statics.propertyType);
        Statics.propertyType = "";

        EditText address = findViewById(R.id.address);
        EditText zip = findViewById(R.id.zip);
        EditText unit = findViewById(R.id.unit);
        EditText owner = findViewById(R.id.owner);
        EditText contact = findViewById(R.id.contact);
        EditText notes = findViewById(R.id.notes);

        address.setHint(getString(R.string.property_detail_address_hint));
        zip.setHint(getString(R.string.property_detail_zip_hint));
        unit.setHint(getString(R.string.property_detail_unit_hint));
        owner.setHint(getString(R.string.property_detail_owner_hint));
        contact.setHint(getString(R.string.property_detail_contact_hint));
        notes.setHint(getString(R.string.property_detail_notes_hint));
    }
}
