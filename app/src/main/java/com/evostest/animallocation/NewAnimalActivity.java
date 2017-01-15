package com.evostest.animallocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evostest.animallocation.model.Animal;
import com.evostest.animallocation.model.Bird;
import com.evostest.animallocation.model.Mammal;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

public class NewAnimalActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    EditText etName;
    RadioButton rbBird, rbMammal;
    CheckBox cbCanFly, cbHasMilk;
    RadioGroup radioGroup;
    TextView tvLocation;
    Button btAddLocation;

    LatLng animalLocation;

    ArrayList<Animal> animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_animal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(API.KEY_LOCATION)) {
            animalLocation = savedInstanceState.getParcelable(API.KEY_LOCATION);
        }

        etName = (EditText) findViewById(R.id.etName);
        rbBird = (RadioButton) findViewById(R.id.rbBird);
        rbMammal = (RadioButton) findViewById(R.id.rbMammal);
        cbCanFly = (CheckBox) findViewById(R.id.cbCanFly);
        cbHasMilk = (CheckBox) findViewById(R.id.cbHasMilk);
        btAddLocation = (Button) findViewById(R.id.btAddLocation);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        animals = getIntent().getParcelableArrayListExtra(API.KEY_LIST_ANIMALS);

        radioGroup.setOnCheckedChangeListener(this);
        btAddLocation.setOnClickListener(this);

    }

    void addAnimalToList() {
        String animalName = etName.getText().toString();
        if (animalName.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_enter_name), Toast.LENGTH_LONG).show();
            return;
        }
        if (animalLocation == null) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_choose_location), Toast.LENGTH_LONG).show();
            return;
        }
        //add animal
        Animal newAnimal = null;
        if (rbBird.isChecked()) {
            newAnimal = new Bird(animals.size(), animalName, cbCanFly.isChecked());
        } else if (rbMammal.isChecked()){
            newAnimal = new Mammal(animals.size(), animalName, cbHasMilk.isChecked());
        }
        if (newAnimal != null) {
            newAnimal.setLocation(animalLocation);
            Intent intent = new Intent();
            intent.putExtra(API.KEY_NEW_ANIMAL, newAnimal);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btAddLocation:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                if (animalLocation != null) {
                    intent.putExtra(API.KEY_MARKER_POSITION, animalLocation);
                }
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        View radioButton = group.findViewById(checkedId);
        int index = group.indexOfChild(radioButton);
        switch (index) {
            case 0:
                cbCanFly.setVisibility(View.VISIBLE);
                cbHasMilk.setVisibility(View.GONE);
                break;
            case 2:
                cbCanFly.setVisibility(View.GONE);
                cbHasMilk.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (data.hasExtra(API.KEY_LOCATION)) {
            animalLocation = data.getParcelableExtra(API.KEY_LOCATION);
            tvLocation.setText(String.format(Locale.US, "%s: %f, %f",
                    getResources().getString(R.string.location), animalLocation.latitude, animalLocation.longitude));
            tvLocation.setVisibility(View.VISIBLE);
            btAddLocation.setText(getResources().getString(R.string.change_location));
        } else {
            tvLocation.setVisibility(View.GONE);
            btAddLocation.setText(getResources().getString(R.string.add_location));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (animalLocation != null) {
            outState.putParcelable(API.KEY_LOCATION, animalLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                addAnimalToList();
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }
}
