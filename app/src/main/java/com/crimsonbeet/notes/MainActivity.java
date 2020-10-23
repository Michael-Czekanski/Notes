package com.crimsonbeet.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.preference.*;

public class MainActivity extends AppCompatActivity implements SetPasswordDialogListener {

    private boolean firstLaunch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefsEditor = sharedPreferences.edit();

        String keyFirstLaunch = getResources().getString(R.string.sharedPrefsKey_firstLaunch);
        firstLaunch = sharedPreferences.getBoolean(keyFirstLaunch, true);

        if(firstLaunch){
            handleFirstLaunch();
        }
        else{

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    private void handleFirstLaunch() {
        showSetPasswordDialog();
    }

    private void initialize() {
        // TODO: Implement
    }

    private void showSetPasswordDialog() {
        SetPasswordDialogFragment setPasswordDialog = new SetPasswordDialogFragment();
        setPasswordDialog.show(getSupportFragmentManager(),
                getResources().getString(R.string.tag_setPassword));
    }

    @Override
    public void setPassword(String password, String repeatedPassword) {
        if(!password.isEmpty() && password.equals(repeatedPassword)){
            saveUserPassword(password);
            saveFirstLaunchFalse();
            showPasswordSetDialog();
        }
        else{
            if(password.isEmpty()){
                showProvidePasswordDialog();
            }
            else{
                showPasswordsNotMatchDialog();
            }
        }
    }

    private void saveFirstLaunchFalse() {
        String keyFirstLaunch = getResources().getString(R.string.sharedPrefsKey_firstLaunch);

        sharedPrefsEditor.putBoolean(keyFirstLaunch, false);
        sharedPrefsEditor.apply();
    }

    private void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_passwordSet);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                initialize();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void setPasswordDialogDismissed() {
        showSetPasswordDialog();
    }


    private void showProvidePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogMsg_providePassword);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showSetPasswordDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveUserPassword(String password) {
        String keyPassword = getResources().getString(R.string.sharedPrefsKey_password);

        sharedPrefsEditor.putString(keyPassword, password);
        sharedPrefsEditor.apply();
    }

    private void showPasswordsNotMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialogMsg_passwordsNotMatch));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showSetPasswordDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
