package com.example.StepApp.SensorsAndAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class HelpDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    //help dialog file
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Registration Help")
                .setMessage("Email address should be in the following format:\n" +
                        "mail@mail.com\n\n" +
                        "Password must be at least 6 characters with at least one uppercase letter, one lowercase letter, a number and a special character")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
