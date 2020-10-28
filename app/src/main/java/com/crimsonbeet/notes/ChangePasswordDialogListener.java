package com.crimsonbeet.notes;

public interface ChangePasswordDialogListener {
    void changePassword(String oldPassword, String newPassword, String repeatedNewPassword);
}
