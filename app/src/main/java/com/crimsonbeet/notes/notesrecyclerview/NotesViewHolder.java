package com.crimsonbeet.notes.notesrecyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.R;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    private final View viewNoteItem;

    public NotesViewHolder(@NonNull View viewNoteItem) {
        super(viewNoteItem);
        this.viewNoteItem = viewNoteItem;
    }

    public TextView getTextViewNoteTitle() {
        return viewNoteItem.findViewById(R.id.item_textView_noteTitle);
    }
}
