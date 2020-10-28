package com.crimsonbeet.notes.notesrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.R;
import com.crimsonbeet.notes.models.Note;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    ArrayList<Note> notes;
    private final NotesViewHolderClickListener noteViewHolderClickListener;

    public NotesAdapter(ArrayList<Note> notes, NotesViewHolderClickListener noteViewHolderClickListener) {
        this.notes = notes;
        this.noteViewHolderClickListener = noteViewHolderClickListener;
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
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
