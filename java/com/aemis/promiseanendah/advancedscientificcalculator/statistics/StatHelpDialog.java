package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

public class StatHelpDialog extends DialogFragment {

    public static final String STAT_HELP_DETAILS = "Stat_Help_Details";


    private String[] helpTopics;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.helpTopics = getResources().getStringArray(R.array.stat_help_items);
        builder.setTitle("Select Help Topics")
                .setItems(helpTopics, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getContext(), "Viewing help for " + helpTopics[which], Toast.LENGTH_SHORT).show();
                        DialogFragment helpDialog = new StatHelpDetailsDialog();
                        Bundle args = new Bundle();
                        args.putString(StatHelpDetailsDialog.DIALOG_TITLE, helpTopics[which]);
                        int layoutContent = R.layout.stat_help_adding_rows;
                        switch(which)
                        {
                            case 0:
                                layoutContent = R.layout.stat_help_adding_rows;
                                break;
                            case 1:
                                layoutContent = R.layout.stat_calculating_result_help;
                                break;
                            case 2:
                                layoutContent = R.layout.stat_using_extended_editor;
                                break;
                            case 3:
                                layoutContent = R.layout.stat_selecting_rows_help;
                                break;
                            case 4:
                                layoutContent = R.layout.stat_clearing_deleting_rows_help;
                                break;
                        }
                        args.putInt(StatHelpDetailsDialog.DIALOG_CONTENT_ID, layoutContent);
                        helpDialog.setArguments(args);
                        helpDialog.show(getFragmentManager(), STAT_HELP_DETAILS);
                    }
                });
        return builder.create();
    }
}
