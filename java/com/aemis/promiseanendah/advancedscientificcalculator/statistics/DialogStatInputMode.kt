package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.R

/**
 *
 */
class DialogStatInputMode : DialogFragment()
{
    private var listener : DialogListener?
    private var selectedOption : Int

    init
    {
        listener = null
        selectedOption = 0
    }

    fun setOnCloseListener(listener: DialogListener)
    {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        super.onCreateDialog(savedInstanceState)
        val builder : AlertDialog.Builder = AlertDialog.Builder(context)

        val inflater : LayoutInflater = activity.layoutInflater
        val view : View = inflater.inflate(R.layout.stat_grouped_input_method_dialog, null)

        //check the first radio button
        val radioButton : RadioButton = view.findViewById(R.id.radio_btn_raw_data)
        val radioButton2 : RadioButton = view.findViewById(R.id.radio_btn_table)

        radioButton.setOnCheckedChangeListener(){compountButton, isChecked -> if(isChecked) {selectedOption = 0}}
        radioButton2.setOnCheckedChangeListener(){compoundButton, isChecked -> if(isChecked) {selectedOption = 1}}
        radioButton.isChecked = true

        builder.setView(view)

        builder.setTitle("Choose Input mode")
        builder.setMessage("Note that input mode will depend on the type of data that you have.")

        //close the dialog and return a DialogResult to the creator of the dialog if listener is set
        builder.setPositiveButton(android.R.string.ok){_, _ -> listener?.onDialogClose(DialogResult.Ok)}
        builder.setNegativeButton(android.R.string.cancel){_,_ -> listener?.onDialogClose(DialogResult.Cancel)}
        return builder.create()
    }

    fun getSelectedOption() : Int
    {
        return selectedOption
    }
}