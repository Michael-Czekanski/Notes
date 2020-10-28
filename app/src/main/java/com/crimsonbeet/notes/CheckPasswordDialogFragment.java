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

public class CheckPasswordDialogFragment extends DialogFragment {
    private EditText editTextPassword;
    CheckPasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_checkpassword, null);

        editTextPassword = view.findViewById(R.id.editText_password);

        builder.setView(view);
        builder.setTitle(R.string.password);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String givenPassword = editTextPassword.getText().toString();
                listener.checkPassword(givenPassword);
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {

        try {
            listener = (CheckPasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (context.toString() + "must implement " + CheckPasswordDialogListener.class.toString());
        }
        super.onAttach(context);
    }
}
