package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

public class StatHelpDetailsDialog extends DialogFragment {

    public static final String DIALOG_TITLE = "dialog_title";
    public static final String DIALOG_CONTENT_ID = "dialog_layout_id";

    private String dialogTitle = null;
    private Bundle dBundle;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        this.dBundle = getArguments();
        if(this.dBundle != null)
        {
            this.dialogTitle = this.dBundle.getString(DIALOG_TITLE, "Statistics Help");
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(dBundle.getInt(DIALOG_CONTENT_ID), null);
            builder.setView(v);
        }else{
            this.dialogTitle = getResources().getString(R.string.str_users_guide);
        }
        builder.setTitle(getResources().getString(R.string.str_users_guide));

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //show the previous help dialog when this one has been cancelled
        DialogFragment statHelp = new StatHelpDialog();
        statHelp.show(getFragmentManager(), "Help Notice Dialog");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
