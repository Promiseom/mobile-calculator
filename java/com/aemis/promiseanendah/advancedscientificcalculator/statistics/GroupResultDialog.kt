package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.aemis.promiseanendah.advancedscientificcalculator.R

class GroupResultDialog : DialogFragment()
{
    init{
        retainInstance = true
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = AlertDialog.Builder(context)

        /*
        * Content specific to this dialog
        * */
        var layoutInflater = activity.layoutInflater
        var dialogView = layoutInflater.inflate(R.layout.group_result_dialog_content, null)
        //dialog.setTitle("Statistics Result(Grouped Data)")
        dialog.setView(dialogView)
        dialog.setPositiveButton(android.R.string.ok)
        {_,_-> //do nothing, just close the dialog
        }

        return dialog.create()
    }
}