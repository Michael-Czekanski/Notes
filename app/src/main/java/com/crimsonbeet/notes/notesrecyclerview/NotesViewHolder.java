package com.crimsonbeet.notes.notesrecyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.crimsonbeet.notes.R;
import com.crimsonbeet.notes.models.Note;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    private final View viewNoteItem;
    private final TextView textViewNoteTitle;

    private Note note;

    private final NotesViewHolderClickListener clickListener;

    private boolean isActivated;

    public NotesViewHolder(@NonNull View viewNoteItem, NotesViewHolderClickListener notesViewHolderClickListener) {
        super(viewNoteItem);
        this.viewNoteItem = viewNoteItem;
        this.clickListener = notesViewHolderClickListener;

        textViewNoteTitle = viewNoteItem.findViewById(R.id.item_textView_noteTitle);
        viewNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isActivated) {
                    clickListener.notesViewHolderClick(note);
                }
            }
        });
    }

    /**
     * Use this method in onBindViewHolder() in Adapter
     *
     * @param note
     */
    public void bind(Note note, boolean isActivated) {
        this.note = note;
        textViewNoteTitle.setText(note.getTitle());
        this.isActivated = isActivated;
        viewNoteItem.setActivated(isActivated);
    }

    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
        ItemDetailsLookup.ItemDetails<Long> itemDetails =
                new ItemDetailsLookup.ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return getAdapterPosition();
                    }

                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return getItemId();
                    }
                };
        return itemDetails;
    }
}
