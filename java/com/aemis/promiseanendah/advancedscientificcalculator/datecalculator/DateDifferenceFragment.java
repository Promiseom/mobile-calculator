package com.aemis.promiseanendah.advancedscientificcalculator.datecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import java.util.Calendar;
import aemis.calculator.DateTime;

/**
 * DateDifferenceFragment is hosted by DateCalculationFragment and allows for finding the
 * between 2 dates. Given 2 dates the fragment can calculate the number of years,months, weeks and days between the 2 dates.
 *
 * Created by Promise Anendah on 12/9/2017.
 */

public class DateDifferenceFragment extends Fragment implements android.app.DatePickerDialog.OnDateSetListener, OnCalculateListener {

    private Button btnToDate;
    private DateTime toDate;
    private TextView resultText;
    private static final String DATE_DIFFERENCE_RESULT = "date_difference_result";
    private static final String TO_DATE_YEAR = "to_date_year";
    private static final String TO_DATE_MONTH = "to_date_month";
    private static final String TO_DATE_DAY_OF_MONTH = "to_day_day_of_month";

    public DateDifferenceFragment()
    {
        this.toDate = DateTime.now();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle)
    {
        return inflater.inflate(R.layout.date_difference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ((DateCalculationFragment)getParentFragment()).setOnCalculateListener(this);

        String resultString;

        //restore the fragment data
        SharedPreferences pref = getContext().getSharedPreferences(DateCalculationFragment.DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
        resultString = pref.getString(DATE_DIFFERENCE_RESULT, "Same Dates");
        int year = pref.getInt(TO_DATE_YEAR, toDate.get(Calendar.YEAR));
        int month = pref.getInt(TO_DATE_MONTH, toDate.get(Calendar.MONTH));
        int day = pref.getInt(TO_DATE_DAY_OF_MONTH, toDate.get(Calendar.DAY_OF_MONTH));
        toDate = new DateTime(year, month, day);

        //get the views and set their listeners
        this.resultText = (TextView) getView().findViewById(R.id.txt_date_difference_result);
        if(resultString != null)
        {
            this.resultText.setText(resultString);
        }

        View.OnLongClickListener longClickListener = ((DateCalculationFragment)getParentFragment()).getResultTextLongClickListener();
        resultText.setOnLongClickListener(longClickListener);

        this.btnToDate = (Button) getView().findViewById(R.id.btnToDate);
        this.btnToDate.setText(this.toDate.toDateShortString());
        this.btnToDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DialogFragment dialogFragment = new DateDialogFragment();
                Bundle argumentBundle = new Bundle();
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_YEAR_KEY, toDate.get(Calendar.YEAR));
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_MONTH_KEY, toDate.get(Calendar.MONTH));
                argumentBundle.putInt(DateDialogFragment.ARGUMENT_DAY_KEY, toDate.get(Calendar.DAY_OF_MONTH));

                dialogFragment.setArguments(argumentBundle);
                ((DateDialogFragment)dialogFragment).setListener(DateDifferenceFragment.this);
                dialogFragment.show(getFragmentManager(), "Date Picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int days)
    {
        this.toDate = new DateTime(year, month, days);
        this.btnToDate.setText(toDate.toDateShortString());
        Log.d(MainActivity.TAG, "Parent Fragment Name: " + getParentFragment().toString());

        onCalculate();
    }

    @Override
    public void onCalculate()
    {
        DateTime fromDate = ((DateCalculationFragment)getParentFragment()).getFromDate();
        int[] result = DateTime.compare(fromDate, toDate);
        this.resultText.setText(DateTime.dateDifferenceToString(result));
    }

    @Override
    public void onStop()
    {
        super.onStop();

        SharedPreferences pref = getContext().getSharedPreferences(DateCalculationFragment.DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(TO_DATE_YEAR, toDate.get(Calendar.YEAR));
        editor.putInt(TO_DATE_MONTH, toDate.get(Calendar.MONTH));
        editor.putInt(TO_DATE_DAY_OF_MONTH, toDate.get(Calendar.DAY_OF_MONTH));
        editor.putString(DATE_DIFFERENCE_RESULT, this.resultText.getText().toString());
        editor.apply();
    }

}
