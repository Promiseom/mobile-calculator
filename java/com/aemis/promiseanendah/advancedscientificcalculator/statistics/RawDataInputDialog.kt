package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity
import com.aemis.promiseanendah.advancedscientificcalculator.R

class RawDataInputDialog : DialogFragment()
{
    private var listener : DialogListener?
    private var dataEditor : EditText?
    private var freqEditor : EditText?

    var dialogTitle : String = ""
        get() = field
        set(value)
        {
            field = value
        }
    var contentInputType : Int = android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        get() = field
        set(value)
        {
            field = value
            Log.d(StatisticsGroupedDataRawInput.TAG, "Input type set")
        }

    var defaultDataContent : String = "0.0"
        get() = field
        set(value)
        {
            field = value
        }

    var defaultFrequencyContent : String = "1"
        get() = field
        set(value)
        {
            field = value
        }

    var isEditingContent : Boolean = false
        get() = field
        set(value)
        {
            field = value
        }

    init
    {
        listener = null
        dataEditor = null
        freqEditor = null
        //close on device rotation
        retainInstance = true
    }

    fun setOnDialogCloseListener(listener : DialogListener)
    {
        this.listener = listener
    }

    override fun onCreateDialog(saveInstanceState : Bundle?) : Dialog
    {
        super.onCreateDialog(saveInstanceState)
        val builder : AlertDialog.Builder = AlertDialog.Builder(context)

        //prepare the view the dialog will use so that its components can be modified before
        //calling setView(View) in the dialog
        //*Please note that components will be modified via function calls
        val layoutInflater : LayoutInflater = activity.layoutInflater
        val dialogView : View = layoutInflater.inflate(R.layout.raw_data_input_dialog_content, null)
        dataEditor = dialogView.findViewById(R.id.new_val)
        freqEditor = dialogView.findViewById(R.id.new_freq)
        //>>this should work but is not working<<//dataEditor?.inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        //set the default content to display in input fields (helpful when editing content)
        dataEditor?.hint = defaultDataContent
        freqEditor?.hint = defaultFrequencyContent
        Log.d(StatisticsGroupedDataRawInput.TAG, "Applying input type")

        builder.setTitle(this.dialogTitle)
        builder.setView(dialogView)

        builder.setPositiveButton(android.R.string.ok)
        {
            _,_ -> listener?.onDialogClose(DialogResult.Ok)
            val main : MainActivity = activity as MainActivity
            main.hideSoftInput(context, dataEditor)
            super.onDismiss(dialog)
        }
        builder.setNegativeButton(android.R.string.cancel)
        {
            _,_ -> listener?.onDialogClose(DialogResult.Cancel)
            val main : MainActivity = activity as MainActivity
            main.hideSoftInput(context, dataEditor)
            super.onDismiss(dialog)
        }

        //add the neutral button only when we are adding items (not when editing content)
        if(!isEditingContent)
        {
            builder.setNeutralButton("More", null)
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        //prevent dialog from closing
        if(!isEditingContent) {
            val myDialog: AlertDialog = dialog as AlertDialog
            val neutralBtn: Button = myDialog.getButton(Dialog.BUTTON_NEUTRAL)

            neutralBtn.setOnClickListener {
                listener?.onDialogClose(DialogResult.Ok)
                dataEditor?.setText("")
                freqEditor?.setText("")
            }
        }
    }

    /**
     * Return the input as a double value
     */
    fun getValue1() : Double
    {
        var value = defaultDataContent.toDouble()
        try
        {
            value = dataEditor?.text.toString().toDouble()
        }catch(arg : NumberFormatException)
        {
            //ignore and return default value
        }
        return value
    }

    fun getValue2() : Int
    {
        var value = defaultFrequencyContent.toInt()
        try
        {
            value = freqEditor?.text.toString().toInt()
        }catch(arg : NumberFormatException)
        {
            //ignore the and return default value
        }
        return value
    }
}