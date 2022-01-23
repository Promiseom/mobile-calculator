package com.aemis.promiseanendah.advancedscientificcalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Promise Anendah on 12/29/2017.
 */

public class NoticeDialogFragment extends DialogFragment {

    public final static String DIALOG_MESSAGE = "Dialog Fragment";
    public final static String DIALOG_TITLE = "Dialog Title";
    public final static String DIALOG_CONTAINS_OK = "Contains Ok";

    private DialogInterface.OnClickListener listener = null;

    public void setOnClickListener(DialogInterface.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState)throws NullPointerException
    {
        super.onCreateDialog(saveInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //the message(Notice) to display in this Dialog is set with the setArguments(Bundles bundle) method
        //is the message is set by the caller class, an exception is thrown
        if(getArguments().getString(DIALOG_MESSAGE) == null)
        {
            throw new NullPointerException("Notice Dialog Message not set, set message to display in dialog with NoticeDialogFragment.setArguments(Bundle bundle)");
        }else if(getArguments().getString(DIALOG_TITLE) == null)
        {
            throw new NullPointerException("Notice Dialog dialogTitle not set, set dialogTitle of dialog with NoticeDialogFragment.setArguments(Bundle bundle)");
        }
        builder.setTitle(getArguments().getString(DIALOG_TITLE));
        builder.setMessage(getArguments().getString(DIALOG_MESSAGE));
        if(getArguments().getBoolean(DIALOG_CONTAINS_OK))
        {
            builder.setPositiveButton("Ok", this.listener);
        }
        return builder.create();
    }

}
