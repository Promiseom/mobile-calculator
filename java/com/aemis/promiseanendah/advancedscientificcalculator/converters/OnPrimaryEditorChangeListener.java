package com.aemis.promiseanendah.advancedscientificcalculator.converters;


import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Promise Anendah on 3/10/2018.
 */

public interface OnPrimaryEditorChangeListener {

    /**
     * This methods is called when the primary editor of the converter fragment has changed
     * And the new components are passed to the function so that any listener will carry out the appropriate
     * functionality using the new components
     * @param newPrimaryEditor is the new primary editor
     * @param newPrimaryUnit is the new primary unit
     * @param newSecondaryEditor is the new secondary editor
     * @param newSecondaryUnit is the new secondary unit
     */
    void onPrimaryEditorChange(EditText newPrimaryEditor, Spinner newPrimaryUnit, EditText newSecondaryEditor, Spinner newSecondaryUnit);

}
