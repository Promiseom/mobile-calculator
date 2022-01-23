package com.aemis.promiseanendah.advancedscientificcalculator.matrix;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

import static com.aemis.promiseanendah.advancedscientificcalculator.matrix.MatrixCalculatorFragment.TAG;

/**
 * Created by Promise Anendah on 4/21/2018.
 */

public class MatrixFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle)
    {
        return inflater.inflate(R.layout.matrix_fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        View view = getView();
        if(view == null)
        {
            Log.d("MatrixFragment", "View is null");
        }else
        {
            Log.d("MatrixFragment", "View is not null");
        }
    }

    /** Adds a new row to the matrix, this is simply adding a new <TableRow> to the TableLayout and adding
     * the same number of EditText views as the other <TableRow>*/
    public void addRow()
    {
        View view = getView();
        if(view == null)
        {
            Log.d("MatrixFragment", "View is null");
        }else
        {
            Log.d("MatrixFragment", "View is not null");
        }
        TableLayout matrix = (TableLayout) view.findViewById(R.id.matrix);
        int currentRows = matrix.getChildCount();

        //create a new row
        TableRow newTableRow = new TableRow(getContext());
        try{
            //this might throw an exception if this is the very row to be added
            //get the number of cols the other rows have and use to create the cols in the new row
            TableRow previousRow = (TableRow)matrix.getChildAt(0);
            int currentCols = previousRow.getChildCount();
            //begin adding new cols to new row
            for(int a = 0; a < currentCols; a++)
            {
                //Create a new col and set attributes
                AttributeSet attributes;
                EditText newCol = new EditText(getContext());
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                newTableRow.addView(new EditText(getContext()), a);
            }
            matrix.addView(newTableRow, currentRows + 1);
        }catch(IndexOutOfBoundsException arg)
        {
            Log.d("MatrixFragment", "Index out of bound, handling...");
            //add the very fist element to the row
            newTableRow.addView(new EditText(getContext()), 0);
            //add the very first row
            matrix.addView(newTableRow, 0);
        }
    }

    /**
     * Adds a new colum to the matrix, if the matrix is empty, the first row and column in
     *
     */
    public void addCols(int numberOfColsToAdd)
    {
        //get the whole matrix
        TableLayout matrix = (TableLayout) getView().findViewById(R.id.matrix);
        int numberOfRows = matrix.getChildCount();

        for(int a = 0; a < numberOfRows; a++)
        {
            TableRow tableRow = (TableRow)matrix.getChildAt(a);

            for(int i = 0; i < numberOfColsToAdd; i++)
            {
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View newField = inflater.inflate(R.layout.matrix_field, new LinearLayout(getContext()));
                tableRow.addView(newField);
            }
        }
        Log.d(TAG, "All the columns have been added!");
    }
}
