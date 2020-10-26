package com.crimsonbeet.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.crimsonbeet.notes.models.Note;
import com.crimsonbeet.notes.utils.JsonManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SetPasswordDialogListener,
        NewNoteDialogListener {

    public static final String NOTE_TITLE = "com.crimsonbeet.notes.NOTE_TITLE";
    public static final int MIN_NOTE_ID = 1;

    private boolean firstLaunch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;

    private JsonManager jsonManager;

    private ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefsEditor = sharedPreferences.edit();

        jsonManager = new JsonManager();
        String keyFirstLaunch = getResources().getString(R.string.sharedPrefsKey_firstLaunch);
        firstLaunch = sharedPreferences.getBoolean(keyFirstLaunch, true);

        if (firstLaunch) {
            handleFirstLaunch();
        } else {
            normalLaunch();
        }

    }

    private void normalLaunch() {
        // TODO: Implement
        checkPassword();
        try {
            notes = readAllNotes();
        } catch (IOException e) {
            handleReadingNotesError();
        }
        visualizeNotes();
        throw new UnsupportedOperationException();

    }

    private void handleReadingNotesError() {
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    private void visualizeNotes() {
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    /**
     * Reads all notes.
     *
     * @throws IOException
     */
    private ArrayList<Note> readAllNotes() throws IOException {
        ArrayList<Note> notes = new ArrayList<>();

        String sharedPrefsKey = getResources().getString(R.string.sharedPrefsKey_lastNoteId);
        int lastNoteId = sharedPreferences.getInt(sharedPrefsKey, 0);

        for (int noteId = MIN_NOTE_ID; noteId <= lastNoteId; noteId++) {
            try {
                notes.add(jsonManager.readNoteFromJson(noteId));
            } catch (FileNotFoundException ignored) {
            } catch (IOException e1) {
                throw new IOException("Error reading note, noteId = " + noteId);
            }
        }
        return notes;
    }

    private void checkPassword() {
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_note:
                showNewNoteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewNoteDialog() {
        NewNoteDialogFragment noteDialogFragment = new NewNoteDialogFragment();
        noteDialogFragment.show(getSupportFragmentManager(),
                getResources().getString(R.string.dialogTitle_newNote));
    }

    private void handleFirstLaunch() {
        showSetPasswordDialog();
    }

    private void initialize() {
        // TODO: Implement
    }

    private void showSetPasswordDialog() {
        SetPasswordDialogFragment setPasswordDialog = new SetPasswordDialogFragment();
        setPasswordDialog.show(getSupportFragmentManager(),
                getResources().getString(R.string.tag_setPassword));
    }

    @Override
    public void setPassword(String password, String repeatedPassword) {
        if(!password.isEmpty() && password.equals(repeatedPassword)){
            saveUserPassword(password);
            saveFirstLaunchFalse();
            showPasswordSetDialog();
        }
        else{
            if(password.isEmpty()){
                showProvidePasswordDialog();
            }
            else{
                showPasswordsNotMatchDialog();
            }
        }
    }

    private void saveFirstLaunchFalse() {
        String keyFirstLaunch = getResources().getString(R.string.sharedPrefsKey_firstLaunch);

        sharedPrefsEditor.putBoolean(keyFirstLaunch, false);
        sharedPrefsEditor.apply();
    }

    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_passwordSet);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                initialize();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void setPasswordDialogDismissed() {
        showSetPasswordDialog();
    }


    private void showProvidePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_providePassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showSetPasswordDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveUserPassword(String password) {
        String keyPassword = getResources().getString(R.string.sharedPrefsKey_password);

        sharedPrefsEditor.putString(keyPassword, password);
        sharedPrefsEditor.apply();
    }

    private void showPasswordsNotMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialogMsg_passwordsNotMatch));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showSetPasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void createNewNote(String title) {
        // TODO: Implement

        int id = createNewNoteId();

        Note note = new Note(id, title, "");
        try {
            saveNote(note);
        } catch (IOException e) {
            handleNoteSaveError();
        }

        // TODO: Pass newly created Note object to NoteActivity using intent
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NOTE_TITLE, title);
        startActivity(intent);
    }

    private void handleNoteSaveError() {
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    private int createNewNoteId() {
        String sharedPrefsKey = getResources().getString(R.string.sharedPrefsKey_lastNoteId);
        int lastNoteId = sharedPreferences.getInt(sharedPrefsKey, 0);
        int newNoteId = lastNoteId + 1;

        sharedPrefsEditor.putInt(sharedPrefsKey, newNoteId);
        sharedPrefsEditor.apply();

        return newNoteId;
    }

    private void saveNote(Note note) throws IOException {
        jsonManager.writeNoteToJson(note);
    }
}
