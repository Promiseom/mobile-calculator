package com.aemis.promiseanendah.advancedscientificcalculator.datecalculator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;

import java.util.Calendar;

/**
 * Fragment displays a date picker enabling the user to pick a date.
 * When first instantiated the initial date can either the one displaying the fragment
 * using the setArgument(Bundle dataBundle) interface or using the systems current date.
 * Created by Promise Anendah on 12/12/2017.
 */

public class DateDialogFragment extends DialogFragment{

    private DatePickerDialog.OnDateSetListener listener = null;
    public final String DIALOG_LISTENER = "dialog_listener";
    public static final String ARGUMENT_YEAR_KEY = "arguments_year_access_key";
    public static final String ARGUMENT_MONTH_KEY = "arguments_month_access_key";
    public static final String ARGUMENT_DAY_KEY = "arguments_day_access_key";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)throws NullPointerException
    {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        Bundle argumentBundle = getArguments();
        if(argumentBundle != null)
        {
            //if custom date has been set, the previously set data is overridden
            year = argumentBundle.getInt(ARGUMENT_YEAR_KEY);
            month = argumentBundle.getInt(ARGUMENT_MONTH_KEY);
            day = argumentBundle.getInt(ARGUMENT_DAY_KEY);
        }

        Log.d(MainActivity.TAG, "Date Picker dialog shown");
        //Fragment fragment = ((MainActivity)getHost()).getActiveFragment();
        if(listener == null)
        {
            throw new NullPointerException("Listener is null, listener cannot be null, please set listener to instance of object, use setListener(-) method");
        }
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    /**
     * There are 2 buttons each in different fragments that make use of this dialog
     * while returning the dialog, the dialog needs to know the listener
     * The listener is the name of the class that handles the onDateChanged event
     * this class T implements OnDateChangedListener interface
     * This is accomplished through the set listener method which allows the user to
     * set the listener of this dialog. This makes it more dynamic for any class to make use of this
     * dialog with different listeners.
     */
    public void setListener(DatePickerDialog.OnDateSetListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState)
    {
        super.onSaveInstanceState(instanceState);
    }

    @Override
    public void onStop()
    {
        //the dialog should be dismissed
        super.onStop();
    }

}
