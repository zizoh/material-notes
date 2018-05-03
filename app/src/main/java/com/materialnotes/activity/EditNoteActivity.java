package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @InjectView(R.id.note_title)
    private EditText noteTitleText;
    @InjectView(R.id.note_content)
    private EditText noteContentText;

    private Note note;

    /**
     * Construye el Intent para llamar a esta actividad con una nota ya existente.
     *
     * @param context el contexto que la llama.
     * @param note    la nota a editar.
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

    /**
     * {@inheritDoc}
     */
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

    private void openBible() {
        CharSequence selectedText = noteContentText.getText().subSequence(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd());

        // TODO: parse selectedText to appropriate reference string eg 1 Corinthians 13:4-5 to 1CO.13.4-5
        String bookPrefix = "";
        String bookVerse = "";
        String selectectedTextString = selectedText.toString();

        String selectectedTextStringWithoutSpaces = selectectedTextString.toLowerCase().replace(" ", "");
        String selectectedTextStringWithoutPrefix = "";
        if (bookHasPrefix(selectectedTextStringWithoutSpaces)) {
            bookPrefix = getPrefix(selectectedTextStringWithoutSpaces);
            selectectedTextStringWithoutPrefix = removePrefix(selectectedTextStringWithoutSpaces);
        }
        String bookName = "";
        bookName = getBookname(selectectedTextStringWithoutPrefix);
        bookVerse = getVerse(selectectedTextStringWithoutPrefix);

        // Bible dictionary with OSIS codes
        Map<String, String> bookMap = new HashMap<String, String>();
        bookMap.put("Genesis", "GEN");
        bookMap.put("Exodus", "EXO");
        bookMap.put("Leviticus", "LEV");
        bookMap.put("Numbers", "NUM");
        bookMap.put("Deuteronomy", "DEU");
        bookMap.put("Joshua", "JOS");
        bookMap.put("Judges", "JDG");
        bookMap.put("Ruth", "RUT");
        bookMap.put("1 Samuel", "1SA");
        bookMap.put("2 Samuel", "2SA");
        bookMap.put("1 Kings", "1KI");
        bookMap.put("2 Kings", "2KI");
        bookMap.put("1 Chronicles", "1CH");
        bookMap.put("2 Chronicles", "2CH");
        bookMap.put("Ezra", "EZR");
        bookMap.put("Nehemiah", "NEH");
        bookMap.put("Esther", "EST");
        bookMap.put("Job", "JOB");
        bookMap.put("Psalms", "PSA");
        bookMap.put("Proverbs", "PRO");
        bookMap.put("Ecclesiastes", "ECC");
        bookMap.put("Song of Solomon", "SNG");
        bookMap.put("Isaiah", "ISA");
        bookMap.put("Jeremiah", "JER");
        bookMap.put("Lamentations", "LAM");
        bookMap.put("Ezekiel", "EZK");
        bookMap.put("Daniel", "DAN");
        bookMap.put("Hosea", "HOS");
        bookMap.put("Joel", "JOL");
        bookMap.put("Amos", "AMO");
        bookMap.put("Obadiah", "OBA");
        bookMap.put("Jonah", "JON");
        bookMap.put("Micah", "MIC");
        bookMap.put("Nahum", "NAM");
        bookMap.put("Habakkuk", "HAB");
        bookMap.put("Zephaniah", "ZEP");
        bookMap.put("Haggai", "HAG");
        bookMap.put("Zechariah", "ZEC");
        bookMap.put("Malachi", "MAL");
        bookMap.put("Matthew", "MAT");
        bookMap.put("Mark", "MRK");
        bookMap.put("Luke", "LUK");
        bookMap.put("John", "JHN");
        bookMap.put("Acts", "ACT");
        bookMap.put("Romans", "ROM");
        bookMap.put("1 Corinthians", "1CO");
        bookMap.put("2 Corinthians", "2CO");
        bookMap.put("Galatians", "GAL");
        bookMap.put("Ephesians", "EPH");
        bookMap.put("Philippians", "PHP");
        bookMap.put("Colossians", "COL");
        bookMap.put("1 Thessalonians", "1TH");
        bookMap.put("2 Thessalonians", "2TH");
        bookMap.put("1 Timothy", "1TI");
        bookMap.put("2 Timothy", "2TI");
        bookMap.put("Titus", "TIT");
        bookMap.put("Philemon", "PHM");
        bookMap.put("Hebrews", "HEB");
        bookMap.put("James", "JAS");
        bookMap.put("1 Peter", "1PE");
        bookMap.put("2 Peter", "2PE");
        bookMap.put("1 John", "1JN");
        bookMap.put("2 John", "2JN");
        bookMap.put("3 John", "3JN");
        bookMap.put("Jude", "JUD");
        bookMap.put("Revelation", "REV");

        String bookMapKey = "";
        if (bookPrefix.isEmpty()) {
            bookMapKey = bookName;
        } else {
            bookMapKey = bookPrefix + " " + bookName;
        }
        String bookMapValue = "";
        bookMapValue = bookMap.get(bookMapKey);

        // Build the intent
        // https://www.bible.com/en-GB/bible/1/1CO.13.4-5
        String url = "https://www.bible.com/en-GB/bible/1/" + bookMapValue + "." + bookVerse;
        //String url = "youversion://bible?reference=" + bookMapValue + "." + bookVerse;

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

    @NonNull
    private String removePrefix(String selectectedTextStringWithoutPrefix) {
        selectectedTextStringWithoutPrefix = selectectedTextStringWithoutPrefix.substring(1);
        if (selectectedTextStringWithoutPrefix.substring(0, 2).equals("ii")) {
            selectectedTextStringWithoutPrefix = selectectedTextStringWithoutPrefix.substring(2);
        } else if (selectectedTextStringWithoutPrefix.substring(0, 1).equals("i")) {
            selectectedTextStringWithoutPrefix = selectectedTextStringWithoutPrefix.substring(1);
        }
        return selectectedTextStringWithoutPrefix;
    }

    private String getBookname(String givenText) {
        String bookName;
        // Remove integers
        bookName = givenText.replaceAll("\\d", "");
        // Remove colon
        bookName = bookName.replaceAll(":", "");
        // Capitalize first letter of book name
        bookName = bookName.substring(0, 1).toUpperCase() + bookName.substring(1);
        if (bookName.contains("Song")) {
            // For when users enter Songs of Solomon, Song of Songs or Songs of Songs
            bookName = "Song of Solomon";
        }
        return bookName;
    }

    private String getVerse(String givenText) {
        String verse;
        verse = givenText.replaceAll("[a-zA-Z]*", "");
        verse = verse.replaceAll(":", ".");
        return verse;
    }

    private String getPrefix(String givenText) {
        String prefix;
        prefix = givenText.substring(0, 1);
        if (givenText.substring(0, 3).equals("iii")) {
            prefix = "3";
            return prefix;
        } else if (givenText.substring(0, 2).equals("ii")) {
            prefix = "2";
            return prefix;
        } else if (givenText.substring(0, 1).equals("i")) {
            prefix = "1";
            return prefix;
        }
        return prefix;
    }

    public boolean bookHasPrefix(String book) {
        if (book.startsWith("1") || book.startsWith("2") || book.startsWith("3")
                || book.startsWith("i") || book.startsWith("ii") || book.startsWith("iii")) {
            if (!book.contains("isaiah")) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
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
     * @return {@code true} si tiene titulo y contenido; {@code false} en cualquier otro caso.
     */
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

    /**
     * Muestra mensajes de validación de la forma de la nota.
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
        // No se edito ningúna nota ni creo alguna nota
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}