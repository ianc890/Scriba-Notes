package com.materialnotes.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.materialnotes.R;
import com.shamanland.fab.FloatingActionButton;

import roboguice.inject.InjectView;

/**
 * Created by Ian C on 03/05/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PopupMenuActivity extends Activity implements PopupMenu.OnMenuItemClickListener {

    @InjectView(R.id.popup_button) private FloatingActionButton popupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        popupButton = (FloatingActionButton) findViewById(R.id.popup_button);
    }



    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.red:
                Toast.makeText(this, "Red Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.blue:
                Toast.makeText(this, "Blue Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.green:
                Toast.makeText(this, "Green Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.black:
                Toast.makeText(this, "Black Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.white:
                Toast.makeText(this, "White Clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }


}
