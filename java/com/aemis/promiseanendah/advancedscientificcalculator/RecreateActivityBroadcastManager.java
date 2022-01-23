package com.aemis.promiseanendah.advancedscientificcalculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Promise Anendah on 2/13/2018.
 */

public class RecreateActivityBroadcastManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SettingsFragment.TAG, "This activity has been informed to create a new instance");
    }
}
