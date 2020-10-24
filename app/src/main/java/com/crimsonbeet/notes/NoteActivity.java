package com.crimsonbeet.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    private TextView textViewNoteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        textViewNoteTitle = findViewById(R.id.textView_noteTitle);

        setNoteTitle(intent);
    }

    private void setNoteTitle(Intent intent) {
        String noteTitle = intent.getStringExtra(MainActivity.NOTE_TITLE);
        textViewNoteTitle.setText(noteTitle);
    }
}
