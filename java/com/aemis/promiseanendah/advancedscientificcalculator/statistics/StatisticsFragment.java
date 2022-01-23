package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aemis.promiseanendah.advancedscientificcalculator.ConfirmDialog;
import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.lang.Math;
import aemis.calculator.Mathematics;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Promise Anendah on 5/10/2018.
 */

public class StatisticsFragment extends Fragment implements View.OnLongClickListener{

    public static final String STATISTICS_FRAGMENT_PREF_FILENAME = "statistics_fragment_pref_filename";
    public static final String RESULT_ARRAY = "result array";
    public static final String MODAL_VALUES = "modal values";
    public static final String TAG = "StatisticsFragment";

    public static final String SERIAL_NUMBER = "s/n";
    public static final String DATA_FIELD = "data_field";
    public static final String FREQUENCY_FIELD = "frequency_field";
    public static final String DEVIATION_FIELD = "deviation_field";
    public static final String DEVIATION_SQUARE_FIELD = "deviation_square_field";
    public static final String DEVIATION_TIMES_FREQUENCY = "deviation_times_frequency";

    private static final String RESULT_HAS_BEEN_CALCULATED = "result has been calculated";
    private static final String NUMBER_OF_ROWS = "number of rows";
    private static final String DATA_FIELD_CONTENT = "data field content";
    private static final String FREQUENCY_FIELD_CONTENT = "frequency field content";
    private static final String DEVIATION_FIELD_CONTENT = "deviation field content";
    private static final String DEVIATION_SQUARED_FIELD_CONTENT = "deviation squared field content";
    private static final String DEVIATION_SQUARE_TIMES_FREQUENCY_CONTENT = "deviation square times frequency";

	private static final String ADD_NEW_DATA_ROWS = "Add new data rows";
	private static final String EXTENDED_EDITOR_TAG = "extended editor dialog";
	private static final String FIELD_DELETION_CONFIRM_DIALOG = "field_deleting_confirm_dialog";
    private static final String ALL_FIELD_DELETION_CONFIRM_DIALOG = "all field deletion confirm dialog";
    private static final String CLEARING_ALL_FIELDS_CONFIRM_DIALOG = "clearing all fields confirm dialog";

    private static final String INDEX_CURRENT_EXTENDED_EDITOR_COL = "index current extended editor col";
    private static final String INDEX_CURRENT_EXTENDED_EDITOR = "index current extended editor";
    private static final String IS_SELECTION_MODE = "is_selection mode";
    private static final String SELECTED_ROWS_INDEX = "selected row index";

    //you cannot add more than this number of rows int the calculator
    private static final int MAX_NUMBER_OF_ROWS = 200;
    //a frequency field cannot have more than this value
    private static final int MAX_FREQUENCY_VAL = 200;
    //a data field cannot have more then this value
    private static final double MAX_DATA_FIELD_VAL = 1000000;

    //The values of these variables are calculated while the application is running
    //These states should be saved when the activity is closing
    private double[] result;  //contains all the statistical measures calculated excluding the mode
    private String modeValues;

    //these will be used in rebuilding the UI when the activity is recreated
    private int numberOfRows;  //number of rows in the data
    //used to determine whether calculations on the data set are carried for the first time or recalculation
    private boolean resultHasBeenCalculated;

    //these hold the content of the columns as the fragment is destroyed, once the data is retrieved
    //its is set to null
    private String[] dataFieldContent, frequencyFieldContent, deviationFieldContent, deviationSquaredFieldContent, deviationSquaredFieldTimeFrequencyFieldContent;

    //these lists contain the numeric values the respective views hold
    //the frequency of each value is the value at the same index in the frequencyValues list
    private ArrayList<Double> dataFieldValues;
    private ArrayList<Integer> frequencyFieldValues;
    private TableLayout tableContainer;

    //these hold the columns that are added dynamically at runtime
    private final ArrayList<View> serialNumber = new ArrayList<>();
    private final ArrayList<View> dataField = new ArrayList<>();
    private final ArrayList<View> frequencyField = new ArrayList<>();
    private final ArrayList<View> deviationField = new ArrayList<>();
    private final ArrayList<View> deviationSquareField = new ArrayList<>();
    private final ArrayList<View> deviationSquareTimesFrequency = new ArrayList<>();

    //a reference to the menu is kept to add/remove menu contents
    private Menu menu;
    private boolean isSelectionMode = false;

    //contains index of selected rows
    private ArrayList<Integer> selectedRows = new ArrayList<>();

    //index of the current extended editor, this index will be used to get the actual view from the list
    //and set the new content, this index is the index of the view in the list of views
    //this is same thing as row number
    private int extendedEditorRow;
    //since only one editor is edited at a time, this value will be set based
    //on type of editor: Data, Frequency, Deviation, DeviationSquare, DeviationSquareTimeFrequency
    private int extendedEditorCol;

    //so that we can use the app resources without calling getResources();
    private Resources appResources;
    public StatisticsFragment()
    {
        super();
        //set the default values of the result,
        //this will prevent any error from occurring if the user checks the result before inputting the values
        this.result = new double[11];
        modeValues = "N/A";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appResources = getResources();

        //restore state before the rest of the fragment is recreated
        if(savedInstanceState != null)
        {
            this.result = savedInstanceState.getDoubleArray(RESULT_ARRAY);
            this.modeValues = savedInstanceState.getString(MODAL_VALUES);
            this.extendedEditorRow = savedInstanceState.getInt(INDEX_CURRENT_EXTENDED_EDITOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceBundle)
    {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.statistics_fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        ((MainActivity)getHost()).getSupportActionBar().setTitle("Statistics");
        try
        {
            final View fragmentView = getView();
            final Button btnCalculateResult = (Button)fragmentView.findViewById(R.id.btn_calculate_result);
            tableContainer = (TableLayout)getView().findViewById(R.id.table_layout_data_container);
            //TableRow th = (TableRow)getView().findViewById(R.id.table_header);

            if(savedInstanceBundle != null)
            {
                this.resultHasBeenCalculated = savedInstanceBundle.getBoolean("Result_Has_Been_Calculated", false);
                if(this.resultHasBeenCalculated)
                {
                    btnCalculateResult.setText(appResources.getString(R.string.str_recalculate));
                }
            }

            //reCreateFragment(savedInstanceBundle);
            reCreateFragment();

            btnCalculateResult.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    calculateStatResult();
                    btnCalculateResult.setText(appResources.getString(R.string.str_recalculate));
                }
            });
        }catch(NullPointerException arg)
        {
            //the fragments view could not be loaded
            Log.e(TAG, "There's was an error during the activity creation caused by referencing a null pointer!");
        }
    }

    private void calculateStatResult()
    {
        resultHasBeenCalculated = true;

        //Toast.makeText(getContext(), content.toString(), Toast.LENGTH_LONG).show();
        if(!getPreliminaryData())
        {
            //if not all preliminary data was gotten, the result should not be calculated
            return;
        }


        //get the stats(The results) from the data gotten
        ArrayList<Double> newData = applyFrequencyInList(dataFieldValues, frequencyFieldValues);
        Map<Double, Integer> modalMap = new Hashtable<>();
        try{
            this.result = Mathematics.getStatMeasures(newData, modalMap);
        }catch(IndexOutOfBoundsException arg)
        {
            Log.e(TAG, "The stat measures could not be calculated");

            //since result could not be calculated
            //set the result to their default values
            this.result = new double[11];
            this.modeValues = "N/A";
        }
        StringBuilder modeConstructor = new StringBuilder();

        //get the values of the modal dictionary and store them in a string variable
        for(Iterator<Double> iterator = modalMap.keySet().iterator(); iterator.hasNext();)
        {
            double value = iterator.next();
            int frequency = modalMap.get(value);
            modeConstructor.append(value).append("(").append(frequency).append("), ");
        }
        modeValues = modeConstructor.toString();
        logResult(result);

        completedUneditableFields();

        dataFieldValues = null;
        frequencyFieldValues = null;
        //set the results on the result panel
        //arithmeticMeanResult.setText("1. Arithmetic Mean: ".concat(Double.toString(result[Mathematics.ARITHMETIC_MEAN])));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MainActivity main = (MainActivity)getActivity();
        main.hideSoftInput(getContext(), tableContainer);
        
        /*
         * The AddNewRowDataDialog is the dialog used to add a variable number of rows to the table
         * This dialog contains an instance of this fragment, which is its parent fragment, this instance is used to
         * add the required number of rows at the specific position. This is done by calling the addRows on the parent instance
         *
         * However when the dialog is destroyed and recreated, the instance of the parent fragment is lost, this way the dialog
         * is unable to call the function to have the rows added. To solve this problem, the Parent Fragment has to reset its
         * instance to the dialog, if the dialog has been displayed when the fragment resumes.
         *
         * This was implemented by checking if the dialog fragment exists when the fragment resumes, if the dialog fragment can be found
         * it means the dialog exits then instance of the parent can be set, if not its ignored
         */

        Log.d(TAG, "The Statistics Fragment has resumed");
		//check if any fragment is open
        //set the parameters they need to perform that might have been
        //lost due to fragment restarting
		AddNewDataRowDialog addNewDataRowDialog = (AddNewDataRowDialog)getFragmentManager().findFragmentByTag(ADD_NEW_DATA_ROWS);
        ExtendedEditTextDialog extendedEditorDialog = (ExtendedEditTextDialog) getFragmentManager().findFragmentByTag(EXTENDED_EDITOR_TAG);
		ConfirmDialog fieldDeletionDialog = (ConfirmDialog) getFragmentManager().findFragmentByTag(FIELD_DELETION_CONFIRM_DIALOG);
        ConfirmDialog clearAllFieldDialog = (ConfirmDialog) getFragmentManager().findFragmentByTag(CLEARING_ALL_FIELDS_CONFIRM_DIALOG);
        ConfirmDialog deleteAllDataDialog = (ConfirmDialog) getFragmentManager().findFragmentByTag(ALL_FIELD_DELETION_CONFIRM_DIALOG);

        //if the dialog has been created
		if(addNewDataRowDialog != null)
		{
			//reset the parent fragment reference since it has been lost due to the fragment recreation
			addNewDataRowDialog.setParentFragment(this);
            Log.d(TAG, "!!!The add new dialog was found!!!");
		}else if(extendedEditorDialog != null)
        {
            extendedEditorDialog.setOnEditContentListener(onEditContentListener);
            Log.d(TAG, "The extended dialog was found");
        }else if(fieldDeletionDialog != null)
        {
            //fieldDeletionDialog.setArguments("Confirm Delete", "Note, the result will be recalculated");
            fieldDeletionDialog.setResponseListener(deleteSelectedRowsDialogListener);
        }else if(clearAllFieldDialog != null)
        {
            clearAllFieldDialog.setResponseListener(clearAllFieldsConfirmDialogListener);
        }else if(deleteAllDataDialog != null)
        {
            deleteAllDataDialog.setResponseListener(deleteAllDataConfirmDialog);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "View State has been restored!");
    }

    /**
     * Takes the values in the data and make them as many times the value at their index in the frequency list
     * Data list must be the same size with frequency list or an ArgumentException is thrown
     * @param data
     * @param frequencies
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> applyFrequencyInList(ArrayList<T> data, ArrayList<Integer> frequencies)throws IllegalArgumentException
    {
        if(data.size() != frequencies.size())
        {
            throw new IllegalArgumentException("The data and the frequency list must have the same cardinality");
        }
        ArrayList<T> newData = new ArrayList<>();
        for(int a = 0; a < data.size(); a++)
        {
            //add the value at data[a] frequency[a] times
            for(int count = 0; count < frequencies.get(a); count++)
            {
                newData.add(data.get(a));
            }
        }
        return newData;
    }

    public void logResult(double[] result)
    {
        if(result != null) {
            Log.d(TAG, "Cardinality: " + result[Mathematics.CARDINALITY]);
            Log.d(TAG, "Total Sum: " + result[Mathematics.TOTAL_SUM]);
            Log.d(TAG, "Arithmetic Mean: " + result[Mathematics.ARITHMETIC_MEAN]);
            Log.d(TAG, "Geometric Mean: " + result[Mathematics.GEOMETRIC_MEAN]);
            Log.d(TAG, "Harmonic Mean: " + result[Mathematics.HARMONIC_MEAN]);
            Log.d(TAG, "Root Mean Square: " + result[Mathematics.ROOT_MEAN_SQUARE]);
            Log.d(TAG, "Median: " + result[Mathematics.MEDIAN]);
            Log.d(TAG, "Range: " + result[Mathematics.RANGE]);
            Log.d(TAG, "Variance: " + result[Mathematics.VARIANCE]);
            Log.d(TAG, "Standard Deviation: " + result[Mathematics.STANDARD_DEVIATION]);
            Log.d(TAG, "Mean Deviation: " + result[Mathematics.MEAN_DEVIATION]);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        this.menu = menu;
        inflater.inflate(R.menu.statistics_menu_items, menu);
        Log.d(TAG, "Creating the options menu created for statistics fragment...");

        //the selection mode can only restore after the menu has been set,
        //this is because the menu will need to be inflated based on the selection mode

        //change the color of the selected rows
        for(int index : this.selectedRows)
        {
            selectRow(index, true);
        }
        resetSelectionMode();
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.d(TAG, "Destroying the options menu created for statistics fragment...");
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.actions_view_result:
                DialogFragment dialog = new StatisticsResultDisplayDialog();
                Bundle argumentBundle = new Bundle();
                //set the result to send to the dialog
                argumentBundle.putDoubleArray(RESULT_ARRAY, result);
                argumentBundle.putString(MODAL_VALUES, this.modeValues);
                dialog.setArguments(argumentBundle);
                dialog.show(getFragmentManager(), "Result Dialog");
                return true;
            case R.id.actions_help:
                DialogFragment statHelp = new StatHelpDialog();
                //Bundle arguments = new Bundle();
                //arguments.putString(NoticeDialogFragment.DIALOG_TITLE, "Help - Statistics");
                //arguments.putString(NoticeDialogFragment.DIALOG_MESSAGE, "This guide will help you get started on how to use the statistics calculator!");
                //noticeDialog.setArguments(arguments);
                statHelp.show(getFragmentManager(), "Help Notice Dialog");
                return true;
            case R.id.actions_calculateStatResult:
                try {
                    calculateStatResult();
                    ((Button) getView().findViewById(R.id.btn_calculate_result)).setText(getResources().getString(R.string.str_recalculate));
                }catch(NullPointerException arg)
                {
                    Log.e(TAG, "Could not set button text! Button view not found");
                }
                return true;
            case R.id.actions_add_row:
                addRows(1, -1, false);
                return true;
            case R.id.actions_add_rows:
                AddNewDataRowDialog addDataDialog = new AddNewDataRowDialog();
                //set the parent fragment, this instance will be enable the dialog
                //make a call to AddNewDataRowDialog.addRows(int, int) to create the new rows
                addDataDialog.setParentFragment(this);
                Bundle newArguments = new Bundle();
                newArguments.putString("DialogTitle", appResources.getString(R.string.str_add_insert_rows));
                newArguments.putString("Index", Integer.toString(10));
                addDataDialog.setArguments(newArguments);
                addDataDialog.show(getFragmentManager(), ADD_NEW_DATA_ROWS);
                return true;
            case R.id.actions_delete_all_rows:
                ConfirmDialog deleteAllRowsDialog = new ConfirmDialog();
                deleteAllRowsDialog.setArguments(appResources.getString(R.string.str_confirm_delete), appResources.getString(R.string.str_confirm_delete_message));
                deleteAllRowsDialog.setResponseListener(deleteAllDataConfirmDialog);
                deleteAllRowsDialog.show(getFragmentManager(), ALL_FIELD_DELETION_CONFIRM_DIALOG);
                return true;
            case R.id.actions_clear_all_selected_fields:
                //clear the data of all the selected fields without warning
                for(int selectedIndex : this.selectedRows)
                {
                    ((EditText)this.dataField.get(selectedIndex)).setText("");
                    ((EditText)this.frequencyField.get(selectedIndex)).setText("");
                    ((TextView)this.deviationField.get(selectedIndex)).setText("N/A");
                    ((TextView)this.deviationSquareField.get(selectedIndex)).setText("N/A");
                    ((TextView)this.deviationSquareTimesFrequency.get(selectedIndex)).setText("N/A");
                }
                Log.d(TAG, "Selected fields cleared!");
                return true;
            case R.id.action_deselect_all:
                Collections.sort(this.selectedRows);
                while(this.selectedRows.size() > 0)
                {
                    int selectedIndex = this.selectedRows.get(0);
                    this.selectedRows.remove((Object)selectedIndex);
                    selectRow(selectedIndex, false);
                }
                Log.d(TAG, ((this.selectedRows.size() > 0)? "Still selected":"No Selection"));
                //restore the menu of normal menu
                resetSelectionMode();
                return true;
            case R.id.actions_clear_all_fields:
                ConfirmDialog clearAllFieldDialog = new ConfirmDialog();
                clearAllFieldDialog.setArguments(appResources.getString(R.string.str_confirm_fields_clear), appResources.getString(R.string.str_confirm_clear_all_fields_message));
                clearAllFieldDialog.setResponseListener(clearAllFieldsConfirmDialogListener);
                clearAllFieldDialog.show(getFragmentManager(), CLEARING_ALL_FIELDS_CONFIRM_DIALOG);
                return true;
            case R.id.action_delete_selected_rows:
                ConfirmDialog cd = new ConfirmDialog();
                cd.setArguments(appResources.getString(R.string.str_confirm_delete), appResources.getString(R.string.str_confirm_delete_message2));
                cd.setResponseListener(deleteSelectedRowsDialogListener);
                cd.show(getFragmentManager(), FIELD_DELETION_CONFIRM_DIALOG);
                return true;
            default:
                return false;
        }
    }

    /**
     *Adds a variable number of rows to the table at given position
     * @param numberOfRows is the number of rows to add
     * @param position is the position to add the rows at
     * @param restoreContent determines if the content of the row added is to restored from previous instance
     *        since the content is gotten when the recalculate button is clicked
     */
    public void addRows(int numberOfRows, int position, boolean restoreContent)
    {
        int n = this.dataField.size();
        //limit the total number of rows that can bee create in total
        if(numberOfRows + n > MAX_NUMBER_OF_ROWS)
        {
            Toast.makeText(getContext(), String.format("Can create a max of %s rows, you can add %s more", MAX_NUMBER_OF_ROWS, MAX_NUMBER_OF_ROWS - n), Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        int index = (position > 0 && position <= tableContainer.getChildCount())? --position : tableContainer.getChildCount();
        int currentIndex = dataField.size() - 1;
        for(int counter = 0; counter < numberOfRows; counter++)
        {
            View newRow = inflater.inflate(R.layout.stat_new_row, new LinearLayout(getContext()));
            tableContainer.addView(newRow, index);
            currentIndex++; //an item has been added to the container

            ArrayList<View> tempViews = new ArrayList<>();

            //get all the new cols, this enable for insertion at right position in the view array
            newRow.findViewsWithText(tempViews, SERIAL_NUMBER, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            newRow.findViewsWithText(tempViews, DATA_FIELD, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            newRow.findViewsWithText(tempViews, FREQUENCY_FIELD, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            newRow.findViewsWithText(tempViews, DEVIATION_FIELD, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            newRow.findViewsWithText(tempViews, DEVIATION_SQUARE_FIELD, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            newRow.findViewsWithText(tempViews, DEVIATION_TIMES_FREQUENCY, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

            serialNumber.add(index, tempViews.get(0));

            tempViews.get(0).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String s = ((TextView)view).getText().toString();

                    //we need the actual row index of the view
                    int index = Integer.parseInt(s) - 1;
                    //view methods of the view need to be accessed
                    //View v = (View)view.getParent();

                    selectRow(index, !(selectedRows.contains(index)));//deselect if already selected

                    Log.d(TAG, "Selection Mode: " + ((isSelectionMode)? "On":"Off"));
                    resetSelectionMode();
                }
            });

            dataField.add(index, tempViews.get(1));
            frequencyField.add(index, tempViews.get(2));
            deviationField.add(index, tempViews.get(3));
            deviationSquareField.add(index, tempViews.get(4));
            deviationSquareTimesFrequency.add(index, tempViews.get(5));

            index++; //next row will be inserted at the next position

            //make the newly added fields respond to the clicks
            (dataField.get(currentIndex)).setOnLongClickListener(this);
            (frequencyField.get(currentIndex)).setOnLongClickListener(this);
            (deviationField.get(currentIndex)).setOnLongClickListener(this);
            (deviationSquareField.get(currentIndex)).setOnLongClickListener(this);
            (deviationSquareTimesFrequency.get(currentIndex)).setOnLongClickListener(this);

            //make the following fields non-editable
            //((TextView)serialNumber.get(currentIndex)).setInputType(View.LAYER_TYPE_NONE);
            //((TextView)deviationField.get(currentIndex)).setInputType(View.LAYER_TYPE_NONE);
            //((TextView)deviationSquareField.get(currentIndex)).setInputType(View.LAYER_TYPE_NONE);
            //((TextView)deviationSquareTimesFrequency.get(currentIndex)).setInputType(View.LAYER_TYPE_NONE);

            if(restoreContent)
            {
                //restore the content of the added field
                //set the content of the newly added row fields
                try {
                    ((EditText) this.dataField.get(currentIndex)).setText(this.dataFieldContent[counter]);
                    ((EditText) this.frequencyField.get(currentIndex)).setText(this.frequencyFieldContent[counter]);
                    ((TextView) this.deviationField.get(currentIndex)).setText(this.deviationFieldContent[counter]);
                    ((TextView) this.deviationSquareField.get(currentIndex)).setText(this.deviationSquaredFieldContent[counter]);
                    ((TextView) this.deviationSquareTimesFrequency.get(currentIndex)).setText(this.deviationSquaredFieldTimeFrequencyFieldContent[counter]);
                }catch(IndexOutOfBoundsException args)
                {
                    //can in know if the exception was thrown from an array or an array list
                    //because if it occurred in an array, then it is to be ignored, because this will imply
                    //there was no content to restore in a particular field
                    //IF THE CAUSE IS FROM AN ARRAY LIST THEN WE HAVE A PROBLEM
                    Log.e(TAG, "There was a problem while restoring the content of the fields!");
                }
            }
        }
        resetSerialNumber();
    }

    //selects or deselects a single row
    public void selectRow(int index, boolean isSelection)
    {
        View view = serialNumber.get(index);
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if(isSelection)
        {
            //Toast.makeText(getContext(), "Row " + s + " selected", Toast.LENGTH_SHORT).show();
            if(!selectedRows.contains(index))
            {
                selectedRows.add(index);
            }
            ///SET SELECTION
            viewParent.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            /*serialNumber.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            dataField.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            frequencyField.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            deviationField.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            deviationSquareField.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            deviationSquareTimesFrequency.get(index).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            */
        }else
        {
            //Toast.makeText(getContext(), "Row " + s + " deselected", Toast.LENGTH_SHORT).show();
            //SET DESELECTION
            viewParent.setBackgroundColor(getResources().getColor(R.color.transparent));
            selectedRows.remove((Object)index);

            /*serialNumber.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            dataField.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            frequencyField.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            deviationField.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            deviationSquareField.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            deviationSquareTimesFrequency.get(index).setBackgroundColor(getResources().getColor(R.color.transparent));
            */
        }
    }

    /**
     * Resets some properties of the fragment based on the state of the selection
     * This function is made after modification is made to the selection state such
     * as selecting an item, deleting a selected item or deselecting an item.
     */
    private void resetSelectionMode()
    {
        //reset the selection mode and the menu
        if(selectedRows.size() > 0)
        {
            if(!isSelectionMode)
            {
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.statistics_row_selection_menu, menu);
                Log.d(TAG, "Inflating menu with SELECTION MENU ITEMS");
                ((MainActivity)getHost()).getSupportActionBar().setTitle("");
            }
            menu.getItem(0).setTitle(Integer.toString(selectedRows.size()));
            isSelectionMode = true;
        }else
        {
            //this condition is kind of irrelevant since since there can't be a deselection without there
            // having being a selection first, since they're handled by the same click handler
            if(isSelectionMode)
            {
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.statistics_menu_items, menu);
                Log.d(TAG, "inflating Menu with MAIN MENU ITEMS");
                ((MainActivity)getHost()).getSupportActionBar().setTitle("Statistics");
            }
            isSelectionMode = false;
        }
        Log.d(TAG, "Resetting Selection Mode");

        // sdk versions higher than 23 (mashmellow) will make use theme settings to adjust drawable tint
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            //((MainActivity)getActivity()).setMenuIconTint(menu);
        }

    }

    /**
     * Recreates the fragment from a saved instance
     *
     * This function can only be called within this class since only the class will want to recreate itself from a saved instance
     */
    private void reCreateFragment(Bundle instanceState)
    {
        if(instanceState == null)
        {
            return;
        }

        this.dataFieldContent = instanceState.getStringArray(DATA_FIELD_CONTENT);
        this.frequencyFieldContent = instanceState.getStringArray(FREQUENCY_FIELD_CONTENT);
        this.deviationFieldContent = instanceState.getStringArray(DEVIATION_FIELD_CONTENT);
        this.deviationSquaredFieldContent = instanceState.getStringArray(DEVIATION_SQUARED_FIELD_CONTENT);
        this.deviationSquaredFieldTimeFrequencyFieldContent = instanceState.getStringArray(DEVIATION_SQUARE_TIMES_FREQUENCY_CONTENT);

        this.numberOfRows = instanceState.getInt(NUMBER_OF_ROWS);
        Log.d("Statistics", "During restoration - Number of rows: " + this.numberOfRows);
        //restore all rows with content
        addRows(this.numberOfRows, -1, true);

        //RELEASE THE DATA HELD BY THESE VARIABLES
        this.numberOfRows = 0;
        this.dataFieldContent = null;
        this.frequencyFieldContent = null;
        this.deviationFieldContent = null;
        this.deviationSquaredFieldContent = null;
        this.deviationSquaredFieldTimeFrequencyFieldContent = null;

        //REBUILD THE SELECTION
        this.isSelectionMode = false;
        this.selectedRows = instanceState.getIntegerArrayList(SELECTED_ROWS_INDEX);
    }

    /**
     * Restores the fragment data from a shared preference file
     */
    private void reCreateFragment()
    {
        SharedPreferences pref = getActivity().getSharedPreferences(STATISTICS_FRAGMENT_PREF_FILENAME, 0);

        String strResult = pref.getString(RESULT_ARRAY, "0 0 0 0 0 0 0 0 0 0 0 0");
        this.result = stringToDoubleArray(strResult);
        this.modeValues = pref.getString(MODAL_VALUES, "N/A");
        this.resultHasBeenCalculated = pref.getBoolean(RESULT_HAS_BEEN_CALCULATED, false);
        this.extendedEditorRow = pref.getInt(INDEX_CURRENT_EXTENDED_EDITOR, 0);
        this.extendedEditorCol = pref.getInt(INDEX_CURRENT_EXTENDED_EDITOR_COL, 1);

        //restore the field data that has been saved as string
        this.dataFieldContent = stringToStringArray(pref.getString(DATA_FIELD_CONTENT, ""));
        this.frequencyFieldContent = stringToStringArray(pref.getString(FREQUENCY_FIELD_CONTENT, ""));
        this.deviationFieldContent = stringToStringArray(pref.getString(DEVIATION_FIELD_CONTENT, ""));
        this.deviationSquaredFieldContent = stringToStringArray(pref.getString(DEVIATION_SQUARED_FIELD_CONTENT, ""));
        this.deviationSquaredFieldTimeFrequencyFieldContent = stringToStringArray(pref.getString(DEVIATION_SQUARE_TIMES_FREQUENCY_CONTENT, ""));

        this.numberOfRows = pref.getInt(NUMBER_OF_ROWS, 0);
        Log.d(TAG, "During Restoration from preference, number of rows: " + this.numberOfRows);

        addRows(this.numberOfRows, -1, true);

        //RELEASE THE DATA HELD BY THESE VARIABLES
        this.numberOfRows = 0;
        this.dataFieldContent = null;
        this.frequencyFieldContent = null;
        this.deviationFieldContent = null;
        this.deviationSquaredFieldContent = null;
        this.deviationSquaredFieldTimeFrequencyFieldContent = null;

        //REBUILD THE SELECTION
        this.isSelectionMode = false;
        this.selectedRows = stringToIntList(pref.getString(SELECTED_ROWS_INDEX, ""));

        if(this.resultHasBeenCalculated) ((Button)getView().findViewById(R.id.btn_calculate_result)).setText("Recalculate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveFragmentInstanceData(outState);
    }

    /**
     * Handles the displaying of extended editor.
     * Extended editor displays the value of a number in its actual length.
     * You can copy the value from the extended editor.
     * @param clickedView
     * @return
     */
    public boolean onLongClick(View clickedView)
    {
        try
        {
            final TextView editor = (TextView) clickedView;
            ExtendedEditTextDialog dialog = new ExtendedEditTextDialog();

            //so we can know the row number of the clicked view
            LinearLayout tr = (LinearLayout) clickedView.getParent();
            final String editorContentDescription = editor.getContentDescription().toString();

            //determine the column of the clicked editor
            switch(editorContentDescription)
            {
                case DATA_FIELD:
                    extendedEditorCol = 1;
                    break;
                case FREQUENCY_FIELD:
                    extendedEditorCol = 2;
                    break;
                case DEVIATION_FIELD:
                    extendedEditorCol = 3;
                    break;
                case DEVIATION_SQUARE_FIELD:
                    extendedEditorCol = 4;
                    break;
                case DEVIATION_TIMES_FREQUENCY:
                    extendedEditorCol = 5;
                    break;
            }

            LinearLayout trParent = (LinearLayout)tr.getParent();
            extendedEditorRow = tableContainer.indexOfChild(trParent);
            dialog.setArguments(editor.getText(), extendedEditorRow, extendedEditorCol,false);
            dialog.setOnEditContentListener(onEditContentListener);
            dialog.show(getFragmentManager(), EXTENDED_EDITOR_TAG);
            return true;
        }catch(ClassCastException arg)
        {
            //the clicked view is not a TextView
            //ignore this exception
            Log.e(TAG, arg.getMessage());
            return false;
        }
    }

    /**
     * Saved the state of the fragment to a bundle
     * @param outState
     */
    private void saveFragmentInstanceData(Bundle outState)
    {
        //SAVE RESULTS FROM LAST CALCULATION
        outState.putDoubleArray(RESULT_ARRAY, this.result);
        outState.putString(MODAL_VALUES, this.modeValues);

        outState.putBoolean(RESULT_HAS_BEEN_CALCULATED, this.resultHasBeenCalculated);
        outState.putInt(INDEX_CURRENT_EXTENDED_EDITOR, this.extendedEditorRow);

        //SAVE DATA IN THE STATISTICS TABLE
        this.numberOfRows = this.dataField.size();
        Log.d("Statistics","During saving - Number of rows: " + this.numberOfRows);

        outState.putInt(NUMBER_OF_ROWS, this.numberOfRows);

        //initialize the size of the array before writing to it
        this.dataFieldContent = new String[this.numberOfRows];
        this.frequencyFieldContent = new String[this.numberOfRows];
        this.deviationFieldContent = new String[this.numberOfRows];
        this.deviationSquaredFieldContent = new String[this.numberOfRows];
        this.deviationSquaredFieldTimeFrequencyFieldContent = new String[this.numberOfRows];

        //the size of data row is used since all fields contain the same number of columns
        for(int a = 0; a < this.numberOfRows; a++)
        {
            //get all the content of the data fields and store them in the String array
            this.dataFieldContent[a] = ((EditText)dataField.get(a)).getText().toString();
            this.frequencyFieldContent[a] = ((EditText)frequencyField.get(a)).getText().toString();
            this.deviationFieldContent[a] = ((TextView)deviationField.get(a)).getText().toString();
            this.deviationSquaredFieldContent[a] = ((TextView)deviationSquareField.get(a)).getText().toString();
            this.deviationSquaredFieldTimeFrequencyFieldContent[a] = ((TextView)deviationSquareTimesFrequency.get(a)).getText().toString();
        }

        outState.putStringArray(DATA_FIELD_CONTENT, this.dataFieldContent);
        outState.putStringArray(FREQUENCY_FIELD_CONTENT, this.frequencyFieldContent);
        outState.putStringArray(DEVIATION_FIELD_CONTENT, this.deviationFieldContent);
        outState.putStringArray(DEVIATION_SQUARED_FIELD_CONTENT, this.deviationSquaredFieldContent);
        outState.putStringArray(DEVIATION_SQUARE_TIMES_FREQUENCY_CONTENT, this.deviationSquaredFieldTimeFrequencyFieldContent);

        //set the containers of the data that has just been restored to null
        this.dataFieldContent = null;
        this.frequencyFieldContent = null;
        this.deviationFieldContent = null;
        this.deviationSquaredFieldContent = null;
        this.deviationSquaredFieldTimeFrequencyFieldContent = null;

        //SAVE DATA CONCERNING SELECTION OF ROWS
        outState.putIntegerArrayList(SELECTED_ROWS_INDEX, this.selectedRows);
    }

    /**
     * Saved the fragment data to shared preferences so the state can be restored when the application is launched
     */
    private void saveFragmentInstanceData()
    {
        SharedPreferences pref = getContext().getSharedPreferences(STATISTICS_FRAGMENT_PREF_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        StringBuilder strResult = new StringBuilder();

        for(double i : this.result)
        {
            strResult.append(String.format(Locale.getDefault(), "%s ", i));
        }

        editor.putString(RESULT_ARRAY, strResult.toString());
        Log.d(TAG, "Result Array: " + strResult.toString());
        editor.putString(MODAL_VALUES, this.modeValues);
        editor.putBoolean(RESULT_HAS_BEEN_CALCULATED, this.resultHasBeenCalculated);
        editor.putInt(INDEX_CURRENT_EXTENDED_EDITOR, this.extendedEditorRow);
        editor.putInt(INDEX_CURRENT_EXTENDED_EDITOR_COL, this.extendedEditorCol);

        //SAVE DATA IN THE STATISTICS TABLE
        this.numberOfRows = this.dataField.size();
        editor.putInt(NUMBER_OF_ROWS, this.numberOfRows);

        StringBuilder strDataFieldContent = new StringBuilder();
        StringBuilder strFrequencyFieldContent = new StringBuilder();
        StringBuilder strDeviationFieldContent = new StringBuilder();
        StringBuilder strDeviationSquaredFieldContent = new StringBuilder();
        StringBuilder strDeviationSquaredFieldTimesFrequencyFieldContent = new StringBuilder();

        for(int a = 0; a < this.numberOfRows; a++)
        {
            strDataFieldContent.append(((EditText)this.dataField.get(a)).getText()).append(" ");
            strFrequencyFieldContent.append(((EditText)this.frequencyField.get(a)).getText()).append(" ");
            strDeviationFieldContent.append(((TextView)this.deviationField.get(a)).getText()).append(" ");
            strDeviationSquaredFieldContent.append(((TextView)this.deviationSquareField.get(a)).getText()).append(" ");
            strDeviationSquaredFieldTimesFrequencyFieldContent.append(((TextView)this.deviationSquareTimesFrequency.get(a)).getText()).append(" ");
        }
        String strSelectedRowsIndex = this.selectedRows.toString();
        editor.putString(DATA_FIELD_CONTENT, strDataFieldContent.toString());
        Log.d(TAG, "DATA FIELD CONTENT: " + "\"" + strDataFieldContent.toString() + "\"");

        editor.putString(FREQUENCY_FIELD_CONTENT, strFrequencyFieldContent.toString());
        editor.putString(DEVIATION_FIELD_CONTENT, strDeviationFieldContent.toString());
        editor.putString(DEVIATION_SQUARED_FIELD_CONTENT, strDeviationSquaredFieldContent.toString());
        editor.putString(DEVIATION_SQUARE_TIMES_FREQUENCY_CONTENT, strDeviationSquaredFieldTimesFrequencyFieldContent.toString());

        //SAVE DATA CONCERNING SELECTION OF ROWS
        editor.putString(SELECTED_ROWS_INDEX, strSelectedRowsIndex.toString());
        editor.apply();
    }

    /**
     * The preliminary data is the data the user enters, this is the data and it's corresponding frequency.
     * This data is gotten from its fields. If any of these fields has been left blank, zero is placed in such a field
     * This data must be gotten before the result is calculated
     * @return
     */
    private boolean getPreliminaryData()
    {
        this.dataFieldValues = new ArrayList<>();
        this.frequencyFieldValues = new ArrayList<>();

        for(int a = 0; a < dataField.size(); a++)
        {
            //attempts getting the data the user has entered
            //if any fields has been left blank, zero will be inserted in place
            //Note: The user is only allowed to enter values in the data and frequency fields
            try {
                String dataFieldValue = ((EditText) dataField.get(a)).getText().toString();
                String frequencyFieldValue = ((EditText) frequencyField.get(a)).getText().toString();

                //set some random value for the field if its is empty
                if(dataFieldValue.isEmpty())
                {
                    //number = Integer.toString(randomNumber.nextInt((50 - (-50))+ 1) - 10);
                    ((EditText) dataField.get(a)).setText("0");
                    dataFieldValue = "0";
                }
                if(frequencyFieldValue.isEmpty())
                {
                    //number = Integer.toString(randomNumber.nextInt(20));
                    ((EditText) frequencyField.get(a)).setText("0");
                    frequencyFieldValue = "0";
                }
                //add the values to the list
                Double data = Double.parseDouble(dataFieldValue);
                int frequency = Integer.parseInt(frequencyFieldValue);

                if(frequency > MAX_FREQUENCY_VAL)
                {
                    ((EditText) frequencyField.get(a)).setError("Field value cannot be more than " + MAX_FREQUENCY_VAL);
                    //terminate the calculation
                    return false;
                }

                dataFieldValues.add(data);
                frequencyFieldValues.add(frequency);
                Log.d("Statistics", "Data: ".concat(dataFieldValue).concat(" Frequency: ").concat(frequencyFieldValue));
            }catch(ClassCastException arg)
            {
                String dataFieldContent = ((TextView)dataField.get(a)).getText().toString();
                Log.d("Statistics", "TextView Content: ".concat(dataFieldContent));
            }catch(NumberFormatException arg)
            {
                //check which editable field has an mal-formatted number
                ((EditText)frequencyField.get(a)).setError("Field cannot contain fractions or negative numbers!");
            }
        }
        return true;
    }

    /**
     * Completes the Uneditable fields with data that has been calculated from the
     * Data in the editable fields
     */
    private void completedUneditableFields()
    {
        //false mean used to complete the table(only for test version)
        //fill the other fields with the data
        for(int a = 0; a < dataField.size(); a++)
        {
            String fieldContent;
            Double fieldResult;
            //set the value of deviation field field
            //fieldContent = Double.toString(dataFieldValues.get(a)) + " - " + Double.toString(falseMean);
            fieldResult = dataFieldValues.get(a) - result[Mathematics.ARITHMETIC_MEAN];
            fieldContent = Double.toString(fieldResult);
            ((TextView)deviationField.get(a)).setText(fieldContent);
            //set the value for deviation square field
            fieldResult = Math.pow(fieldResult, 2);
            fieldContent = Double.toString(fieldResult);
            ((TextView)deviationSquareField.get(a)).setText(fieldContent);

            //set the value for deviation square times frequency field
            fieldResult *= frequencyFieldValues.get(a);
            fieldContent = Double.toString(fieldResult);
            //fieldContent = Double.toString(frequencyFieldValues.get(a)) + "(" + fieldContent + ")";
            ((TextView)deviationSquareTimesFrequency.get(a)).setText(fieldContent);
        }
    }

    private void clearAllDataRows(boolean deleteRows)
    {
        if(deleteRows)
        {
            this.tableContainer.removeAllViews();

            this.serialNumber.clear();
            this.dataField.clear();
            this.frequencyField.clear();
            this.deviationField.clear();
            this.deviationSquareField.clear();
            this.deviationSquareTimesFrequency.clear();
            this.modeValues = "N/A";
            this.result = new double[11];
        }else
        {
            //clear the content of the fields
            for (int selectedIndex = 0; selectedIndex < this.serialNumber.size(); selectedIndex++)
            {
                ((EditText) this.dataField.get(selectedIndex)).setText("");
                ((EditText) this.frequencyField.get(selectedIndex)).setText("");
                ((TextView) this.deviationField.get(selectedIndex)).setText("N/A");
                ((TextView) this.deviationSquareField.get(selectedIndex)).setText("N/A");
                ((TextView) this.deviationSquareTimesFrequency.get(selectedIndex)).setText("N/A");
            }
        }
        Button btnAddData = (Button)getView().findViewById(R.id.btn_calculate_result);
        btnAddData.setText("Calculate");
    }

    /**
     * Resets the serial of all the rows after the row has been added/removed
     */
    private void resetSerialNumber()
    {
        for(int i = 1; i <= this.serialNumber.size(); i++)
        {
            TextView snView = (TextView)this.serialNumber.get(i - 1);
            snView.setText(String.format("%d", i));
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        MainActivity main = (MainActivity)getActivity();
        main.hideSoftInput(getContext(), tableContainer);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Stopping Statistics Fragment!!!");
        //save fragment instance to shared preference
        saveFragmentInstanceData();
    }

    public ExtendedEditTextDialog.OnEditContent onEditContentListener = new ExtendedEditTextDialog.OnEditContent()
    {
        @Override
        //update the extended editor with the new content
        public void onEdit(CharSequence newContent, String contentDescription)
        {
            //use the extendedEditorRow and contentDescription to determine which view to edit
            if(extendedEditorCol == 1)
            {
                ((TextView)dataField.get(extendedEditorRow)).setText(newContent);
            }else if(extendedEditorCol == 2)
            {
                ((TextView)frequencyField.get(extendedEditorRow)).setText(newContent);
            }
        }

        @Override
        public boolean extendNextEditor()
        {
            if(++extendedEditorCol > 5) //since there's only 5 cols for each row
            {
                if(++extendedEditorRow < dataField.size())
                {
                    extendedEditorCol = 1;
                }else
                {
                    return false;  //no view to extend
                }
            }
            Log.d(TAG, "Row: " + extendedEditorRow + " Col: " + extendedEditorCol);

            //find the dialog and set the new argument for the next editor to extend
            ExtendedEditTextDialog dialog = (ExtendedEditTextDialog)getFragmentManager().findFragmentByTag(EXTENDED_EDITOR_TAG);
            dialog.setArguments(getCurrentEditorContent(), extendedEditorRow, extendedEditorCol, true);
            return true;
        }
    };

    /**
     * Returns the content of the editor specified by the extendedEditorRow and extendedEditorCol values
     * @return the content of the newly extended editor
     */
    private CharSequence getCurrentEditorContent()
    {
        //get the value of the current editor based on the row and column values
        //the extendedEditorRow tells us the index of the view to get the content from
        //the extendedEditorCol tells us which type of view to get the content from
        //the extendedEditorRow will have to be decremented by one since the array list holding the views is zero indexed
        TextView editor;
        switch(extendedEditorCol)
        {
            case 1: editor = (TextView)dataField.get(extendedEditorRow);
                break;
            case 2: editor = (TextView)frequencyField.get(extendedEditorRow);
                break;
            case 3: editor = (TextView)deviationField.get(extendedEditorRow);
                break;
            case 4: editor = (TextView)deviationSquareField.get(extendedEditorRow);
                break;
            case 5: editor = (TextView)deviationSquareTimesFrequency.get(extendedEditorRow);
                break;
            default:
                throw new IllegalArgumentException("Invalid editor col, unable to get the content of the next editor");
        }
        return editor.getText();
    }

    private double[] stringToDoubleArray(String strData)
    {
        //remove unnecessary characters
        strData = strData.replace(",", "");
        strData = strData.replace("[", "");
        strData = strData.replace("]", "");

        strData = strData.trim();
        if(strData.isEmpty())
        {
            return new double[0];
        }
        String[] strArr = strData.split(" ");
        double[] data = new double[strArr.length];

        for(int index = 0; index < data.length; index++)
        {
            data[index] = Double.parseDouble(strArr[index]);
        }

        return data;
    }

    private ArrayList<Integer> stringToIntList(String strData)
    {
        ArrayList<Integer> dataList = new ArrayList<>();
        //remove unnecessary characters
        strData = strData.replace(",", "");
        strData = strData.replace("[", "");
        strData = strData.replace("]", "");

        strData = strData.trim();
        if(strData.isEmpty())
        {
            return dataList;
        }
        String[] strArr = strData.split(" ");

        //use a count loop
        for(int a = 0; a < strArr.length; a++)
        {
            dataList.add(Integer.parseInt(strArr[a]));
        }
        return dataList;
    }

    private String[] stringToStringArray(String strData)
    {
        //remove unnecessary characters
        strData = strData.replace(",", "");
        strData = strData.replace("[", "");
        strData = strData.replace("]", "");

        strData = strData.trim();
        if(strData.isEmpty())
        {
            return new String[0];
        }
        return strData.split(" ");
    }

    private ConfirmDialog.ConfirmDialogResponseListener deleteSelectedRowsDialogListener = new ConfirmDialog.ConfirmDialogResponseListener() {
        @Override
        public void onPositiveButtonClick() {
            //remove all selected rows
            //remove the smallest index before the larger ones
            Collections.sort(selectedRows);
            //since multiple rows might be removed in a single operation, after each remove,
            //the indices of the rows change(shift) or reduce by one, indexReductionFactor is used
            //to know how far the index has reduced for each remove operation
            int indexReductionFactor = 0;
            while(selectedRows.size() > 0)
            {
                int selectedIndex = selectedRows.get(0);
                Log.d(TAG, "Removing row " + selectedIndex);
                //remove from list of rows
                selectedRows.remove((Object)selectedIndex); //if this number is not removed then we have a problem

                selectedIndex -= indexReductionFactor;
                serialNumber.remove(selectedIndex);
                dataField.remove(selectedIndex);
                frequencyField.remove(selectedIndex);
                deviationField.remove(selectedIndex);
                deviationSquareField.remove(selectedIndex);
                deviationSquareTimesFrequency.remove(selectedIndex);

                // remove from layout
                tableContainer.removeViewAt(selectedIndex);
                indexReductionFactor++;
                resetSerialNumber();
            }
            //selection state might have changed, reset
            resetSelectionMode();
            calculateStatResult();
        }

        @Override
        public void onNegativeButtonClick() {
            //do nothing
        }
    };

    private ConfirmDialog.ConfirmDialogResponseListener clearAllFieldsConfirmDialogListener = new ConfirmDialog.ConfirmDialogResponseListener()
    {
        @Override
        public void onPositiveButtonClick()
        {
            //clear the data of all the selected field
            clearAllDataRows(false);
            Log.d(TAG, "All data cleared!");
        }

        @Override
        public void onNegativeButtonClick()
        {
            //do nothing
        }

    };

    private ConfirmDialog.ConfirmDialogResponseListener deleteAllDataConfirmDialog = new ConfirmDialog.ConfirmDialogResponseListener()
    {
        @Override
        public void onPositiveButtonClick()
        {
            clearAllDataRows(true);
        }

        @Override
        public void onNegativeButtonClick()
        {
            //do nothing
        }
    };
}
