package com.aemis.promiseanendah.advancedscientificcalculator.calculators;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

/**
 * Created by Promise Anendah on 1/19/2018.
 */
public class NumberPadFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle)
    {
        return inflater.inflate(R.layout.main_num_pad_fragment_content, container, false);
    }

}
