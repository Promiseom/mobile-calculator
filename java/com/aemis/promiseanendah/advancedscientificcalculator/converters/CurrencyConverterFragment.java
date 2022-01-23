package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

/**
 * Created by Promise Anendah on 11/25/2017.
 */
public class CurrencyConverterFragment extends ConverterFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);

        if(valueTitle != null)
        {
            valueTitle.setText("Value");
        }

        if( unitTitle != null)
        {
            unitTitle.setText("Currency");
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.number_bases, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerNumberBase.setAdapter(adapter);
        this.spinnerNumberBase2.setAdapter(adapter);
    }

}
