package com.aemis.promiseanendah.advancedscientificcalculator.matrix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

public class MatrixCalculatorFragment extends Fragment{

    public static final String TAG = "MatrixCalcFragment";
    private ViewPager fragmentPager = null;
    private MatrixFragmentPagerAdapter fragmentPagerAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.matrix_calculator_fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        super.onActivityCreated(savedInstanceBundle);
        try {
            getActivity().getActionBar().setTitle("Matrices");
        }catch(NullPointerException arg)
        {
            //ignore if the dialogTitle cannot be set
            Log.e(TAG, arg.getMessage());
        }
        View view = getView();
        Log.d("Matrix" +
                "Fragment", "Matrix Calculator Activity has been created");
        try {
            fragmentPager = (ViewPager) view.findViewById(R.id.matrix_fragment_container);
            fragmentPagerAdapter = new MatrixFragmentPagerAdapter(getChildFragmentManager());
            fragmentPager.setAdapter(fragmentPagerAdapter);
        }catch(NullPointerException arg)
        {
            Log.d("MatrixFragment", "The matrix was not found in this fragment" + arg.getMessage());
        }

        Button btnAddRows = (Button) view.findViewById(R.id.btn_add_rows);
        Button btnAddCols = (Button) view.findViewById(R.id.btn_add_cols);

        btnAddCols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCols(1);
            }
        });

        btnAddCols.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addCols(1);
            }
        });

        fragmentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Adds a single column to all the existing rows in the current matrix
     * @param numberOfColsToAdd is the number of columns to add
     */
    private void addCols(int numberOfColsToAdd)
    {

    }
}
