package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

public class AddNewDataRowDialog extends DialogFragment {

    private static final String TAG  = "AddNewDataRowDialog";
    private EditText amountEditor, indexEditor;
    private CheckBox checkBox;
    private StatisticsFragment parentFragment = null;
    private final int maxRowCount = 100; //the max number of rows you can add at a time.

    public void setParentFragment(StatisticsFragment parentFragment)
    {
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle instanceBundle)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_data_rows, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        amountEditor = (EditText)dialogView.findViewById(R.id.txt_amount_editor);
        indexEditor = (EditText)dialogView.findViewById(R.id.txt_add_index);
        checkBox = (CheckBox)dialogView.findViewById(R.id.insert_check_box);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton checkedButton, boolean isChecked)
            {
                AlertDialog dialog = (AlertDialog)getDialog();
                Button posBtn = dialog.getButton(Dialog.BUTTON_POSITIVE);
                if(isChecked)
                {
                    posBtn.setText(getString(R.string.str_insert));
                }else{
                    posBtn.setText(getString(R.string.str_add));
                }
            }
        });

        String strTitle = getArguments().getString("DialogTitle");
        String strIndexHint = getArguments().getString("Index");
        indexEditor.setHint(strIndexHint);
        builder.setView(dialogView);

        builder.setTitle(strTitle);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int clickedButton)
            {
                //do nothing just close the dialog
                MainActivity main = (MainActivity)getActivity();
                main.hideSoftInput(getContext(), amountEditor);
            }
        });

        return builder.create();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final AlertDialog dialog = (AlertDialog)getDialog();
        Button btnPositive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int amount;
                boolean canDismiss = true;
                //the try catch blocks have been separated for each editText view so we can know where the error occurred
                try
                {
                    amount = Integer.parseInt(amountEditor.getText().toString());
                    if (amount > maxRowCount) {
                        //restrict the number rows that can be added at once to (0 - 100)
                        amountEditor.setError(getResources().getString(R.string.str_new_rows_amount_error));
                        canDismiss = false;
                        //Toast.makeText(parentFragment.getContext(), "Can add a max of 100 rows at a time!", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        try
                        {
                            if (checkBox.isChecked()) {
                                int position = Integer.parseInt(indexEditor.getText().toString());
                                parentFragment.addRows(amount, position, false);
                            } else {
                                parentFragment.addRows(amount, -1, false);
                            }
                        }catch(NullPointerException arg)
                        {
                            //the parent fragment has not been set
                            Log.e(TAG, arg.getMessage());
                        }catch(NumberFormatException arg)
                        {
                            indexEditor.setError(getResources().getString(R.string.str_invalid_index));
                            canDismiss = false;
                            //Toast.makeText(parentFragment.getContext(), "Invalid integer parameter!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(NullPointerException arg)
                {
                    //the parent fragment has not been set
                    Log.e(TAG, arg.getMessage());
                }catch(NumberFormatException arg)
                {
                    amountEditor.setError(getResources().getString(R.string.str_invalid_amount));
                    canDismiss = false;
                    //Toast.makeText(parentFragment.getContext(), "Invalid integer parameter!", Toast.LENGTH_SHORT).show();
                }finally
                {
                    if(canDismiss)
                    {
                        //since there was no error, close the dialog
                        MainActivity main = (MainActivity)getActivity();
                        main.hideSoftInput(getContext(), amountEditor);
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState)
    {
        super.onSaveInstanceState(instanceState);
    }
}
