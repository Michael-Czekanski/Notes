package com.crimsonbeet.notes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.models.Note;
import com.crimsonbeet.notes.notesrecyclerview.NotesAdapter;
import com.crimsonbeet.notes.notesrecyclerview.NotesViewHolderClickListener;
import com.crimsonbeet.notes.notesrecyclerview.selection.NoteItemSelectedListener;
import com.crimsonbeet.notes.notesrecyclerview.selection.NotesDetailsLookup;
import com.crimsonbeet.notes.utils.JsonManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SetPasswordDialogListener,
        NewNoteDialogListener, CheckPasswordDialogListener, NotesViewHolderClickListener,
        ChangePasswordDialogListener, NoteItemSelectedListener {

    public static final String NOTE_PARCELABLE = "com.crimsonbeet.notes.NOTE_PARCELABLE";
    public static final int MIN_NOTE_ID = 1;

    public static final int NOTE_ACTIVITY_REQUEST_CODE = 1;

    private boolean firstLaunch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;

    private JsonManager jsonManager;

    private ArrayList<Note> notes;

    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private LinearLayoutManager layoutManager;

    private SelectionTracker<Long> selectionTracker;

    private boolean notesSelectedMenuMode = false;

    private Menu menu;

    boolean passwordGiven = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesRecyclerView = findViewById(R.id.recyclerView_notesList);
        layoutManager = new LinearLayoutManager(this);
        notesRecyclerView.setLayoutManager(layoutManager);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefsEditor = sharedPreferences.edit();

        jsonManager = new JsonManager(getFilesDir());
        String keyFirstLaunch = getResources().getString(R.string.sharedPrefsKey_firstLaunch);
        firstLaunch = sharedPreferences.getBoolean(keyFirstLaunch, true);

        if (firstLaunch) {
            handleFirstLaunch();
        } else {
            normalLaunch();
        }

    }

    private void normalLaunch() {
        showCheckPasswordDialog();
    }

    private void handleReadingNotesError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialogTitle_errorReadingNotes);
        builder.setMessage(R.string.dialogMsg_errorReadingNotes);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showCheckPasswordDialog() {
        CheckPasswordDialogFragment checkPasswordDialog = new CheckPasswordDialogFragment();
        checkPasswordDialog.show(getSupportFragmentManager(),
                getResources().getString(R.string.dialogTitle_checkPassword));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (passwordGiven) {
            switch (item.getItemId()) {
                case R.id.new_note:
                    showNewNoteDialog();
                    return true;
                case R.id.change_password:
                    showChangePasswordDialog();
                    return true;
                case R.id.delete_notes:
                    showAreYouSureToDeleteDialog();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAreYouSureToDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_if_delete_notes);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSelectedNotes(selectionTracker.getSelection());
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteSelectedNotes(Selection<Long> selection) {
        ArrayList<Note> notesToDelete = new ArrayList<>();
        for (long selectedKey : selection) {
            notesToDelete.add(notes.get((int) selectedKey));
        }


        notes.removeAll(notesToDelete);
        for (Note note : notesToDelete) {
            jsonManager.deleteNoteJsonFile(note);
        }

        notesAdapter.notifyDataSetChanged();
        selectionTracker.clearSelection();
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialogFragment changePasswordDialog = new ChangePasswordDialogFragment();
        changePasswordDialog.show(getSupportFragmentManager(),
                getResources().getString(R.string.dialogTitle_changePassword));
    }

    private void showNewNoteDialog() {
        NewNoteDialogFragment noteDialogFragment = new NewNoteDialogFragment();
        noteDialogFragment.show(getSupportFragmentManager(),
                getResources().getString(R.string.dialogTitle_newNote));
    }

    private void handleFirstLaunch() {
        showSetPasswordDialog();
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
            passwordGiven = true;
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
        int id = createNewNoteId();

        Note note = new Note(id, title, "");
        try {
            saveNote(note);
            notes.add(note);
        } catch (IOException e) {
            handleNoteSaveError();
        }

        openNoteActivity(note);
    }

    private void openNoteActivity(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NOTE_PARCELABLE, note);
        startActivityForResult(intent, NOTE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Note resultNote = data.getParcelableExtra(NOTE_PARCELABLE);
                for (Note note :
                        notes) {
                    if (note.getId() == resultNote.getId()) {
                        note.setTitle(resultNote.getTitle());
                        note.setContent(resultNote.getContent());

                        notesAdapter.notifyDataSetChanged();
                        try {
                            saveNote(note);
                        } catch (IOException e) {
                            handleNoteSaveError();
                        }
                        break;
                    }
                }
            }
        }

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

    @Override
    public void checkPassword(String password) {
        String keyPassword = getResources().getString(R.string.sharedPrefsKey_password);

        String savedPassword = sharedPreferences.getString(keyPassword, null);

        if (!password.equals(savedPassword)) {
            showWrongPasswordDialog();
        } else {
            passwordChecked();
        }
    }

    @Override
    public void checkPasswordDialogDismissed() {
        showCheckPasswordDialog();
    }

    private void passwordChecked() {
        passwordGiven = true;
        loadAllNotes();
        displayNotes();
    }

    /**
     * Displays notes on the recycler view. Use only once, after notes are loaded from json files.
     */
    private void displayNotes() {
        notesAdapter = new NotesAdapter(notes, this, this);
        notesRecyclerView.setAdapter(notesAdapter);

        toggleSelection();
    }

    private void toggleSelection() {
        SelectionTracker.Builder<Long> builder = new SelectionTracker.Builder<>(
                getResources().getString(R.string.id_notes_selection),
                notesRecyclerView,
                new StableIdKeyProvider(notesRecyclerView),
                new NotesDetailsLookup(notesRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate
                (SelectionPredicates.<Long>createSelectAnything());

        selectionTracker = builder.build();

        notesAdapter.setSelectionTracker(selectionTracker);
    }

    private void loadAllNotes() {
        try {
            notes = readAllNotes();
        } catch (IOException e) {
            handleReadingNotesError();
        }
    }

    private void showWrongPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_wrongPassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showCheckPasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void notesViewHolderClick(Note note) {
        openNoteActivity(note);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String repeatedNewPassword) {
        String keyPassword = getResources().getString(R.string.sharedPrefsKey_password);

        String savedPassword = sharedPreferences.getString(keyPassword, null);

        if (!oldPassword.equals(savedPassword)) {
            showWrongOldPasswordDialog();
        } else {
            if (newPassword.isEmpty()) {
                showProvideNewPasswordDialog();
            } else if (!newPassword.equals(repeatedNewPassword)) {
                showNewPasswordsNotMatchDialog();
            } else {
                showPasswordChangedDialog();
                saveUserPassword(newPassword);
            }
        }
    }

    private void showPasswordChangedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_passwordChanged);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNewPasswordsNotMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_newPasswordsNotMatch);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showChangePasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProvideNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_provideNewPassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showChangePasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showWrongOldPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_wrongOldPassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showChangePasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void noteSelectionNumChanged(int selectionNum) {
        if (selectionNum > 0) {
            if (!notesSelectedMenuMode) {
                toggleNotesSelectedMenuMode(true);
            }
        }
        if (selectionNum == 0) {
            toggleNotesSelectedMenuMode(false);
        }
    }

    private void toggleNotesSelectedMenuMode(boolean toggle) {
        if (toggle) {
            displayNotesSelectedMenu();
        } else {
            closeNotesSelectedMenuMode();
        }
        notesSelectedMenuMode = toggle;

    }

    private void closeNotesSelectedMenuMode() {
        menu.clear();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
    }

    private void displayNotesSelectedMenu() {
        menu.clear();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.multi_selection_menu, menu);

    }

    @Override
    public void onBackPressed() {
        if (notesSelectedMenuMode) {
            selectionTracker.clearSelection();
        } else {
            super.onBackPressed();
        }
    }
}
