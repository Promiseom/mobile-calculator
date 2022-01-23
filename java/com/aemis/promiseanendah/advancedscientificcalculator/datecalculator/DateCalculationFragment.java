package com.aemis.promiseanendah.advancedscientificcalculator.datecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import java.util.Calendar;

import aemis.calculator.DateTime;

/**
 * DateCalculatorFragment is the fragment of the calculator that enables the user to carry out arithmetic operations on dates.
 * This fragment has 2 key operations: Date to Date Arithmetics and Date to single date field arithmetics where a specific value of the YEAR, MONTH or DAY can be
 * Added or subtracted from the given date producing a new date.
 *These child fragments can change at runtime.
 *
 * This fragment hosts another fragment, the hosted fragment is either DateArithmeticFragment or DateDifferenceFragment.
 * Created by Promise Anendah on 11/25/2017.
 */
public class DateCalculationFragment extends Fragment implements View.OnClickListener, android.app.DatePickerDialog.OnDateSetListener{

    public static final String DATE_CALCULATOR_FRAGMENT_PREF_NAME = "Date_Calculator_Fragment_Pref_Name";
    private static final String FROM_DATE_YEAR = "from_date_year";
    private static final String FROM_DATE_MONTH = "from_date_month";
    private static final String FROM_DATE_DAY_OF_MONTH = "from_date_day_of_month";
    private static final String CHECK_RADIO_BUTTON = "checked_radio_button";
    private Button btnFromDate;
    private DateTime fromDate;
    private OnCalculateListener listener;
    //an int value of 0 for DateDifference radio btn and 1 for DateArithmetics radio btn
    private int checkedRadioButton;

    public DateCalculationFragment()
    {
        this.fromDate = DateTime.now();
    }
    public DateTime getFromDate()
    {
        return this.fromDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.date_calculation_layout_main, container, false);
    }

    /**
     * Gets reference to views in the fragment after the activity has been created.
     * @param savedInstanceState Used to restore fragment  state if the state was previously saved
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getHost()).getSupportActionBar().setTitle("Date Calculator");
        Log.d(MainActivity.TAG, "DateCalculationFragment started");

        SharedPreferences pref = getContext().getSharedPreferences(DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
        int year = pref.getInt(FROM_DATE_YEAR, fromDate.get(Calendar.YEAR));
        int month = pref.getInt(FROM_DATE_MONTH, fromDate.get(Calendar.MONTH));
        int day = pref.getInt(FROM_DATE_DAY_OF_MONTH, fromDate.get(Calendar.DAY_OF_MONTH));
        this.checkedRadioButton = pref.getInt(CHECK_RADIO_BUTTON, 0);
        fromDate = new DateTime(year, month, day);

        btnFromDate = (Button) getView().findViewById(R.id.btn_from_date);
        btnFromDate.setText(this.fromDate.toDateShortString());
        btnFromDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //change the time
                DialogFragment dialog = new DateDialogFragment();

                Bundle argumentBundle = new Bundle();
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_YEAR_KEY, fromDate.get(Calendar.YEAR));
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_MONTH_KEY, fromDate.get(Calendar.MONTH));
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_DAY_KEY, fromDate.get(Calendar.DAY_OF_MONTH));

                dialog.setArguments(argumentBundle);
                ((DateDialogFragment)dialog).setListener(DateCalculationFragment.this);
                dialog.show(getFragmentManager(), "Date Picker");
            }
        });
        RadioButton radioButtonDateArithmetic = (RadioButton) getView().findViewById(R.id.radio_btn_date_arithmetic);
        radioButtonDateArithmetic.setOnClickListener(this);
        RadioButton radioButtonDifferenceInDate = (RadioButton) getView().findViewById(R.id.radio_btn_difference_in_date);
        radioButtonDifferenceInDate.setOnClickListener(this);

       //if there was saved instance state then get the saved instance from sharedPreferences
        if(savedInstanceState == null) {
            if(checkedRadioButton == 0)
            {
                radioButtonDifferenceInDate.setChecked(true);
                replaceChildFragment(new DateDifferenceFragment());
            }else
            {
                radioButtonDateArithmetic.setChecked(true);
                replaceChildFragment(new DateArithmeticFragment());
            }
        }
    }

    /**
     * Handles the click of radio buttons
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        boolean isChecked = ((RadioButton) view).isChecked();
        switch(view.getId())
        {
            case R.id.radio_btn_date_arithmetic:
                if(isChecked)
                {
                    this.checkedRadioButton = 1;
                    replaceChildFragment(new DateArithmeticFragment());
                    Log.d("dateCalculator", "Replacing the old fragment");
                }
                break;
            case R.id.radio_btn_difference_in_date:
                if(isChecked)
                {
                    this.checkedRadioButton = 0;
                    replaceChildFragment(new DateDifferenceFragment());
                }
                break;
        }
    }

    public void replaceChildFragment(Fragment newFragment)
    {
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, newFragment).commit();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(MainActivity.TAG, "Pausing DateCalculationFragment");
        MainActivity main = (MainActivity)getActivity();
        main.hideSoftInput(getContext(), btnFromDate);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(MainActivity.TAG, "Resuming DateCalculationFragment");
    }

    @Override
    public void onStop()
    {
        super.onStop();

        //save fragment data
        SharedPreferences preferences = getContext().getSharedPreferences(DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(FROM_DATE_YEAR, fromDate.get(Calendar.YEAR));
        editor.putInt(FROM_DATE_MONTH, fromDate.get(Calendar.MONTH));
        editor.putInt(FROM_DATE_DAY_OF_MONTH, fromDate.get(Calendar.DAY_OF_MONTH));
        editor.putInt(CHECK_RADIO_BUTTON,  this.checkedRadioButton);
        editor.apply();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        //the content of the button is the new set in string(dateTime.toDateShortString)
        Log.d(MainActivity.TAG, "The date has been set");
        this.fromDate = new DateTime(year, month, day);
        if(btnFromDate == null)
        {
            Log.d(MainActivity.TAG, "btnFromDate is null");
            return;
        }
        btnFromDate.setText(this.fromDate.toDateShortString());
        if(this.listener != null)
        {
            this.listener.onCalculate();
        }
    }

    public View.OnLongClickListener getResultTextLongClickListener() { return new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            TextView editor = (TextView)v;
            ((MainActivity)getActivity()).copyContentToClipboard(editor.getText());
            return false;
        }
    };
    }


    /**
     * Set the listener of the onCalculate event
     * This listeners are informed when a date calculation has been carried out.
     * @param listener
     */
    public void setOnCalculateListener(OnCalculateListener listener)
    {
        this.listener = listener;
    }

}
