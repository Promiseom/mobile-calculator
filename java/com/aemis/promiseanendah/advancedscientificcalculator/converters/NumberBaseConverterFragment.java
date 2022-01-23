package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import android.widget.Toast;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import aemis.calculator.NumberBaseConverter;

/**
 * Created by Promise Anendah on 11/24/2017.
 */
public class NumberBaseConverterFragment extends ConverterFragment implements AdapterView.OnItemSelectedListener, OnEditorTextChangeListener {

    public static final String TAG = "converter_fragment";
    public static final String NUMBER_BASE_CONVERTER_PREF_NAME = "number_base_converter_preference_name";

    //private KeyPadFragment keyPad;
    private int fromBase;

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        ((MainActivity)getHost()).getSupportActionBar().setTitle("Number Base");

        Log.d(TAG, "The Number Base Converter Fragment has been created");

        if(valueTitle != null)
        {
            valueTitle.setText("Number");
        }

        if( unitTitle != null)
        {
            unitTitle.setText("Base");
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.number_bases, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.primaryUnit.setAdapter(adapter);
        this.primaryUnit.setSelection(2);
        this.secondaryUnit.setAdapter(adapter);
        this.primaryUnit.setOnItemSelectedListener(this);
        this.secondaryUnit.setOnItemSelectedListener(this);

        //call to parent, call is made during the value editor and the unity spinner sync in parent class
        setOnPrimaryEditorChangeListener(new OnPrimaryEditorChangeListener() {
            @Override
            public void onPrimaryEditorChange(EditText newPrimaryEditor, Spinner newPrimaryUnit, EditText newSecondaryEditor, Spinner newSecondaryUnit)
            {
                fromBase = NumberBaseConverter.getBaseValue(primaryUnit.getSelectedItem().toString());

                //use the information to adjust the keypad
                if(newPrimaryUnit.getSelectedItemPosition() == 3 && currentKeyPadLayoutId != R.layout.extended_key_pad_fragment)
                {
                    setKeyPadLayout(R.layout.extended_key_pad_fragment, true);
                }else if(newPrimaryUnit.getSelectedItemPosition() != 3 && currentKeyPadLayoutId != R.layout.simple_key_pad_fragment)
                {
                    setKeyPadLayout(R.layout.simple_key_pad_fragment, true);
                }else
                {
                    //##########################################
                    //the keypad was not changed
                    //inform the keyboard to activate valid buttons
                    if(keyPad.setActiveButtons(fromBase))
                    {
                        Log.d(TAG, "Active buttons set");
                    }else
                    {
                        Log.d(TAG, "Unable to set active buttons");
                    }
                    Log.d(TAG, "#########Setting the active buttons, the layout has not changed with this change in primaryEditor#############");
                    //#############################################
                }
            }
        });
        Log.d(TAG, "The Number Base Converter Fragment has been creation__Finished");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        int fromBase = this.fromBase = NumberBaseConverter.getBaseValue(primaryUnit.getSelectedItem().toString());
        int toBase = NumberBaseConverter.getBaseValue(secondaryUnit.getSelectedItem().toString());

        if(primaryUnit.getSelectedItemPosition() == 3)
        {
            //inform the fragment to load a different key pad layout
            //change layout for hexadecimal
            Log.d(TAG, "->Change layout file for hexadecimal conversion");
            setKeyPadLayout(R.layout.extended_key_pad_fragment, true);
        }else if(this.primaryUnit.getSelectedItemPosition() != 3){
            //change the layout file back to the default
            Log.d(TAG, "->Change layout file back to default");
            setKeyPadLayout(R.layout.simple_key_pad_fragment, true);
        }else
        {
            Log.d(TAG, "->No layout changed");
            Log.d(TAG, "->The from base is " + fromBase);
            keyPad.setActiveButtons(fromBase);
        }

        //Log.d(TAG, "Unit item selected");
        //each derived class has to provide its own implementation of this method
        //the parent class does not know how the conversion method specific for this implementation
        try {
            String convertedValue = NumberBaseConverter.convertNumberBase(primaryEditor.getText().toString(), fromBase, toBase);
            onConversion(convertedValue);
        }catch(NumberFormatException e)
        {
            e.printStackTrace();
            primaryEditor.setText("0");
            secondaryEditor.setText("0");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "---->Leaving the code block<----");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        //do nothing
    }

    /**
     * Since it is the text of the primaryEditor that will be changing, a calculation needs to performed to
     *recalculate the proper conversion for the new input value
     * @param editText is the texteditor whose content has chaned
     */
    @Override
    public void onEditorTextChange(EditText editText)
    {
        Log.d(TAG, "Ready to convert after value change");
        try
        {
            int fromBase = NumberBaseConverter.getBaseValue(primaryUnit.getSelectedItem().toString());
            int toBase = NumberBaseConverter.getBaseValue(secondaryUnit.getSelectedItem().toString());
            //Log.d(TAG, "!!!About to convert value!!!");
            String convertedValue = NumberBaseConverter.convertNumberBase(primaryEditor.getText().toString(), fromBase, toBase);
            //Log.d(TAG, "!!!Value converted!!!");
            onConversion(convertedValue);
        }catch(NumberFormatException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "!!!"+e.getMessage());
            primaryEditor.setText("0");
            secondaryEditor.setText("0");
        }
        catch(Exception arg)
        {
            Log.d(TAG, "An error occurred");
            //something happened and the conversion was aborted
            arg.printStackTrace();
        }
    }

    /**
     * Add more items to the spinner
     * Increase
     * @param spinner
     */
    public void addMoreItems(Spinner spinner)
    {
        //create a new adapter for a more extended item array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.number_bases_more, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }

    public int getFromBase()
    {
        return this.fromBase;
    }

    /**
     * Set the type of keypad that will be used by the converter.
     * There're 2 type of keypads that can be used with the number base converter, the normal converter
     * containing numbers and the extended keypad which is used when converting from hexadecimal since hex contains alphabets
     * @param resourceFile
     * @param setKeyPadActiveButtons
     */
    @Override
    public void setKeyPadLayout(int resourceFile, boolean setKeyPadActiveButtons)
    {
        super.setKeyPadLayout(resourceFile, setKeyPadActiveButtons);
        keyPad.setOnEditorTextChange(this);
        keyPad.setPrimaryEditor(this.primaryEditor);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        SharedPreferences pref = getContext().getSharedPreferences(NUMBER_BASE_CONVERTER_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PRIMARY_EDITOR_ID, primaryEditor.getId());
        editor.putString(PRIMARY_EDITOR_CONTENT, primaryEditor.getText().toString());
        editor.putString(SECONDARY_EDITOR_CONTENT, secondaryEditor.getText().toString());
        editor.putInt(PRIMARY_UNIT_SELECTED_POSITION, primaryUnit.getSelectedItemPosition());
        editor.putInt(SECONDARY_UNIT_SELECTED_POSITION, secondaryUnit.getSelectedItemPosition());
        editor.apply();
    }

    @Override
    public void onViewStateRestored(Bundle instanceState)
    {
        super.onViewStateRestored(instanceState);
        SharedPreferences pref = getContext().getSharedPreferences(NUMBER_BASE_CONVERTER_PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "RESTORING APPLICATION VIEW STATE");
            if(instanceState == null)
            {
                int primaryEditorId = pref.getInt(PRIMARY_EDITOR_ID, R.id.txt_number);

                //make sure the right id is used, use the default primary if the id has been corrupt
                primaryEditorId = (primaryEditorId != R.id.txt_number && primaryEditorId != R.id.txt_number2)? R.id.txt_number : primaryEditorId;
                primaryEditor = (EditText)getView().findViewById(primaryEditorId);
                super.syncEditorAndUnits();
                primaryEditor.setText(pref.getString(PRIMARY_EDITOR_CONTENT, "0"));
                secondaryEditor.setText(pref.getString(SECONDARY_EDITOR_CONTENT, "0"));
        }
        primaryUnit.setSelection(pref.getInt(PRIMARY_UNIT_SELECTED_POSITION, 2));
        secondaryUnit.setSelection(pref.getInt(SECONDARY_UNIT_SELECTED_POSITION, 0));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "THE FRAGMENT HAS BEEN RESUMED");
    }

    /**
     * The child fragment of this fragment calls this method to inform the parent that it has been created.
     * This is when the state of the fragment will be restored
     */
    public void onChildFragmentCreated()
    {
        Log.d(TAG, "The child fragment has been created");
        if(keyPad == null)
        {
            Log.d(TAG, "The keypad is NULL");
        }else
        {
            Log.d(TAG, "The keypad is NOT NULL");
            keyPad.getKeyPadButtonsSize();
        }
        syncEditorAndUnits();
    }

}
