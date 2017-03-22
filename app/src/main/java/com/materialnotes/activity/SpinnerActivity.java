package com.materialnotes.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.materialnotes.R;
import com.materialnotes.view.ShowHideOnScroll;
import com.shamanland.fab.FloatingActionButton;

import java.util.prefs.Preferences;

import roboguice.activity.RoboActionBarActivity;

/**
 * Created by Ian C on 17/06/2016.
 */
public class SpinnerActivity extends RoboActionBarActivity implements AdapterView.OnItemSelectedListener {

    private ScrollView scrollView;
    private FloatingActionButton saveSettingsButton;

    // SpinnerActivity element
    Spinner spinner;
    Spinner spinner2;
    Spinner spinner3;
    Spinner spinner4;

    public static int settingOne = 6, settingTwo = 0, settingThree = 7, settingFour = 5;
    public static boolean isSettingsSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog);

        Resources res = getResources(); //call the resources folder
        String[] options = res.getStringArray(R.array.settingsArray); //string array that holds the values for the spinners from array declared in strings.xml

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Shows the go back arrow

        saveSettingsButton = (FloatingActionButton) findViewById(R.id.save_settings_button);

        scrollView = (ScrollView) findViewById(R.id.scroll_view_settings);
        scrollView.setOnTouchListener(new ShowHideOnScroll(saveSettingsButton, getSupportActionBar())); // Hides or shows the FAB and the Action Bar

        // SpinnerActivity element
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner4 = (Spinner) findViewById(R.id.spinner4);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);
        spinner3.setAdapter(dataAdapter);
        spinner4.setAdapter(dataAdapter);

        //set item selection on each spinner
        spinner.setSelection(settingOne);
        spinner2.setSelection(settingTwo);
        spinner3.setSelection(settingThree);
        spinner4.setSelection(settingFour);

        // SpinnerActivity click listener
        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);
        spinner4.setOnItemSelectedListener(this);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Preferences savedInstanceState = null;

        if(savedInstanceState != null) {
            savedInstanceState.putInt("MyInt", settingOne);
            savedInstanceState.putInt("MyInt2", settingTwo);
            savedInstanceState.putInt("MyInt3", settingThree);
            savedInstanceState.putInt("MyInt4", settingFour);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        settingOne = savedInstanceState.getInt("MyInt");
        settingTwo = savedInstanceState.getInt("MyInt2");
        settingThree = savedInstanceState.getInt("MyInt3");
        settingFour = savedInstanceState.getInt("MyInt4");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.spinner:
                // do stuffs with you spinner 1

                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                settingOne = parent.getSelectedItemPosition();
                Log.i("Log", item+": "+settingOne);

                break;
            case R.id.spinner2:
                // do stuffs with you spinner 2

                // On selecting a spinner item
                item = parent.getItemAtPosition(position).toString();
                settingTwo = parent.getSelectedItemPosition();
                Log.i("Log", item+": "+settingTwo);

                break;
            case R.id.spinner3:
                // do stuffs with you spinner 3

                // On selecting a spinner item
                item = parent.getItemAtPosition(position).toString();
                settingThree = parent.getSelectedItemPosition();
                Log.i("Log", item+": "+settingThree);

                break;
            case R.id.spinner4:
                // do stuffs with you spinner 4

                // On selecting a spinner item
                item = parent.getItemAtPosition(position).toString();
                settingFour = parent.getSelectedItemPosition();
                Log.i("Log", item+": "+settingFour);

                break;
            default:
                break;
        }

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void saveSettings(View view) {
        isSettingsSaved = true;

        Log.i("Log", settingOne+": "+settingTwo+": "+settingThree+": "+settingFour);

        Intent intent= new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }
}
