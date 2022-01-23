package com.aemis.promiseanendah.advancedscientificcalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Created by Promise Anendah on 12/11/2017.
 */

public class SettingsActivity extends Activity {

	public static final String TAG = "SettingsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//call function to set the theme here
		changeTheme();
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting settings activity");

		try {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}catch(NullPointerException arg)
		{
			Log.d(TAG, "Unable to set Home As Up in settings fragment");
		}catch(android.content.res.Resources.NotFoundException arg)
		{
			Log.d(TAG, arg.getMessage());
		}
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home)
		{
			Log.d(TAG, "Home button pressed");
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("is_activity_resume", true);
			startActivity(intent);
			this.finish();
			Log.d(TAG, "Settings Activity has been destroyed!");
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "Back key pressed");
		Intent intent = new Intent(this, MainActivity.class);
		//before starting the settings fragment the main activity had to be destroyed
		intent.putExtra("is_activity_resume", true);
		startActivity(intent);
		this.finish();
		Log.d(TAG, "Settings Activity has been destroyed!");
		//super.onBackPressed();
	}

	//sets the theme based on the preference value
	public void changeTheme()
	{
		SharedPreferences sharedPreferences = getSharedPreferences(SettingsFragment.MY_PREFERENCES, 0);
		String themeString = sharedPreferences.getString(SettingsFragment.PREF_THEME, "");
		//Log.d(TAG, "The value of the current theme is " + themeString);
		switch(themeString)
		{
			case "0":
				setTheme(R.style.AppTheme_Light_ActionBar);
				break;
			case "1":
				setTheme(R.style.AppTheme_Dark_ActionBar);
				break;
			default:
				setTheme(R.style.AppTheme_Dark_ActionBar);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
