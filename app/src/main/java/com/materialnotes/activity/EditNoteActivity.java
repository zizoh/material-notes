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
import com.materialnotes.data.BooksOfBibleCodes;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;

import java.util.Date;
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
        CharSequence selectedText = getUserSelectedText();
        String selectectedTextString = selectedText.toString();
        String selectectedTextStringWithoutSpaces = selectectedTextString.toLowerCase().replace(" ", "");

        String selectectedTextStringWithoutPrefix = "";
        String bookPrefix = "";
        if (bookHasPrefix(selectectedTextStringWithoutSpaces)) {
            bookPrefix = getPrefix(selectectedTextStringWithoutSpaces);
            selectectedTextStringWithoutPrefix = removePrefix(selectectedTextStringWithoutSpaces);
        } else {
            selectectedTextStringWithoutPrefix = selectectedTextStringWithoutSpaces;
        }
        String bookName = "";
        bookName = getBookname(selectectedTextStringWithoutPrefix);
        String bookVerse = "";
        bookVerse = getVerse(selectectedTextStringWithoutPrefix);

        String bookMapKey = "";
        if (bookPrefix.isEmpty()) {
            bookMapKey = bookName;
        } else {
            bookMapKey = bookPrefix + " " + bookName;
        }
        Map<String, String> booksOfBibleCodesMap = BooksOfBibleCodes.getBookOfBibleCodes();
        String booksOfBibleCodesMapValue = "";
        booksOfBibleCodesMapValue = booksOfBibleCodesMap.get(bookMapKey);

        // Build the intent
        // URL Format --> https://www.bible.com/en-GB/bible/1/1CO.13.4-5
        String url = "https://www.bible.com/en-GB/bible/1/" + booksOfBibleCodesMapValue + "." + bookVerse;
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

    private CharSequence getUserSelectedText() {
        return noteContentText.getText().subSequence(noteContentText.getSelectionStart(), noteContentText.getSelectionEnd());
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
        // The prefix is either 1, 2 or 3
        prefix = givenText.substring(0, 1);
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