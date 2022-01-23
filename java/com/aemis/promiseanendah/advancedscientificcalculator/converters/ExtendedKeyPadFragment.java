package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aemis.promiseanendah.advancedscientificcalculator.R;
import com.aemis.promiseanendah.advancedscientificcalculator.converters.KeyPadFragment;
import com.aemis.promiseanendah.advancedscientificcalculator.converters.NumberBaseConverterFragment;

/**
 * Created by Promise Anendah on 3/8/2018.
 */

public class ExtendedKeyPadFragment extends KeyPadFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceBundle)
    {
        return inflater.inflate(R.layout.extended_key_pad_fragment, viewGroup, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        Log.d(NumberBaseConverterFragment.TAG, "Extended keypad layout has been started");
        int startIndex = this.keyPadButtons.size();
        View view = getView();
        keyPadButtons.add((Button) view.findViewById(R.id.btn_a));
        keyPadButtons.add((Button) view.findViewById(R.id.btn_b));
        keyPadButtons.add((Button) view.findViewById(R.id.btn_c));
        keyPadButtons.add((Button) view.findViewById(R.id.btn_d));
        keyPadButtons.add((Button) view.findViewById(R.id.btn_e));
        keyPadButtons.add((Button) view.findViewById(R.id.btn_f));

        for(int a = startIndex; a < startIndex + 6; a++)
        {
            this.keyPadButtons.get(a).setOnClickListener(this);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }
}
