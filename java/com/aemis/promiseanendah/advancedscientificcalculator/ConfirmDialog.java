package com.aemis.promiseanendah.advancedscientificcalculator;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;

public class ConfirmDialog extends DialogFragment {

    private static final String TITLE_TAG = "dialogTitle tag";
    private static final String MESSAGE_TAG = "message tag";

    private String title = null;
    private String message = null;

    private ConfirmDialogResponseListener responseListener = null;

    public void setArguments(String title, String message)
    {
        this.title = title;
        this.message = message;
    }

    public void setResponseListener(ConfirmDialogResponseListener listener)
    {
        this.responseListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(savedInstanceState != null) {
            this.title = savedInstanceState.getString(TITLE_TAG, getResources().getString(R.string.str_confirm_title));
            this.message = savedInstanceState.getString(MESSAGE_TAG, getResources().getString(R.string.str_confirm_dialog_message));
        }
        //make sure the dialogTitle has been set
        builder.setTitle(this.title);
        builder.setMessage(this.message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(responseListener == null)
                {
                    throw new NullPointerException("ResponseListener is null, response listener has not been set");
                }
                responseListener.onPositiveButtonClick();
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if(responseListener == null)
                {
                    throw new NullPointerException("ResponseListener is null, response listener has not been set");
                }
                responseListener.onNegativeButtonClick();
            }
        });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState)
    {
        super.onSaveInstanceState(instanceState);
        instanceState.putString(TITLE_TAG, this.title);
        instanceState.putString(MESSAGE_TAG, this.message);
    }

    public interface ConfirmDialogResponseListener
    {
        void onPositiveButtonClick();
        void onNegativeButtonClick();
        //void onNeutralButtonClick();
    }
}
