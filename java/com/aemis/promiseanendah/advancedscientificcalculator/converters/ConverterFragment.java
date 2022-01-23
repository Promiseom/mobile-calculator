package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import static com.aemis.promiseanendah.advancedscientificcalculator.converters.NumberBaseConverterFragment.TAG;

/**
 * Created by Promise Anendah on 11/29/2017.
 * This is the parent class for all converters: NumberBaseConverter and UnitConverter
 */
public class ConverterFragment extends Fragment implements View.OnTouchListener, View.OnLongClickListener{

    public final static String PRIMARY_EDITOR = "primary editor";
    public static final String CONVERTER_FRAGMENT_PREF_NAME = "converter_fragment_preference_name";

    protected static final String PRIMARY_EDITOR_ID = "primary_editor";
    protected static final String PRIMARY_EDITOR_CONTENT = "primary_editor_content";
    protected static final String SECONDARY_EDITOR_CONTENT = "secondary_editor_content";
    protected static final String PRIMARY_UNIT_SELECTED_POSITION = "primary_unit_selected_item_position";
    protected static final String SECONDARY_UNIT_SELECTED_POSITION = "secondary_unit_selected_item_position";

    protected int primaryColor, secondaryColor;

    protected TextView valueTitle, unitTitle;
    protected int currentKeyPadLayoutId;
    protected OnPrimaryEditorChangeListener listener = null;
    protected KeyPadFragment keyPad;

    protected int selectedIndexOne, selectedIndeTwo;

    EditText txtNumber, txtNumber2;
    Spinner spinnerNumberBase, spinnerNumberBase2;

    /**The primary editor is the editor where the value to convert from is input,
    * The secondary input is the editor where the converted value is set
    * The primaryUnit is the unit to convert from
    * the secondaryUnit is the unit to convert to
    * The units are set according to the editor
    **/
    protected EditText primaryEditor, secondaryEditor;
    protected Spinner primaryUnit, secondaryUnit;

    public void setOnPrimaryEditorChangeListener(OnPrimaryEditorChangeListener listener)
    {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.converter_layout_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        Log.d(TAG, "Converter Fragment Activity Created");

        //set the value and unit dialogTitle if available
        valueTitle = (TextView) getView().findViewById(R.id.txt_value_title);
        unitTitle = (TextView) getView().findViewById(R.id.txt_unit_title);

        spinnerNumberBase = (Spinner) getView().findViewById(R.id.spinner_number_base);
        spinnerNumberBase2 = (Spinner) getView().findViewById(R.id.spinner_number_base2);

        txtNumber = (EditText) getView().findViewById(R.id.txt_number);
        txtNumber2 = (EditText) getView().findViewById(R.id.txt_number2);

        txtNumber.setOnTouchListener(this);
        txtNumber2.setOnTouchListener(this);

        txtNumber.setOnLongClickListener(this);
        txtNumber2.setOnLongClickListener(this);

        txtNumber.setInputType(View.LAYER_TYPE_NONE);
        txtNumber2.setInputType(View.LAYER_TYPE_NONE);

        //if the instance bundle is null then no primaryEditor has been saved previously, load default
        this.primaryEditor = (savedInstanceBundle == null)? txtNumber : (EditText) getView().findViewById(savedInstanceBundle.getInt(PRIMARY_EDITOR));
        this.syncEditorAndUnits();
    }

    //change the value of the non-editable text
    public void onConversion(String convertedVal)
    {
        secondaryEditor.setText(convertedVal);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        this.primaryEditor = (EditText)v;
        //change the primary and secondary unit spinners as well
        syncEditorAndUnits();
        //Log.d(NumberBaseConverterFragment.TAG, "primary - " + primaryUnit.getSelectedItem().toString());
        //Log.d(NumberBaseConverterFragment.TAG, "secondary - " + secondaryUnit.getSelectedItem().toString());
        Log.d(TAG, "Click position: x: " + event.getX() + ", y: " + event.getY());
        return false;
    }

    @Override
    public boolean onLongClick(View v)
    {
        TextView editor = (TextView)v;
        ((MainActivity)getActivity()).copyContentToClipboard(editor.getText());
        return false;
    }

    /***
     * Syncs the secondaryEditor and the units , this is determined by the current primaryEditor
     */
    public void syncEditorAndUnits()
    {
        Log.d(TAG, "Syncing the units of measurement views with the editors");
        if(primaryEditor.getId() == R.id.txt_number)
        {
            this.secondaryEditor = this.txtNumber2;
            this.primaryUnit = this.spinnerNumberBase;
            this.secondaryUnit = this.spinnerNumberBase2;
        }else
        {
            this.secondaryEditor = this.txtNumber;
            this.primaryUnit = this.spinnerNumberBase2;
            this.secondaryUnit = this.spinnerNumberBase;
        }

        primaryEditor.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        secondaryEditor.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        primaryEditor.setTextSize(23f);
        this.secondaryEditor.setTextSize(18f);

        //only the NumberBaseConverterFragment listens, the UnitConverterFragment does not listen
        if(this.listener != null)
        {
            this.listener.onPrimaryEditorChange(this.primaryEditor, this.primaryUnit, this.secondaryEditor, this.secondaryUnit);
        }
        //inform the keypad about the change
        Log.d(TAG, "Primary Editor Set");
        if(this.keyPad != null)
        {
            this.keyPad.setPrimaryEditor(this.primaryEditor);
        }else
        {
            //if the keypad has not been set then there is a problem,
            //because the converter cannot run without a keypad this problem will be handled here
            Log.d(TAG, "It appears the converter has no keypad, an internal error has occurred");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState)
    {
        super.onSaveInstanceState(instanceState);
        instanceState.putInt(PRIMARY_EDITOR, this.primaryEditor.getId());

    }

    /**
     * Change the current layout file of the keypadFragment this is only accessible by this class and its children
     * a child will typically call this method to make the change, after this method is called the calling class will need to
     * restore the state of the keypad with the new layout
     * @param newKeyPadLayoutId is the id of the keypad layout resource to change the keypad layout to
     * @param setActiveButtonsOnStart if true, the keypad will set the active buttons, disabling others when the keypad is created
     */
    protected void setKeyPadLayout(int newKeyPadLayoutId, boolean setActiveButtonsOnStart)
    {
        KeyPadFragment keyPad = (newKeyPadLayoutId == R.layout.simple_key_pad_fragment)? new SimpleKeyPadFragment() : new ExtendedKeyPadFragment();
        this.keyPad = keyPad;
        keyPad.setActiveButtonsOnStart(setActiveButtonsOnStart);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.key_pad_fragment_container, keyPad);
        ft.commit();

        // Log.d(NumberBaseConverterFragment.TAG, "Restoring keypad state");
        this.currentKeyPadLayoutId = newKeyPadLayoutId;
        Log.d(TAG, "Keypad layout file fragment changed");
    }

    /**
     * This method is called to save the state of this activity
     */
    private void saveData()
    {

    }

    /**
     * This method is called to restore the state of the activity based on the saved instance
     */
    private void restoreApplicationState()
    {

    }
}
