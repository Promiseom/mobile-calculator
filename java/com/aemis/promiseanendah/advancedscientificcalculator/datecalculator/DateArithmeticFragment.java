package com.aemis.promiseanendah.advancedscientificcalculator.datecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

import aemis.calculator.DateTime;

/**
 *The DateArithmeticFragment is hosted by DateCalculationFragment.
 * This fragment allows for Date arithmetics where an amount of a date field such as
 * YEAR, MONTH or DAY is either added or subtracted from a target date resulting to a new date.
 *
 * Created by Promise Anendah on 12/9/2017.
 */

public class DateArithmeticFragment extends Fragment implements View.OnClickListener, KeyListener, OnCalculateListener {

    private boolean addToDate;
    private Button btnCalculate;

    private TextView resultText;
    private EditText yearText;
    private EditText monthText;
    private EditText daysText;

    RadioButton radioButtonAdd;
    RadioButton radioButtonSubtract;

    public DateArithmeticFragment()
    {
        this.addToDate = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.date_arithmetic_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d("dateCalculator", "Date Arithmetics fragment has been started");
        radioButtonAdd = (RadioButton) getView().findViewById(R.id.radio_btn_add);
        radioButtonAdd.setOnClickListener(this);
        radioButtonSubtract = (RadioButton) getView().findViewById(R.id.radio_btn_subtract);
        radioButtonSubtract.setOnClickListener(this);

        resultText = (TextView)getView().findViewById(R.id.txt_date_arithmetic_result);
        yearText = (EditText) getView().findViewById(R.id.editText_year);
        monthText = (EditText) getView().findViewById(R.id.editText_month);
        daysText = (EditText) getView().findViewById(R.id.editText_days);

        btnCalculate = (Button) getView().findViewById(R.id.btn_calculate);
        btnCalculate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCalculate();
            }
        });

        View.OnLongClickListener longClickListener = ((DateCalculationFragment)getParentFragment()).getResultTextLongClickListener();
        resultText.setOnLongClickListener(longClickListener);

        //inform the parent who needs to be informed when an action on the parent fragment causes a calculation to be carried out
        ((DateCalculationFragment)getParentFragment()).setOnCalculateListener(this);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState == null)
        {
            SharedPreferences pref = getContext().getSharedPreferences(DateCalculationFragment.DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
            addToDate = pref.getBoolean("is_addition_operation", true);
            yearText.setText(pref.getString("year_value", "0"));
            monthText.setText(pref.getString("month_value", "0"));
            daysText.setText(pref.getString("day_value", "0"));
            resultText.setText(pref.getString("date_arithmetics_result", "Same Date"));

            if(this.addToDate)
            {
                this.radioButtonAdd.setChecked(this.addToDate);
                btnCalculate.setText("Add");
            }else
            {
                this.radioButtonSubtract.setChecked(true);
                btnCalculate.setText("Subtract");
            }
        }

    }

    @Override
    public void onClick(View view)
    {
        if(((RadioButton)view).isChecked())
        {
            switch(view.getId())
            {
                case R.id.radio_btn_add:
                    btnCalculate.setText("Add");
                    this.addToDate = true;
                    break;
                case R.id.radio_btn_subtract:
                    btnCalculate.setText("Subtract");
                    this.addToDate = false;
                    break;
            }
            //recalculate the date by addiding or subracting parameters
            onCalculate();
        }
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int keyCode)
    {

    }

    @Override
    public int getInputType()
    {
        return InputType.TYPE_CLASS_NUMBER;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent keyEvent)
    {
        Log.d("DateArithmeticFragment", "Key clicked");
        return false; //let the caller handle this method
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent keyEvent)
    {
        Toast.makeText(getContext(), "Key released", Toast.LENGTH_SHORT).show();
        Log.d("DateArithmeticFragment", "Key released");
        return false; //let the caller handle the event
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event)
    {
        return false; //let the caller handle this method
    }

    /**
     * Performs both the addition and the subtraction of dates
     */
    @Override
    public void onCalculate()
    {
        DateCalculationFragment fragment = (DateCalculationFragment)getParentFragment();
        DateTime fromDate = fragment.getFromDate();
        DateTime newDate = new DateTime(fromDate.get(DateTime.YEAR), fromDate.get(DateTime.MONTH), fromDate.get(DateTime.DAY_OF_MONTH));

        //validating the values of the date fields to make sure they contain numbers and are not empty
        if(yearText.getText().toString().length() < 1)
        {
            yearText.setText("0");
        }
        if(monthText.getText().toString().length() < 1)
        {
            monthText.setText("0");
        }
        if(daysText.getText().toString().length() < 1)
        {
            daysText.setText("0");
        }

        if(DateArithmeticFragment.this.addToDate)
        {
            newDate.add(DateTime.DAY_OF_MONTH, Integer.parseInt(daysText.getText().toString()));
            newDate.add(DateTime.MONTH, Integer.parseInt(monthText.getText().toString()));
            newDate.add(DateTime.YEAR, Integer.parseInt(yearText.getText().toString()));
        }else
        {
            newDate.subtract(DateTime.DAY_OF_MONTH, Integer.parseInt(daysText.getText().toString()));
            newDate.subtract(DateTime.MONTH, Integer.parseInt(monthText.getText().toString()));
            newDate.subtract(DateTime.YEAR, Integer.parseInt(yearText.getText().toString()));
        }

        if(Integer.parseInt(yearText.getText().toString()) == 0 && Integer.parseInt(monthText.getText().toString()) == 0 && Integer.parseInt(daysText.getText().toString()) == 0)
        {
            resultText.setText("Same Date");
        }else
        {
            resultText.setText(newDate.toDateShortString());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        SharedPreferences pref = getContext().getSharedPreferences(DateCalculationFragment.DATE_CALCULATOR_FRAGMENT_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("date_arithmetics_result", resultText.getText().toString() );
        editor.putString("year_value",  yearText.getText().toString());
        editor.putString("month_value",  monthText.getText().toString());
        editor.putString("day_value",  daysText.getText().toString());
        editor.putBoolean("is_addition_operation", addToDate);
        editor.apply();
    }

}
