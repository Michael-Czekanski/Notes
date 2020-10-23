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
 * This class is used to display dialog in which user will set password.
 */
public class SetPasswordDialogFragment extends DialogFragment {
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;
    private SetPasswordDialogListener listener;

    private boolean okButtonClicked;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        okButtonClicked = false;


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_setpassword, null);

        editTextPassword = view.findViewById(R.id.firstPassword);
        editTextRepeatPassword = view.findViewById(R.id.repeatPassword);

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        okButtonClicked = true;
                        String password = editTextPassword.getText().toString();
                        String repeatedPassword = editTextRepeatPassword.getText().toString();

                        listener.setPassword(password, repeatedPassword); }});
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SetPasswordDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (context.toString() + " must implement SetPasswordDialogListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if(!okButtonClicked){
            listener.setPasswordDialogDismissed();
        }
        else {
            okButtonClicked = false;
        }
    }
}
