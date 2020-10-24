package com.crimsonbeet.notes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * This class is used to display dialog in which user can create new note.
 */
public class NewNoteDialogFragment extends DialogFragment {
    private EditText editTextNoteTitle;
    private NewNoteDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_newnote, null);

        editTextNoteTitle = view.findViewById(R.id.noteTitle);

        builder.setView(view);
        builder.setTitle(R.string.dialogTitle_newNote);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String noteTitle = editTextNoteTitle.getText().toString();
                listener.createNewNote(noteTitle); }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewNoteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (context.toString() + " must implement NewNoteDialogListener");
        }
    }

}
