package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import aemis.calculator.InconsistentClassSizeException
import aemis.calculator.InvalidClassBoundaryException
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.R

class GroupedDataInputDialog : DialogFragment()
{
    companion object
    {
        const val DIALOG_TITLE = "dialog_title"
        const val GROUP_VAL_SERIAL_NO = "data_serial_no"
        const val GROUP_VAL_UPPER_BOUNDARY = "upper_boundary"
        const val GROUP_VAL_LOWER_BOUNDARY = "lower_boundary"
        const val GROUP_VAL_CLASS_SIZE = "class_size"
        const val GROUP_VAL_CLASS_MARK = "class_mark"
        const val GROUP_VAL_FREQUENCY = "frequency"
        const val GROUP_VAL_CUMULATIVE_FREQUENCY = "cumulative_frequency"

        //used by the dialog to determine if the user is adding new groups or editing old ones
        const val IS_ADDING_GROUP = "is adding values"
        const val CAN_INPUT_CLASS_SIZE = "can_input_class_size"
    }

    private var closeListener : DialogListener?

    //all dialog views
    var txtSerialNo : EditText?
    var uBoundaryEditor : EditText?
    var lBoundaryEditor : EditText?
    var mClassSizeEditor : EditText?
    var classMarkEditor : EditText?
    var freqEditor : EditText?
    var cumFreq : EditText?

    //to be used by the snackbar
    var dialogView : View? = null

    init
    {
        retainInstance = true
        closeListener = null

        txtSerialNo = null
        uBoundaryEditor = null
        lBoundaryEditor = null
        mClassSizeEditor = null
        classMarkEditor = null
        freqEditor = null
        cumFreq = null
    }

    fun setOnDialogCloseListener(listener : DialogListener)
    {
        closeListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = AlertDialog.Builder(context)

        val layoutInflater = LayoutInflater.from(context)
        dialogView = layoutInflater.inflate(R.layout.group_data_input_dialog_content, null)

        txtSerialNo = dialogView?.findViewById(R.id.txt_serial_no)
        uBoundaryEditor = dialogView?.findViewById(R.id.txt_upper_boundary)
        lBoundaryEditor = dialogView?.findViewById(R.id.txt_lower_boundary)
        mClassSizeEditor = dialogView?.findViewById(R.id.txt_class_size)
        classMarkEditor = dialogView?.findViewById(R.id.txt_class_mark)
        freqEditor= dialogView?.findViewById(R.id.txt_frequency)
        cumFreq = dialogView?.findViewById(R.id.txt_cumulative_frequency)

        //don't continue if no listener has been set
        //listener has to be set so dialog can communicate with parent
        //else dialog will be useless
        if(closeListener == null)
        {
            throw UnsupportedOperationException("GroupDataInputDialog can not be displayed without a DialogListener")
        }

        if(arguments != null)
        {
            dialog.setTitle(arguments.getString(DIALOG_TITLE, "Input Dialog"))

            mClassSizeEditor?.isEnabled = arguments.getBoolean(CAN_INPUT_CLASS_SIZE, false)

            txtSerialNo?.hint = arguments.getString(GROUP_VAL_SERIAL_NO, "0")
            uBoundaryEditor?.hint = arguments.getString(GROUP_VAL_UPPER_BOUNDARY, "0.0")
            lBoundaryEditor?.hint = arguments.getString(GROUP_VAL_LOWER_BOUNDARY, "0.0")
            mClassSizeEditor?.setText(arguments.getString(GROUP_VAL_CLASS_SIZE, "0.0"))
            classMarkEditor?.hint = arguments.getString(GROUP_VAL_CLASS_MARK, "0.0")
            freqEditor?.hint = arguments.getString(GROUP_VAL_FREQUENCY, "0")
            cumFreq?.hint = arguments.getString(GROUP_VAL_CUMULATIVE_FREQUENCY, "0")

            if(arguments.getBoolean(IS_ADDING_GROUP, false))
            {
                dialog.setNeutralButton("More", null)
            }
        }
        dialog.setPositiveButton(android.R.string.ok, null)

        dialog.setNegativeButton(android.R.string.cancel)
        {
            _,_-> closeListener?.onDialogClose(DialogResult.Cancel)
        }

        dialog.setView(dialogView)
        return dialog.create()
    }

    /**
     * Prevent dialog from closing if user input is invalid.
     */
    override fun onResume()
    {
        super.onResume()

        val mDialog = dialog as AlertDialog

        val btnPositive = mDialog.getButton(Dialog.BUTTON_POSITIVE)
        val btnNeutral = mDialog.getButton(Dialog.BUTTON_NEUTRAL)

        btnPositive.setOnClickListener()
        {
            if(performPositiveAction()) dismiss()
        }

        btnNeutral.setOnClickListener()
        {
            if(performPositiveAction())
            {
                //clear users entry
                txtSerialNo?.setText("")
                uBoundaryEditor?.setText("")
                lBoundaryEditor?.setText("")
                classMarkEditor?.setText("")
                freqEditor?.setText("")
                cumFreq?.setText("")
            }
        }
    }

    /**
     * Throws an exception if there's an error in the users input.
     */
    private fun performPositiveAction() : Boolean
    {
        if(arguments == null)
        {
            arguments = Bundle()
        }

        //make sure the dialog does not empty strings
        val highBoundaryVal = uBoundaryEditor?.text.toString()
        val lowBoundaryVal = lBoundaryEditor?.text.toString()
        val classSizeVal = mClassSizeEditor?.text.toString()
        val frequencyVal = freqEditor?.text.toString()

        arguments.putString(GROUP_VAL_UPPER_BOUNDARY, if(highBoundaryVal == "") "0" else highBoundaryVal)
        arguments.putString(GROUP_VAL_LOWER_BOUNDARY, if(lowBoundaryVal == "") "0" else lowBoundaryVal)
        arguments.putString(GROUP_VAL_CLASS_SIZE, if(classSizeVal == "") "0" else classSizeVal)
        arguments.putString(GROUP_VAL_FREQUENCY, if(frequencyVal == "") "0" else frequencyVal)

        //inform user about error
        try
        {
            return closeListener!!.onDialogClose(DialogResult.Ok)
        }catch(args : InconsistentClassSizeException)
        {
            displayErrorMessage("Class boundary ${args.mCurrClassSize} does not match class size ${args.mClassSize}", "Close")
        }catch(args : InvalidClassBoundaryException)
        {
            displayErrorMessage("Invalid class boundary", "Close")
        }
        return false
    }

    private fun displayErrorMessage(message : String, actionText : String)
    {
        val mSnackBar = Snackbar.make(dialogView!!, message, Snackbar.LENGTH_INDEFINITE)
        mSnackBar.setAction(actionText)
        {
            mSnackBar.dismiss()
        }
        mSnackBar.show()
    }
}