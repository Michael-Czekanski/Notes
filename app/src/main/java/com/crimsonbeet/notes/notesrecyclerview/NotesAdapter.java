package com.crimsonbeet.notes.notesrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.R;
import com.crimsonbeet.notes.models.Note;
import com.crimsonbeet.notes.notesrecyclerview.selection.NoteItemSelectedListener;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    ArrayList<Note> notes;
    private final NotesViewHolderClickListener noteViewHolderClickListener;
    private SelectionTracker<Long> selectionTracker;

    private final NoteItemSelectedListener noteItemSelectedListener;
    private int selectedNotesNum = 0;

    public NotesAdapter(ArrayList<Note> notes,
                        NotesViewHolderClickListener noteViewHolderClickListener,
                        NoteItemSelectedListener noteItemSelectedListener) {
        this.notes = notes;
        this.noteViewHolderClickListener = noteViewHolderClickListener;
        this.noteItemSelectedListener = noteItemSelectedListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewNoteItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,
                parent, false);

        return new NotesViewHolder(viewNoteItem, noteViewHolderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        boolean isSelected = false;
        if (selectionTracker != null) {
            isSelected = selectionTracker.isSelected((long) position);
        }

        holder.bind(notes.get(position), isSelected);

        int currentSelectionSize = selectionTracker.getSelection().size();
        if (selectedNotesNum != currentSelectionSize) {
            selectedNotesNum = currentSelectionSize;
            noteItemSelectedListener.noteSelectionNumChanged(currentSelectionSize);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }
}
