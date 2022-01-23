package com.aemis.promiseanendah.advancedscientificcalculator.matrix;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
/**
 * Created by Promise Anendah on 4/21/2018.
 */

public class MatrixFragmentPagerAdapter extends FragmentPagerAdapter {

    public MatrixFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    private MatrixFragment matrixFragment = new MatrixFragment();

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                return matrixFragment;
            case 1:
                return new MatrixFragment();
            default:
                return new MatrixFragment();
        }
    }
}
