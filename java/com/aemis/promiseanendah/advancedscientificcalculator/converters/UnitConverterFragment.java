package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import aemis.calculator.Converter;
import aemis.calculator.Mathematics;
import aemis.calculator.physicalQuantities.*;
import android.util.Log;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

/**
 * Created by Promise Anendah on 11/29/2017.
 */
public class UnitConverterFragment extends ConverterFragment implements AdapterView.OnItemSelectedListener, OnEditorTextChangeListener{

    public static final String TAG = "UnitConverterFragment";
    public static final String PHYSICAL_QUANTITY = "physical quantity";

    //if this value is null, we'll be unable to save and restore the state of the unit converter
    public static String UNIT_CONVERTER_PREF_NAME = null;

    private Converter unitConverter;

    public UnitConverterFragment()
    {
        unitConverter = new Converter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        ((MainActivity)getHost()).getSupportActionBar().setTitle(getArguments().getString(PHYSICAL_QUANTITY));
        UNIT_CONVERTER_PREF_NAME =  "unit_converter_" + getArguments().getString(PHYSICAL_QUANTITY);

        Log.d(NumberBaseConverterFragment.TAG, "Unit Converter Fragment has been created");

        if(valueTitle != null)
        {
            valueTitle.setText("Value");
        }

        if( unitTitle != null)
        {
            unitTitle.setText("Unit of Measurement");
        }

        //what physical quantity to convert in
        String physicalQuantity = getArguments().getString(PHYSICAL_QUANTITY);

        unitConverter.setCoversionQuantity(physicalQuantity);
        Quantity conversionQuantity = unitConverter.getConversionQuantity();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, conversionQuantity.getUnitsName());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerNumberBase.setAdapter(adapter);
        this.spinnerNumberBase2.setAdapter(adapter);
        this.primaryUnit.setOnItemSelectedListener(this);
        this.secondaryUnit.setOnItemSelectedListener(this);
        setKeyPadLayout(R.layout.simple_key_pad_fragment, false);
        this.keyPad.setOnEditorTextChange(this);
        this.keyPad.setPrimaryEditor(this.primaryEditor);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        this.unitConverter.setConversionUnits(primaryUnit.getSelectedItem().toString(), secondaryUnit.getSelectedItem().toString());
        this.unitConverter.setValueInPrimaryUnitOfMeasurement(Double.parseDouble(primaryEditor.getText().toString()));
        //modify the content of the secondaryEditor
        if(Mathematics.canRepresentAsInteger(this.unitConverter.getValueInSecondaryUnitOfMeasurement()))
        {
            onConversion(Integer.toString((int)this.unitConverter.getValueInSecondaryUnitOfMeasurement()));
        }else
        {
           onConversion(Float.toString((float) this.unitConverter.getValueInSecondaryUnitOfMeasurement()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    public void onEditorTextChange(EditText editText)
    {
        try
        {
            this.unitConverter.setConversionUnits(primaryUnit.getSelectedItem().toString(), secondaryUnit.getSelectedItem().toString());
            this.unitConverter.setValueInPrimaryUnitOfMeasurement(Double.parseDouble(primaryEditor.getText().toString()));
            //modify the content of the secondaryEditor
            if(Mathematics.canRepresentAsInteger(this.unitConverter.getValueInSecondaryUnitOfMeasurement()))
            {
                onConversion(Integer.toString((int)this.unitConverter.getValueInSecondaryUnitOfMeasurement()));
            }else
            {
                onConversion(Float.toString((float) this.unitConverter.getValueInSecondaryUnitOfMeasurement()));
            }
        }catch(NumberFormatException e)
        {
            e.printStackTrace();
            primaryEditor.setText("0");
            secondaryEditor.setText("0");
        }
        catch(Exception arg)
        {
            //something happened and the conversion was aborted
            arg.printStackTrace();
        }

    }

    @Override
    public void onViewStateRestored(Bundle instanceState)
    {
        super.onViewStateRestored(instanceState);
        SharedPreferences pref = getContext().getSharedPreferences(UNIT_CONVERTER_PREF_NAME, Context.MODE_PRIVATE);
        if(instanceState == null)
        {

            int primaryEditorId = pref.getInt(PRIMARY_EDITOR_ID, R.id.txt_number);

            //make sure the right id is used, use the default primary if the id has been corrupt
            primaryEditorId = (primaryEditorId != R.id.txt_number && primaryEditorId != R.id.txt_number2)? R.id.txt_number : primaryEditorId;
            primaryEditor = (EditText)getView().findViewById(primaryEditorId);
            syncEditorAndUnits();
            primaryEditor.setText(pref.getString(PRIMARY_EDITOR_CONTENT, "0"));
            secondaryEditor.setText(pref.getString(SECONDARY_EDITOR_CONTENT, "0"));
        }
        primaryUnit.setSelection(pref.getInt(PRIMARY_UNIT_SELECTED_POSITION, 0));
        secondaryUnit.setSelection(pref.getInt(SECONDARY_UNIT_SELECTED_POSITION, 1));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        SharedPreferences pref = getContext().getSharedPreferences(UNIT_CONVERTER_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PRIMARY_EDITOR_ID, primaryEditor.getId());
        editor.putString(PRIMARY_EDITOR_CONTENT, primaryEditor.getText().toString());
        editor.putString(SECONDARY_EDITOR_CONTENT, secondaryEditor.getText().toString());
        editor.putInt(PRIMARY_UNIT_SELECTED_POSITION, primaryUnit.getSelectedItemPosition());
        editor.putInt(SECONDARY_UNIT_SELECTED_POSITION, secondaryUnit.getSelectedItemPosition());
        editor.apply();
    }
}
