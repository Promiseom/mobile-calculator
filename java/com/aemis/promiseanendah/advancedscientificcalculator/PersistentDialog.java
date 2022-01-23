package com.aemis.promiseanendah.advancedscientificcalculator;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

/**
 * PersistentDialog is a dialog that displays information to the user, this is different from other
 * dialogs because, this dialog is persistent because it shows up very time the app is launched,
 * you can however stop the dialog from popping up every launch.
 */
public class PersistentDialog extends DialogFragment {

    private MainActivity.InformationDialogTransaction listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Note!");
        builder.setMessage("To copy results from non editable fields, long click the result");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.persistent_dialog_layout, null);

        CheckBox cb = view.findViewById(R.id.cb_show_again);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(listener == null)
                {
                    throw new IllegalArgumentException("MainActivity.InformationDialogTransaction listener not set, dialog needs listener to work!");
                }
                listener.onShowAgainValueChanged(b);
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Ok", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface inf, int which)
            {
                //close the dialog
            }
        });
        return builder.create();
    }

    public void setTransactionListener(MainActivity.InformationDialogTransaction listener)
    {
        this.listener = listener;
    }
}
