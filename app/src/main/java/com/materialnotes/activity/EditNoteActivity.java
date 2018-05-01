package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Actividad para editar notas.
 *
 * @author Daniel Pedraza Arcega
 */
@ContentView(R.layout.activity_edit_note)
public class EditNoteActivity extends RoboActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

    @InjectView(R.id.note_title)   private EditText noteTitleText;
    @InjectView(R.id.note_content) private EditText noteContentText;

    private Note note;

    /**
     * Construye el Intent para llamar a esta actividad con una nota ya existente.
     *
     * @param context el contexto que la llama.
     * @param note la nota a editar.
     * @return un Intent.
     */
    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    /**
     * Construye el Intent para llamar a esta actividad para crear una nota.
     *
     * @param context el contexto que la llama.
     * @return un Intent.
     */
    public static Intent buildIntent(Context context) {
        return buildIntent(context, null);
    }

    /**
     * Recupera la nota editada.
     *
     * @param intent el Intent que vine en onActivityResult
     * @return la nota actualizada
     */
    public static Note getExtraNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_NOTE);
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// Inicializa los componentes //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Muestra la flecha hacia atrás
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // Recuperar la nota del Intent
        if (note != null) { // Editar nota existente
            noteTitleText.setText(note.getTitle());
            noteContentText.setText(note.getContent());
        } else { // Nueva nota
            note = new Note();
            note.setCreatedAt(new Date());
        }
        noteContentText.setCustomSelectionActionModeCallback(mActionModeCallback);

        // Bible dictionary with OSIS codes
        Map<String, String> map = new HashMap<String, String>();
        map.put("Genesis", "GEN");
        map.put("Exodus", "EXO");
        map.put("Leviticus", "LEV");
        map.put("Numbers", "NUM");
        map.put("Deuteronomy", "DEU");
        map.put("Joshua", "JOS");
        map.put("Judges", "JDG");
        map.put("Ruth", "RUT");
        map.put("1 Samuel", "1SA");
        map.put("2 Samuel", "2SA");
        map.put("1 Kings", "1KI");
        map.put("2 Kings", "2KI");
        map.put("1 Chronicles", "1CH");
        map.put("2 Chronicles", "2CH");
        map.put("Ezra", "EZR");
        map.put("Nehemiah", "NEH");
        map.put("Esther", "EST");
        map.put("Job", "JOB");
        map.put("Psalms", "PSA");
        map.put("Proverbs", "PRO");
        map.put("Ecclesiastes", "ECC");
        map.put("Song of Solomon", "SNG");
        map.put("Isaiah", "ISA");
        map.put("Jeremiah", "JER");
        map.put("Lamentations", "LAM");
        map.put("Ezekiel", "EZK");
        map.put("Daniel", "DAN");
        map.put("Hosea", "HOS");
        map.put("Joel", "JOL");
        map.put("Amos", "AMO");
        map.put("Obadiah", "OBA");
        map.put("Jonah", "JON");
        map.put("Micah", "MIC");
        map.put("Nahum", "NAM");
        map.put("Habakkuk", "HAB");
        map.put("Zephaniah", "ZEP");
        map.put("Haggai", "HAG");
        map.put("Zechariah", "ZEC");
        map.put("Malachi", "MAL");
        map.put("Matthew", "MAT");
        map.put("Mark", "MRK");
        map.put("Luke", "LUK");
        map.put("John", "JHN");
        map.put("Acts", "ACT");
        map.put("Romans", "ROM");
        map.put("1 Corinthians", "1CO");
        map.put("2 Corinthians", "2CO");
        map.put("Galatians", "GAL");
        map.put("Ephesians", "EPH");
        map.put("Philippians", "PHP");
        map.put("Colossians", "COL");
        map.put("1 Thessalonians", "1TH");
        map.put("2 Thessalonians", "2TH");
        map.put("1 Timothy", "1TI");
        map.put("2 Timothy", "2TI");
        map.put("Titus", "TIT");
        map.put("Philemon", "PHM");
        map.put("Hebrews", "HEB");
        map.put("James", "JAS");
        map.put("1 Peter", "1PE");
        map.put("2 Peter", "2PE");
        map.put("1 John", "1JN");
        map.put("2 John", "2JN");
        map.put("3 John", "3JN");
        map.put("Jude", "JUD");
        map.put("Revelation", "REV");
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.edit_note_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_bible:
                    openBible();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
        }
    };

    private void openBible(){
        CharSequence selectedText =  noteContentText.getText().subSequence(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd());

        // TODO: parse selectedText to appropriate reference string eg 1 Corinthians 13:4-5 to 1CO.13.4-5
        selectedText = "https://www.bible.com/en-GB/bible/1/1CO.13.4-5" ;

        // Build the intent
        String url = selectedText.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        // Verify there's at least one app installed that can handle the intent
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    /** {@inheritDoc} */
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
            default: return super.onOptionsItemSelected(item);
        }
    }

    /** @return {@code true} si tiene titulo y contenido; {@code false} en cualquier otro caso. */
    private boolean isNoteFormOk() {
        return !Strings.isNullOrBlank(noteTitleText.getText().toString()) && !Strings.isNullOrBlank(noteContentText.getText().toString());
    }

    /**
     * Actualiza el contenido del objeto Note con los campos de texto del layout y pone el objeto
     * como resultado de esta actividad.
     */
    private void setNoteResult() {
        note.setTitle(noteTitleText.getText().toString().trim());
        note.setContent(noteContentText.getText().toString().trim());
        note.setUpdatedAt(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
    }

    /** Muestra mensajes de validación de la forma de la nota. */
    private void validateNoteForm() {
        StringBuilder message = null;
        if (Strings.isNullOrBlank(noteTitleText.getText().toString())) {
            message = new StringBuilder().append(getString(R.string.title_required));
        }
        if (Strings.isNullOrBlank(noteContentText.getText().toString())) {
            if (message == null) message = new StringBuilder().append(getString(R.string.content_required));
            else message.append("\n").append(getString(R.string.content_required));
        }
        if (message != null) {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBackPressed() {
        // No se edito ningúna nota ni creo alguna nota
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}