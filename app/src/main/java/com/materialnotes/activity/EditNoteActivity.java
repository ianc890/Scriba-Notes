package com.materialnotes.activity;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;
import com.materialnotes.view.ShowHideOnScrollThree;
import com.shamanland.fab.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import no.nordicsemi.android.scriba.hrs.HRSActivity;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Edit notes activity
 **/
@ContentView(R.layout.activity_edit_note)
public class EditNoteActivity extends RoboActionBarActivity{

    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final int FILTER_ID = 1;

    @InjectView(R.id.note_title)
    private EditText noteTitleText;
    @InjectView(R.id.note_content)
    private EditText noteContentText;
    @InjectView(R.id.popup_button)
    private FloatingActionButton popupButton;
    @InjectView(R.id.keyboard_button)
    private FloatingActionButton keyboardButton;
    @InjectView(R.id.keyboard_hide_button)
    private FloatingActionButton keyboardHideButton;
    //@InjectView(R.id.scroll_view_edit_note)
    private ScrollView scrollView;
    @InjectView(R.id.note_layout)
    private RelativeLayout noteLayout;


    private Note note;
    private SpannableStringBuilder ssbtitle, ssbcontent;
    public TextView valTv2;
    private ActionMode mActionMode = null;
    private String mode = "mode";
    private int modeCounter = 0;

    Vibrator v;

    private Thread t, myThread, deleteThread;

    TextView tv;

    int count;//variable to keep track of number of times popup menu opens when condition is met i.e. when value is between 0 and 300.

    int setOne, setTwo, setThree, setFour;

    int touchPosition;
    int touchPosition2;

    NumberFormat nf = new DecimalFormat("#0");

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActionModeStarted(final ActionMode mode) {
        if (mActionMode == null) {
            mActionMode = mode;

            Menu menu = mode.getMenu();
            MenuItem item1 = menu.findItem(android.R.id.selectAll);
            item1.setVisible(false);

            /*Menu menu = mode.getMenu();
            //menu.removeItem(android.R.id.selectAll);
            //menu.add(0, R.id.clear1, 1, "Clear Format");
            // Remove the default menu items (select all, copy, paste, search)
            //menu.clear();

            if (mode.equals("Highlight") || mode.equals("Underline") || mode.equals("Delete")) {
                MenuItem item1 = menu.findItem(android.R.id.selectAll);
                item1.setVisible(false);
            }

            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

            // If it does contain data, decide if you can handle the data.
            if (clipboard.hasPrimaryClip() && mode.equals("Highlight") || mode.equals("Underline") || mode.equals("Delete")) {
                MenuItem item2 = menu.findItem(android.R.id.paste);
                item2.setVisible(false);
            }

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // Inflate your own menu items
                mode.getMenuInflater().inflate(R.menu.my_custom_menu, menu);
            }*/

        }

        super.onActionModeStarted(mode);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onContextualMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear1:
                unformatContent(item);
                break;
            default:
                break;
        }

        // This will likely always be true, but check it anyway, just in case
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        mActionMode = null;
        super.onActionModeFinished(mode);
    }

    /**
     * Makes the intent to call the activity with an existing note
     *
     * @param context the context
     * @param note    the note to edit
     * @return the Intent.
     */
    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    /**
     * Makes the intent to call the activity for creating a note
     *
     * @param context the context that calls the activity
     * @return the Intent.
     */
    public static Intent buildIntent(Context context) {
        return buildIntent(context, null);
    }

    /**
     * Gets the edited note
     *
     * @param intent the intent from onActivityResult
     * @return the updated note
     */
    public static Note getExtraNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_NOTE);
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteLayout.setOnTouchListener(new ShowHideOnScrollThree(popupButton, keyboardButton, keyboardHideButton, getSupportActionBar())); // Hides or shows the FAB and the Action Bar

        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

        //Typeface type = Typeface.createFromAsset(getAssets(), "fonts/airstrike.ttf");
        //valTv.setTypeface(type);

        valTv2 = (TextView) findViewById(R.id.valTv2);
        valTv2.setText(String.valueOf(100.0));

        tv = new TextView(this);

        noteTitleText.setShowSoftInputOnFocus(false);

        popupButton = (FloatingActionButton) findViewById(R.id.popup_button);
        /*popupButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });//closing the setOnClickListener method*/

        keyboardButton = (FloatingActionButton) findViewById(R.id.keyboard_button);
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                keyboardButton.setVisibility(View.INVISIBLE);
                keyboardHideButton.setVisibility(View.VISIBLE);
            }
        });

        keyboardHideButton = (FloatingActionButton) findViewById(R.id.keyboard_hide_button);
        keyboardHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                keyboardHideButton.setVisibility(View.INVISIBLE);
                keyboardButton.setVisibility(View.VISIBLE);
            }
        });

        // Starts the components //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Shows the go back arrow
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // gets the note from the intent
        if (note != null) { // Edit existing note
            noteTitleText.setText(com.materialnotes.activity.Html.fromHtml(note.getTitle()));
            noteContentText.setText(com.materialnotes.activity.Html.fromHtml(note.getContent()));
        } else { // New note

            note = new Note();
            note.setCreatedAt(new Date());
        }

        threadOne();

        // Get instance of Vibrator from current Context
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setOne = SpinnerActivity.settingOne;
        setTwo = SpinnerActivity.settingTwo;
        setThree = SpinnerActivity.settingThree;
        setFour = SpinnerActivity.settingFour;

        Log.i("Log", setOne+": "+setTwo+": "+setThree+": "+setFour);

        noteTitleText.setOnTouchListener(new View.OnTouchListener() {
            boolean moved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moved = false;

                        float x = (int) event.getX();
                        float y = (int) event.getY();
                        touchPosition = noteTitleText.getOffsetForPosition(x, y);

                        break;
                    case MotionEvent.ACTION_MOVE:

                        float x1 = (int) event.getX();
                        float y1 = (int) event.getY();
                        touchPosition2 = noteTitleText.getOffsetForPosition(x1, y1);
                        noteTitleText.setSelection(touchPosition, touchPosition2);
                        moved = true;

                        break;
                    case MotionEvent.ACTION_UP:

                        /* EditNoteActivity.this.startActionMode(new ActionMode.Callback() {
                            });*/

                        if(moved == true){
                            if(noteTitleText.hasSelection()) {
                                noteTitleText.performLongClick();
                            }
                        }

                        break;
                }

                if(mode.equals("Highlight") || mode.equals("Highlight Blue") || mode.equals("Highlight Green") || mode.equals("Bold") || mode.equals("Italic") || mode.equals("Underline") || mode.equals("Delete")) {
                    tagsThree(false);
                    if(modeCounter == 0 && noteTitleText.hasSelection()) {
                        threadTwo();
                        t.interrupt();
                        modeCounter++;
                        Toast.makeText(getBaseContext(), mode, Toast.LENGTH_SHORT).show();
                    }
                } else if (mode.equals("Select")){
                    tagsTwo(true);
                }

                return false;
            }
        });

        noteContentText.setOnTouchListener(new View.OnTouchListener() {
            boolean moved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        moved = false;

                        float x = (int) event.getX();
                        float y = (int) event.getY();
                        touchPosition = noteContentText.getOffsetForPosition(x, y);

                        break;
                    case MotionEvent.ACTION_MOVE:

                        float x1 = (int) event.getX();
                        float y1 = (int) event.getY();
                        touchPosition2 = noteContentText.getOffsetForPosition(x1, y1);
                        noteContentText.setSelection(touchPosition, touchPosition2);
                        moved = true;

                        break;
                    case MotionEvent.ACTION_UP:

                        /* EditNoteActivity.this.startActionMode(new ActionMode.Callback() {
                            });*/

                        if(moved == true){
                            if(noteContentText.hasSelection()) {
                                noteContentText.performLongClick();
                            }
                        }

                        break;
                }

                if(mode.equals("Highlight") || mode.equals("Highlight Blue") || mode.equals("Highlight Green") || mode.equals("Bold") || mode.equals("Italic") || mode.equals("Underline") || mode.equals("Delete")) {
                    tagsThree(false);
                    if(modeCounter == 0 && noteContentText.hasSelection()) {
                        threadTwo();
                        t.interrupt();
                        modeCounter++;
                        Toast.makeText(getBaseContext(), mode, Toast.LENGTH_SHORT).show();
                    }
                } else if (mode.equals("Select")){
                    tagsTwo(true);
                }

                return false;
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        //tv.setText(String.valueOf(HRSActivity.mHrmValue));
        tv.setTextColor(getResources().getColor(R.color.white_circle));
        //tv.setOnClickListener((View.OnClickListener) this);
        tv.setPadding(5, 0, 5, 0);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(14);
        //menu.add(0, FILTER_ID, 1, String.valueOf(HRSActivity.mHrmValue)).setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
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
            case R.id.action_save:
                if (isNoteFormOk()) {
                    setNoteResult();
                    finish();
                } else validateNoteForm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return {@code true} is the note has title and content; {@code false} every other case
     */
    private boolean isNoteFormOk() {
        //noteTitleText.setTypeface(null, Typeface.NORMAL);
        return !Strings.isNullOrBlank(noteTitleText.getText().toString()) && !Strings.isNullOrBlank(noteContentText.getText().toString());
    }

    /**
     * Updates the note content with the layout texts and it makes the object as a result of the activity
     */
    private void setNoteResult() {
        note.setTitle(com.materialnotes.activity.Html.toHtml(noteTitleText.getText()));
        note.setContent(com.materialnotes.activity.Html.toHtml(noteContentText.getText()));
        note.setUpdatedAt(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
    }

    /**
     * Shows validating messages
     */
    private void validateNoteForm() {
        StringBuilder message = null;
        if (Strings.isNullOrBlank(noteTitleText.getText().toString())) {
            message = new StringBuilder().append(getString(R.string.title_required));
        }
        if (Strings.isNullOrBlank(noteContentText.getText().toString())) {
            if (message == null)
                message = new StringBuilder().append(getString(R.string.content_required));
            else message.append("\n").append(getString(R.string.content_required));
        }
        if (message != null) {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // Note not created or updated
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    //method to get rid of all formats for specific selected text
    public void unformatContent(MenuItem item) {
        if (noteContentText.hasFocus()) {
            int startSelection = noteContentText.getSelectionStart();
            int endSelection = noteContentText.getSelectionEnd();

            Spannable str = noteContentText.getText();
            StyleSpan[] ss = str.getSpans(startSelection, endSelection, StyleSpan.class);

            for (int i = 0; i < ss.length; i++) {
                if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC) {
                    str.removeSpan(ss[i]);
                }
            }

            UnderlineSpan[] ulSpan = str.getSpans(startSelection, endSelection, UnderlineSpan.class);
            for (int i = 0; i < ulSpan.length; i++) {
                str.removeSpan(ulSpan[i]);
            }

            BackgroundColorSpan[] bgSpan = str.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
            for (int i = 0; i < bgSpan.length; i++) {
                str.removeSpan(bgSpan[i]);
            }

            noteContentText.setText(str);
        } else {
            int startSelection = noteTitleText.getSelectionStart();
            int endSelection = noteTitleText.getSelectionEnd();

            Spannable str = noteTitleText.getText();
            StyleSpan[] ss = str.getSpans(startSelection, endSelection, StyleSpan.class);

            for (int i = 0; i < ss.length; i++) {
                if (ss[i].getStyle() == Typeface.BOLD || ss[i].getStyle() == Typeface.ITALIC) {
                    str.removeSpan(ss[i]);
                }
            }

            UnderlineSpan[] ulSpan = str.getSpans(startSelection, endSelection, UnderlineSpan.class);
            for (int i = 0; i < ulSpan.length; i++) {
                str.removeSpan(ulSpan[i]);
            }

            BackgroundColorSpan[] bgSpan = str.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
            for (int i = 0; i < bgSpan.length; i++) {
                str.removeSpan(bgSpan[i]);
            }

            noteTitleText.setText(str);
        }
    }

    //method that reads the current value from the Scriba devcie
    public void checkScribaValue() {
        if (HRSActivity.mHrmValue > 500 && HRSActivity.mHrmValue < 851) {
            if(setTwo == 0){
                mode = "Highlight";
                popupButton.setImageResource(R.drawable.highlight_yellow_icon);
            } else if(setTwo == 1){
                mode = "Highlight Blue";
                popupButton.setImageResource(R.drawable.highlight_blue_icon);
            } else if(setTwo == 2){
                mode = "Highlight Green";
                popupButton.setImageResource(R.drawable.highlight_green_icon);
            } else if(setTwo == 3){
                mode = "Bold";
                popupButton.setImageResource(R.drawable.bold_icon);
            } else if(setTwo == 4){
                mode = "Italic";
                popupButton.setImageResource(R.drawable.italic_icon_new);
            } else if(setTwo == 5){
                mode = "Delete";
                popupButton.setImageResource(R.drawable.eraser_icon);
            } else if(setTwo == 6){
                mode = "Select";
                popupButton.setImageResource(R.drawable.selection_icon);
            } else if(setTwo == 7){
                mode = "Underline";
                popupButton.setImageResource(R.drawable.underline_icon_new);
            }
        } else if (HRSActivity.mHrmValue > 150 && HRSActivity.mHrmValue < 501) {
            if(setThree == 0){
                mode = "Highlight";
                popupButton.setImageResource(R.drawable.highlight_yellow_icon);
                //Snackbar.make(findViewById(android.R.id.content), mode, Snackbar.LENGTH_SHORT).show();
            } else if(setThree == 1){
                mode = "Highlight Blue";
                popupButton.setImageResource(R.drawable.highlight_blue_icon);
            } else if(setThree == 2){
                mode = "Highlight Green";
                popupButton.setImageResource(R.drawable.highlight_green_icon);
            } else if(setThree == 3){
                mode = "Bold";
                popupButton.setImageResource(R.drawable.bold_icon);
            } else if(setThree == 4){
                mode = "Italic";
                popupButton.setImageResource(R.drawable.italic_icon_new);
            } else if(setThree == 5){
                mode = "Delete";
                popupButton.setImageResource(R.drawable.eraser_icon);
            } else if(setThree == 6){
                mode = "Select";
                popupButton.setImageResource(R.drawable.selection_icon);
            } else if(setThree == 7){
                mode = "Underline";
                popupButton.setImageResource(R.drawable.underline_icon_new);
            }
        } else if (HRSActivity.mHrmValue < 151 && HRSActivity.mHrmValue > 30) {
            if(setFour == 0){
                mode = "Highlight";
                popupButton.setImageResource(R.drawable.highlight_yellow_icon);
                //Snackbar.make(findViewById(android.R.id.content), mode, Snackbar.LENGTH_SHORT).show();
            } else if(setFour == 1){
                mode = "Highlight Blue";
                popupButton.setImageResource(R.drawable.highlight_blue_icon);
            } else if(setFour == 2){
                mode = "Highlight Green";
                popupButton.setImageResource(R.drawable.highlight_green_icon);
            } else if(setFour == 3){
                mode = "Bold";
                popupButton.setImageResource(R.drawable.bold_icon);
            } else if(setFour == 4){
                mode = "Italic";
                popupButton.setImageResource(R.drawable.italic_icon_new);
            } else if(setFour == 5){
                mode = "Delete";
                popupButton.setImageResource(R.drawable.eraser_icon);
            } else if(setFour == 6){
                mode = "Select";
                popupButton.setImageResource(R.drawable.selection_icon);
            } else if(setFour == 7){
                mode = "Underline";
                popupButton.setImageResource(R.drawable.underline_icon_new);
            }
        } else if (HRSActivity.mHrmValue > 850) {
            if(setOne == 0){
                mode = "Highlight";
                popupButton.setImageResource(R.drawable.highlight_yellow_icon);
                //Snackbar.make(findViewById(android.R.id.content), mode, Snackbar.LENGTH_SHORT).show();
            } else if(setOne == 1){
                mode = "Highlight Blue";
                popupButton.setImageResource(R.drawable.highlight_blue_icon);
            } else if(setOne == 2){
                mode = "Highlight Green";
                popupButton.setImageResource(R.drawable.highlight_green_icon);
            } else if(setOne == 3){
                mode = "Bold";
                popupButton.setImageResource(R.drawable.bold_icon);
            } else if(setOne == 4){
                mode = "Italic";
                popupButton.setImageResource(R.drawable.italic_icon_new);
            } else if(setOne == 5){
                mode = "Delete";
                popupButton.setImageResource(R.drawable.eraser_icon);
            } else if(setOne == 6){
                mode = "Select";
                popupButton.setImageResource(R.drawable.selection_icon);
            } else if(setOne == 7){
                mode = "Underline";
                popupButton.setImageResource(R.drawable.underline_icon_new);
            }
        }
    }

    //Pop up menu method for selecting colours
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showPopup(final View v) {
        final IconizedMenu popup = new IconizedMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnDismissListener(new IconizedMenu.OnDismissListener() {
            @Override
            public void onDismiss(IconizedMenu menu) {
                count = 0;

                if (noteTitleText.hasSelection()) {
                    noteTitleText.clearFocus();
                } else if (noteContentText.hasSelection()) {
                    noteContentText.clearFocus();
                }
            }
        });

        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Snackbar.make(v, "You Chose : " + item.getTitle(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                if (item.getTitle().equals("Green")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.green_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.green_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Light Green")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_green_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_green_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }

                } else if (item.getTitle().equals("Red")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.red_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.red_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }

                } else if (item.getTitle().equals("Light Red")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_red_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_red_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Blue")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.blue_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.blue_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Light Blue")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_blue_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.light_blue_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Orange")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Yellow")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.yellow_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.yellow_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Pink")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.pink_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.pink_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Purple")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.purple_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.purple_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("Black")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.black_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.black_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                } else if (item.getTitle().equals("White")) {
                    if (noteTitleText.hasSelection()) {
                        int startSelection = noteTitleText.getSelectionStart();
                        int endSelection = noteTitleText.getSelectionEnd();

                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                        BackgroundColorSpan[] bgSpan = ssbtitle.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbtitle.removeSpan(bgSpan[i]);
                        }
                        ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                        ssbtitle.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white_circle)), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

                    } else if (noteContentText.hasSelection()) {
                        int startSelection = noteContentText.getSelectionStart();
                        int endSelection = noteContentText.getSelectionEnd();

                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                        BackgroundColorSpan[] bgSpan = ssbcontent.getSpans(startSelection, endSelection, BackgroundColorSpan.class);
                        for (int i = 0; i < bgSpan.length; i++) {
                            ssbcontent.removeSpan(bgSpan[i]);
                        }
                        ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                        ssbcontent.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.white_circle)), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);

                    }
                }
                //popup.dismiss();
                count = 0;

                if (noteTitleText.hasSelection()) {
                    noteTitleText.clearFocus();
                } else if (noteContentText.hasSelection()) {
                    noteContentText.clearFocus();
                }

                return true;
            }
        });

        popup.show();
    }

    //Thread Methods
    public void threadOne() {

        //rerun the thread
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(25);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                tv.setText(String.valueOf(HRSActivity.mHrmValue));
                                checkScribaValue();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        t.start();

    }

    public void threadTwo() {

        myThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(25);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                valTv2.setText(String.valueOf(HRSActivity.mHrmValue));
                                formattingText();
                                deselectText();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        myThread.start();

    }

   /* public void threadThree() {

        deleteThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    deleteText();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        deleteThread.start();

    }*/


    //function methods
    public void formattingText() {
        if (mode.equals("Delete")) {
            //threadThree();
            try {
                deleteText();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (mode.equals("Underline")) {
            underlineText();
        } else if (mode.equals("Highlight")) {
            highlightText();
            /*if (count == 0) {
                showPopup(popupButton);
            }
            count++;*/
        } else if (mode.equals("Highlight Blue")) {
            highlightTextBlue();
        } else if (mode.equals("Highlight Green")) {
            highlightTextGreen();
        } else if (mode.equals("Bold")){
            boldText();
        } else if (mode.equals("Italic")){
            italicText();
        }
    }

    public void deleteText() throws InterruptedException {
        //int startSel = noteTitleText.getSelectionStart();
        //int endSel = noteTitleText.getSelectionEnd();

        try{
            if (noteTitleText.hasSelection()) {
                int startSelection = noteTitleText.getSelectionStart();
                int endSelection = noteTitleText.getSelectionEnd();
                myThread.sleep(500);
                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                ssbtitle.delete(startSelection, endSelection);
            } else if (noteContentText.hasSelection()) {
                int startSelection = noteContentText.getSelectionStart();
                int endSelection = noteContentText.getSelectionEnd();
                myThread.sleep(500);
                ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                ssbcontent.delete(startSelection, endSelection);
            }
        } catch(IndexOutOfBoundsException e){
            //e.printStackTrace();
        }
    }

    public void underlineText() {
        try{
            if(noteTitleText.hasSelection()) {
                int startSelection = noteTitleText.getSelectionStart();
                int endSelection = noteTitleText.getSelectionEnd();

                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();

                UnderlineSpan[] ulSpan = ssbtitle.getSpans(startSelection, endSelection, UnderlineSpan.class);
                for (int i = 0; i < ulSpan.length; i++) {
                    ssbtitle.removeSpan(ulSpan[i]);
                }

                ssbtitle.setSpan(new UnderlineSpan(), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
            } else if(noteContentText.hasSelection()){
                int startSelection = noteContentText.getSelectionStart();
                int endSelection = noteContentText.getSelectionEnd();

                ssbcontent = (SpannableStringBuilder) noteContentText.getText();

                UnderlineSpan[] ulSpan = ssbcontent.getSpans(startSelection, endSelection, UnderlineSpan.class);
                for (int i = 0; i < ulSpan.length; i++) {
                    ssbcontent.removeSpan(ulSpan[i]);
                }

                ssbcontent.setSpan(new UnderlineSpan(), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
            }
        } catch (IndexOutOfBoundsException e){
            //e.printStackTrace();
        }
    }

    public void highlightText() {
            try {
                if(noteTitleText.hasSelection()) {
                    ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                    ssbtitle.setSpan(new BackgroundColorSpan(Color.YELLOW), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
                } else if(noteContentText.hasSelection()) {
                    ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                    ssbcontent.setSpan(new BackgroundColorSpan(Color.YELLOW), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
                }
            } catch (IndexOutOfBoundsException e) {
                //e.printStackTrace();
            }
    }

    public void highlightTextBlue() {
        try {
            if(noteTitleText.hasSelection()) {
                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                ssbtitle.setSpan(new BackgroundColorSpan(Color.CYAN), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
            } else if(noteContentText.hasSelection()) {
                ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                ssbcontent.setSpan(new BackgroundColorSpan(Color.CYAN), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
            }
        } catch (IndexOutOfBoundsException e) {
            //e.printStackTrace();
        }
    }

    public void highlightTextGreen() {
        try {
            if(noteTitleText.hasSelection()) {
                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                ssbtitle.setSpan(new BackgroundColorSpan(Color.GREEN), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);
            } else if(noteContentText.hasSelection()) {
                ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                ssbcontent.setSpan(new BackgroundColorSpan(Color.GREEN), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
            }
        } catch (IndexOutOfBoundsException e) {
            //e.printStackTrace();
        }
    }

    public void deselectText() {
        if (Float.valueOf(valTv2.getText().toString()) < 50) {
            threadOne();
            myThread.interrupt();

            if (noteTitleText.hasSelection()) {
                noteTitleText.clearFocus();
            } else if (noteContentText.hasSelection()) {
                noteContentText.clearFocus();
            }

            modeCounter = 0;

            //Toast.makeText(getApplicationContext(), "Out of Mode", Toast.LENGTH_SHORT).show();
        }
    }

    public void boldText(){
        try {
            if (noteTitleText.hasSelection()) {
                int startSelection = noteTitleText.getSelectionStart();
                int endSelection = noteTitleText.getSelectionEnd();

                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                StyleSpan[] ss = ssbtitle.getSpans(startSelection, endSelection, StyleSpan.class);

                for (int i = 0; i < ss.length; i++) {
                    if (ss[i].getStyle() == Typeface.BOLD) {
                        ssbtitle.removeSpan(ss[i]);
                    }
                }
                ssbtitle.setSpan(new StyleSpan(Typeface.BOLD), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

            } else if (noteContentText.hasSelection()) {
                int startSelection = noteContentText.getSelectionStart();
                int endSelection = noteContentText.getSelectionEnd();

                ssbcontent = (SpannableStringBuilder) noteContentText.getText();
                StyleSpan[] ss = ssbcontent.getSpans(startSelection, endSelection, StyleSpan.class);

                for (int i = 0; i < ss.length; i++) {
                    if (ss[i].getStyle() == Typeface.BOLD) {
                        ssbcontent.removeSpan(ss[i]);
                    }
                }
                ssbcontent.setSpan(new StyleSpan(Typeface.BOLD), noteContentText.getSelectionStart(), noteContentText.getSelectionEnd(), 0);
            }
        } catch (IndexOutOfBoundsException e) {
            //e.printStackTrace();
        }

    }

    public void italicText(){
        try{
            if(noteTitleText.hasSelection()) {
                int startSelection = noteTitleText.getSelectionStart();
                int endSelection = noteTitleText.getSelectionEnd();

                ssbtitle = (SpannableStringBuilder) noteTitleText.getText();
                StyleSpan[] ss = ssbtitle.getSpans(startSelection, endSelection, StyleSpan.class);

                for (int i = 0; i < ss.length; i++) {
                    if (ss[i].getStyle() == Typeface.ITALIC) {
                        ssbtitle.removeSpan(ss[i]);
                    }
                }

                ssbtitle.setSpan(new StyleSpan(Typeface.ITALIC), noteTitleText.getSelectionStart(), noteTitleText.getSelectionEnd(), 0);

            } else if(noteContentText.hasSelection()){
                int startSelection = noteContentText.getSelectionStart();
                int endSelection = noteContentText.getSelectionEnd();

                ssbcontent=(SpannableStringBuilder)noteContentText.getText();
                StyleSpan[] ss = ssbcontent.getSpans(startSelection, endSelection, StyleSpan.class);

                for (int i = 0; i < ss.length; i++) {
                    if (ss[i].getStyle() == Typeface.ITALIC) {
                        ssbcontent.removeSpan(ss[i]);
                    }
                }

                ssbcontent.setSpan(new StyleSpan(Typeface.ITALIC),noteContentText.getSelectionStart(),noteContentText.getSelectionEnd(),0);

            }
        } catch (IndexOutOfBoundsException e){
            //e.printStackTrace();
        }
    }

    //Contextual Action Mode Methods
    /**
     * Shows or hides the copy/paste tags when text is selected, onActionItemClicked must be always false if you want the
     * copy/paste functions to work even if they don't show. If it's true then the app won't be able to copy/paste and you'll
     * have to implement it manually to the app yourself.
     **/
    public void tags(final boolean tag) {

        noteTitleText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item1 = menu.findItem(android.R.id.selectAll);
                MenuItem item2 = menu.findItem(android.R.id.cut);
                MenuItem item3 = menu.findItem(android.R.id.copy);
                item1.setVisible(false);
                item2.setVisible(false);
                item3.setVisible(false);

                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

                // If it does contain data, decide if you can handle the data.
                if (clipboard.hasPrimaryClip()) {
                    MenuItem item4 = menu.findItem(android.R.id.paste);
                    item4.setVisible(false);
                }

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    MenuItem item5 = menu.findItem(android.R.id.shareText);
                    item5.setVisible(false);
                }

                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mActionMode == null) {
                    mActionMode = mode;

                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        menu.clear();
                        mode.setTitle("Edit Note");
                        mode.getMenuInflater().inflate(R.menu.edit_note, menu);
                    }
                }

                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        noteContentText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item1 = menu.findItem(android.R.id.selectAll);
                MenuItem item2 = menu.findItem(android.R.id.cut);
                MenuItem item3 = menu.findItem(android.R.id.copy);
                item1.setVisible(false);
                item2.setVisible(false);
                item3.setVisible(false);

                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

                // If it does contain data, decide if you can handle the data.
                if (clipboard.hasPrimaryClip()) {
                    MenuItem item4 = menu.findItem(android.R.id.paste);
                    item4.setVisible(false);
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    MenuItem item5 = menu.findItem(android.R.id.shareText);
                    item5.setVisible(false);
                }

                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mActionMode == null) {
                    mActionMode = mode;

                    //menu.removeItem(android.R.id.selectAll);
                    //menu.add(0, R.id.clear1, 1, "Clear Format");
                    // Remove the default menu items (select all, copy, paste, search)
                    //menu.clear();

                    // Inflate your own menu items
                    //mode.getMenuInflater().inflate(R.menu.blank_menu, menu);

                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        menu.clear();
                        mode.setTitle("Edit Note");
                        mode.getMenuInflater().inflate(R.menu.edit_note, menu);
                    }
                }

                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

    }

    public void tagsTwo(final boolean tag) {

        noteTitleText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mActionMode == null) {
                    mActionMode = mode;

                    //menu.add(0, android.R.id.paste, 3, "Paste");
                    //menu.removeItem(android.R.id.shareText);
                    //menu.removeItem(android.R.id.selectAll);
                    // Inflate your own menu items

                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        mode.setTitle("Edit Note");
                        mode.getMenuInflater().inflate(R.menu.my_custom_menu, menu);
                    }
                }

                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        noteContentText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mActionMode == null) {
                    mActionMode = mode;
                    //menu.add(0, android.R.id.paste, 3, "Paste");
                    //menu.removeItem(android.R.id.shareText);
                    //menu.removeItem(android.R.id.selectAll);
                    // Inflate your own menu items
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        mode.setTitle("Edit Note");
                        mode.getMenuInflater().inflate(R.menu.my_custom_menu, menu);
                    }
                }

                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

    }

    //Touch Listener
    public void touchListen(){
        noteTitleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mode.equals("Highlight") || mode.equals("Highlight Blue") || mode.equals("Highlight Green") || mode.equals("Bold") || mode.equals("Italic") || mode.equals("Underline") || mode.equals("Delete")) {
                    tagsThree(false);
                    if(modeCounter == 0 && noteTitleText.hasSelection()) {
                        threadTwo();
                        t.interrupt();
                        modeCounter++;
                        Toast.makeText(getBaseContext(), mode, Toast.LENGTH_SHORT).show();
                    }
                } else if (mode.equals("Select")){
                    tagsTwo(true);
                }
                return false;
            }
        });

        noteContentText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mode.equals("Highlight") || mode.equals("Highlight Blue") || mode.equals("Highlight Green") || mode.equals("Bold") || mode.equals("Italic") || mode.equals("Underline") || mode.equals("Delete")) {
                    tagsThree(false);
                    if(modeCounter == 0 && noteContentText.hasSelection()) {
                        threadTwo();
                        t.interrupt();
                        modeCounter++;
                        Toast.makeText(getBaseContext(), mode, Toast.LENGTH_SHORT).show();
                    }
                } else if (mode.equals("Select")){
                    tagsTwo(true);
                }
                return false;
            }
        });
    }

    public void tagsThree(final boolean tag) {

        noteTitleText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        noteContentText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return tag;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

    }

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            if (mActionMode == null) {
                mActionMode = mode;

                //menu.add(0, android.R.id.paste, 3, "Paste");
                //menu.removeItem(android.R.id.shareText);
                //menu.removeItem(android.R.id.selectAll);
                // Inflate your own menu items

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    mode.getMenuInflater().inflate(R.menu.my_custom_menu, menu);
                }
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            return true;
        }

    }

}


