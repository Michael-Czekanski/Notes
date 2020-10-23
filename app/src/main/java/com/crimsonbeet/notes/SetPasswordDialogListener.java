package com.crimsonbeet.notes;

public interface SetPasswordDialogListener {
    void setPassword(String password, String repeatedPassword);

    void setPasswordDialogDismissed();
}
