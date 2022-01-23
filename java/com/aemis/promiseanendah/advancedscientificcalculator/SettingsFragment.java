package com.aemis.promiseanendah.advancedscientificcalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.LinearLayout;

import java.text.NumberFormat;

/**
 * Created by Promise Anendah on 12/11/2017.
 */

public class SettingsFragment extends PreferenceFragment{

    public static final String MY_PREFERENCES = "my_preference_file";

    public static final String TAG = "SettingsFragment";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_STARTUP_MODE = "pref_startup_mode";
    public static final String PREF_NUMBER_PRECISION = "pref_number_precision";
    public static final String PREF_SCREEN_BRIGHTNESS_LOCK = "pref_screen_brightness_lock";
    public static final String SAVED_INSTANCE_BUNDLE = "Saved instance bundle";

    //the dialog that comes up in the activity when the app is started
    public static final String PREF_SHOW_PERSISTENT_DIALOG_1 = "persistent_dialog_one";

    private String selectedThemeSummary, selectedStartUpModeSummary;
    private String[] startUpModeEntries;
    private String[] themeEntries;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);
        startUpModeEntries = getResources().getStringArray(R.array.pref_calculator_startup_mode_entries);
        themeEntries = getResources().getStringArray(R.array.pref_themes_entries);

        SharedPreferences settings = getActivity().getSharedPreferences(MY_PREFERENCES, 0);
        String selectedStartUpModeValue = settings.getString(PREF_STARTUP_MODE, "0");
        String selectedThemeValue = settings.getString(PREF_THEME, "1");

        try {
            this.selectedStartUpModeSummary = startUpModeEntries[Integer.parseInt(selectedStartUpModeValue)];
            this.selectedThemeSummary = themeEntries[Integer.parseInt(selectedThemeValue)];
        }catch(NumberFormatException arg)
        {
            Log.e(TAG, arg.getMessage());
        }catch(IndexOutOfBoundsException arg)
        {
            Log.e(TAG, arg.getMessage());
        }

        Preference pref = findPreference(PREF_THEME);
        pref.setSummary(this.selectedThemeSummary);
        pref = findPreference(PREF_STARTUP_MODE);
        pref.setSummary(this.selectedStartUpModeSummary);

        Boolean bol = settings.getBoolean(PREF_SCREEN_BRIGHTNESS_LOCK, false);
        Log.d(TAG, "Screen Lock " + bol);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            SharedPreferences settings = getActivity().getSharedPreferences(MY_PREFERENCES, 0);
            SharedPreferences.Editor settingsEditor = settings.edit();
            Preference preference = findPreference(key);
            if(key.equals(PREF_THEME))
            {
                String setTheme = sharedPreferences.getString(key, "1");
                Log.d(TAG, "THE SET THEME IS " + setTheme);
                String selectedThemeEntry = themeEntries[Integer.parseInt(setTheme)];
                preference.setSummary(selectedThemeEntry);
                //save the new set theme
                settingsEditor.putString(PREF_THEME, setTheme);
                settingsEditor.apply();
                getActivity().recreate();
                MainActivity.isRecreateActivity = true;
            }else if(key.equals(PREF_STARTUP_MODE))
            {
                String setStartUpMode = sharedPreferences.getString(key, "0");
                Log.d(TAG, "START UP MODE SET TO " + setStartUpMode);
                String selectedStartUpModeEntry = startUpModeEntries[Integer.parseInt(setStartUpMode)];
                preference.setSummary(selectedStartUpModeEntry);

                settingsEditor.putString(PREF_STARTUP_MODE, setStartUpMode);
                settingsEditor.apply();
            }else if(key.equals(PREF_SCREEN_BRIGHTNESS_LOCK))
            {
                boolean setBrightnessLock = sharedPreferences.getBoolean(key, false);
                settingsEditor.putBoolean(key, setBrightnessLock);
                settingsEditor.apply();
                Log.d(TAG, "Screen brightness lock setting set to " + setBrightnessLock);
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

}
