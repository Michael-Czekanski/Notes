package com.crimsonbeet.notes.notesrecyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.R;
import com.crimsonbeet.notes.models.Note;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    private final View viewNoteItem;
    private final TextView textViewNoteTitle;

    private Note note;

    public NotesViewHolder(@NonNull View viewNoteItem) {
        super(viewNoteItem);
        this.viewNoteItem = viewNoteItem;
        textViewNoteTitle = viewNoteItem.findViewById(R.id.item_textView_noteTitle);
    }

    /**
     * Use this method in onBindViewHolder() in Adapter
     *
     * @param note
     */
    public void bind(Note note) {
        this.note = note;
        textViewNoteTitle.setText(note.getTitle());
    }
}
