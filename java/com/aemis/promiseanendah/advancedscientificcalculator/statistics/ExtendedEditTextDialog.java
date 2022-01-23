package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

import java.util.Locale;

public class ExtendedEditTextDialog extends DialogFragment {

    private static final String EDITOR_CONTENT = "Editor Content";
    private static final String EDITOR_CONTENT_DESCRIPTION = "editor content description";
    private static final String EDITOR_ROW_NUMBER = "editor row number";
    private static final String EDITOR_COL_NUMBER = "editor column number";

    private CharSequence editorContent;
    private String editorContentDescription;  //indicates if the view can be edited or not
    private int editorRow, editorCol;
    private EditText editor;
    //the listener should be set after the creation of the dialog, before it is displayed
    private OnEditContent listener = null;

    @Override
    public Dialog onCreateDialog(Bundle instanceBundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.extended_editor_layout, null);
        final EditText secondaryEditor = view.findViewById(R.id.secondary_editor);
        editor = secondaryEditor;
        final ImageButton btnCopyContent = view.findViewById(R.id.copy_content_to_clip_board);

        //copy the content of the extended editor to the clipboard
        btnCopyContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CharSequence editorContent = secondaryEditor.getText();
                ClipboardManager clipBoardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String[] mimetypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData cd = new ClipData("numeric value", mimetypes, new ClipData.Item(editorContent));
                clipBoardManager.setPrimaryClip(cd);
                Toast.makeText(getContext(), editorContent + " copied to clipboard!", Toast.LENGTH_SHORT).show();
                Log.d(StatisticsFragment.TAG, editorContent + " copied to clipboard!");
            }
        });

        builder.setView(view);
        if(instanceBundle == null)
        {
            secondaryEditor.setText(this.editorContent);   //the setArgument method has already been called
        }else
        {
            this.editorContentDescription = instanceBundle.getString(EDITOR_CONTENT_DESCRIPTION);
            this.editorRow = instanceBundle.getInt(EDITOR_ROW_NUMBER);
            this.editorCol = instanceBundle.getInt(EDITOR_COL_NUMBER);
            secondaryEditor.setText(instanceBundle.getCharSequence(EDITOR_CONTENT));
        }

        String str = initDialog();
        builder.setTitle(str);
        builder.setPositiveButton("Ok", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int clickedButtonId)
            {
                //only the content of the data field and the frequency fields can be modified
                if(editorCol == 1 || editorCol == 2)
                {
                    //make sure there is a listener
                    if(listener != null)
                    {
                        listener.onEdit(secondaryEditor.getText(), editorContentDescription);
                    }else
                    {
                        Log.e(StatisticsFragment.TAG, "Cannot edit extended field, listener not set!");
                    }
                }
            }
        });

        builder.setNeutralButton("Next", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dl, int which)
            {

            }
        });

        builder.setNegativeButton("Cancel", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int clickedButtonId)
            {
                //do nothing, just close the dialog and ignore any change that might have occurred
            }
        });
        return builder.create();
    }

    //sets the dialogTitle of the dialog and determines if the editor is editable or not
    //depending on column of the editor
    //returns the dialog dialogTitle
    private String initDialog()
    {
        String str = "";
        editor.setEnabled(true);
        switch(this.editorCol)
        {
            case 1:
                str = String.format(Locale.getDefault(), " View/Edit Row %s, Data", editorRow);
                break;
            case 2:
                str = String.format(Locale.getDefault(), "View/Edit Row %s, Frequency", editorRow);
                editor.setInputType(InputType.TYPE_CLASS_NUMBER);
                Log.d(StatisticsFragment.TAG, "Setting properties for frequency field extension");
                break;
            default:
                switch(this.editorCol)
                {
                    case 3:
                        str = String.format(Locale.getDefault(), "View Row %s, (x - x`)", editorRow);
                        break;
                    case 4:
                        str = String.format(Locale.getDefault(), "View Row %s, (x - x`)²", editorRow);
                        break;
                    case 5:
                        str = String.format(Locale.getDefault(), "View Row %s, f(x - x`)²", editorRow);
                        break;
                }
                editor.setEnabled(false);
        }
        return str;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AlertDialog dialog = (AlertDialog)getDialog();
        Button posButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        posButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(listener.extendNextEditor())
                {
                    Log.d(StatisticsFragment.TAG, "Extending Next Editor in row");
                }else
                {
                    Log.d(StatisticsFragment.TAG, "No more editors to extend");
                    dismiss(); //close the dialog
                }
            }
        });
    }

    /**
     * Sets the data the dialog needs, this method call changes the dialogTitle of the dialog
     * @param editorContent is the current content of the editor that has been extended
     * @param editorContentDescription is the content description of the extended editor,
     *                                 this is used to determine of the content is editable or not
     * @param editorRow is the row number of the extended editor
     */
    public void setArguments(CharSequence editorContent, String editorContentDescription, int editorRow)
    {
        this.editorContent = editorContent;
        this.editorContentDescription = editorContentDescription;
        this.editorRow = editorRow;
    }

    /**
     * Sets the data the dialog needs
     * @param editorContent os the current content of the editor that has been extended
     * @param editorRow is the row number of the extended editor
     * @param editorCol is the column number of the extended editor, this number will
     *                  determine the type of editor this is.
     */
    public void setArguments(CharSequence editorContent, int editorRow, int editorCol, boolean initDialog)
    {
        this.editorContent = editorContent;
        this.editorRow = editorRow + 1;
        this.editorCol = editorCol;
        if(initDialog)
        {
            AlertDialog dialog = (AlertDialog)getDialog();
            dialog.setTitle(initDialog());
            this.editor.setText(this.editorContent);
        }
    }

    public void setOnEditContentListener(OnEditContent listener)
    {
        this.listener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState)
    {
        super.onSaveInstanceState(instanceState);
        instanceState.putCharSequence(EDITOR_CONTENT, this.editorContent);
        instanceState.putString(EDITOR_CONTENT_DESCRIPTION, this.editorContentDescription);
        instanceState.putInt(EDITOR_ROW_NUMBER, this.editorRow);
        instanceState.putInt(EDITOR_COL_NUMBER, this.editorCol);
    }

    /**
     * This dialog used this interface to communicate with the fragment or activity that created it
     */
    public interface OnEditContent
    {
        //overrides the content of the extended editor
        void onEdit(CharSequence content, String editorContentDescription);
        //extends the next editor if available
        //returns true if view extended, else returns false
        boolean extendNextEditor();
    }
}
