package com.aemis.promiseanendah.advancedscientificcalculator.statistics;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;

import aemis.calculator.Mathematics;

/**
 * Created by Promise Anendah on 6/6/2018.
 *
 */

/**
 * Handles the displaying of result values.
 *
 * Values are set using the setArgument(Bundle) method and are retrieved using
 * appropriate index.
 */
public class StatisticsResultDisplayDialog extends DialogFragment implements View.OnLongClickListener{

    @Override
    public Dialog onCreateDialog(Bundle instanceBundle)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(getResources().getString(R.string.str_statistical_measures));
        View view = inflater.inflate(R.layout.stat_measures_result_dialog, null);
        Bundle arguments = getArguments();
        //get the result and modify the view before giving the dialog the view
        double[] result = arguments.getDoubleArray(StatisticsFragment.RESULT_ARRAY);

        EditText cardinality = (EditText) view.findViewById(R.id.txt_result_cardinality);
        EditText totalSum = (EditText) view.findViewById(R.id.txt_result_total_sum);
        EditText arithmeticMean = (EditText) view.findViewById(R.id.txt_result_arithmetic_mean);
        EditText geometricMean = (EditText) view.findViewById(R.id.txt_result_geometric_mean);
        EditText harmonicMean = (EditText) view.findViewById(R.id.txt_result_harmonic_mean);
        EditText rootMeanSquare = (EditText) view.findViewById(R.id.txt_result_roo_mean_square);
        EditText median = (EditText) view.findViewById(R.id.txt_result_median);
        EditText mode = (EditText) view.findViewById(R.id.txt_result_mode);
        EditText range = (EditText) view.findViewById(R.id.txt_result_Range);
        EditText variance = (EditText) view.findViewById(R.id.txt_result_variance);
        EditText standardDeviation = (EditText) view.findViewById(R.id.txt_result_standard_deviation);
        EditText meanDeviation = (EditText) view.findViewById(R.id.txt_result_mean_deviation);

        //no input type for this editors
        cardinality.setText(Double.toString(result[Mathematics.CARDINALITY]));
        cardinality.setInputType(0);
        cardinality.setOnLongClickListener(this);

        totalSum.setText(Double.toString(result[Mathematics.TOTAL_SUM]));
        totalSum.setInputType(0);
        totalSum.setOnLongClickListener(this);

        arithmeticMean.setText(Double.toString(result[Mathematics.ARITHMETIC_MEAN]));
        arithmeticMean.setInputType(0);
        arithmeticMean.setOnLongClickListener(this);

        geometricMean.setText(Double.toString(result[Mathematics.GEOMETRIC_MEAN]));
        geometricMean.setInputType(0);
        geometricMean.setOnLongClickListener(this);

        harmonicMean.setText(Double.toString(result[Mathematics.HARMONIC_MEAN]));
        harmonicMean.setInputType(0);
        harmonicMean.setOnLongClickListener(this);

        rootMeanSquare.setText(Double.toString(result[Mathematics.ROOT_MEAN_SQUARE]));
        rootMeanSquare.setInputType(0);
        rootMeanSquare.setOnLongClickListener(this);

        median.setText(Double.toString(result[Mathematics.MEDIAN]));
        median.setInputType(0);
        median.setOnLongClickListener(this);

        mode.setText(arguments.getString(StatisticsFragment.MODAL_VALUES, "N/A"));  //if the modal values where not gotten write N/A in the field
        mode.setInputType(0);
        mode.setOnLongClickListener(this);

        range.setText(Double.toString(result[Mathematics.RANGE]));
        range.setInputType(0);
        range.setOnLongClickListener(this);

        variance.setText(Double.toString(result[Mathematics.VARIANCE]));
        variance.setInputType(0);
        variance.setOnLongClickListener(this);

        standardDeviation.setText(Double.toString(result[Mathematics.STANDARD_DEVIATION]));
        standardDeviation.setInputType(0);
        standardDeviation.setOnLongClickListener(this);

        meanDeviation.setText(Double.toString(result[Mathematics.MEAN_DEVIATION]));
        meanDeviation.setInputType(0);
        meanDeviation.setOnLongClickListener(this);

        builder.setView(view);
        builder.setPositiveButton("Ok", new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int clickedButtonId)
            {
                //do nothing simply close the dialog
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle instanceBundle)
    {
        super.onActivityCreated(instanceBundle);
    }

    @Override
    public boolean onLongClick(View view)
    {
        CharSequence editorContent = ((EditText)view).getText();
        ((MainActivity)getActivity()).copyContentToClipboard(editorContent);

//        ClipboardManager clipBoardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        String[] mimetypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//        ClipData cd = new ClipData("numeric value", mimetypes, new ClipData.Item(editorContent));
//        clipBoardManager.setPrimaryClip(cd);
//        Toast.makeText(getContext(), editorContent + " copied to clipboard!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
