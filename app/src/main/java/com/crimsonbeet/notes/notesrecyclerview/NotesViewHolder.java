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

    private final NotesViewHolderClickListener clickListener;

    public NotesViewHolder(@NonNull View viewNoteItem, final NotesViewHolderClickListener clickListener) {
        super(viewNoteItem);
        this.viewNoteItem = viewNoteItem;
        this.clickListener = clickListener;

        textViewNoteTitle = viewNoteItem.findViewById(R.id.item_textView_noteTitle);
        viewNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.notesViewHolderClick(note);
            }
        });
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
