package com.crimsonbeet.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crimsonbeet.notes.models.Note;

public class NoteActivity extends AppCompatActivity {

    private TextView textViewNoteTitle;
    private EditText editTextNoteContent;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        textViewNoteTitle = findViewById(R.id.textView_noteTitle);
        editTextNoteContent = findViewById(R.id.editText_noteContent);

        Intent intent = getIntent();
        note = intent.getParcelableExtra(MainActivity.NOTE_PARCELABLE);

        displayNote(note);
    }

    private void displayNote(Note note) {
        textViewNoteTitle.setText(note.getTitle());
        editTextNoteContent.setText(note.getContent());
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.NOTE_PARCELABLE, note);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
