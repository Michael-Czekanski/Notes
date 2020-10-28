package com.crimsonbeet.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChangePasswordDialogFragment extends DialogFragment {
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextRepeatNewPassword;

    private ChangePasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_changepassword, null);

        editTextOldPassword = view.findViewById(R.id.old_password_dialog_changepassword);
        editTextNewPassword = view.findViewById(R.id.new_password_dialog_changepassword);
        editTextRepeatNewPassword = view.findViewById(R.id.repeat_new_password_dialog_changepassword);

        builder.setView(view);
        builder.setTitle(R.string.dialogTitle_changePassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.changePassword(
                        editTextOldPassword.getText().toString(),
                        editTextNewPassword.getText().toString(),
                        editTextRepeatNewPassword.getText().toString());
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            listener = (ChangePasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (context.toString() + "must implement " + ChangePasswordDialogListener.class.toString());
        }
        super.onAttach(context);
    }
}
