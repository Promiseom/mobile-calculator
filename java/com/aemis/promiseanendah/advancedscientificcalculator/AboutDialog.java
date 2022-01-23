package com.aemis.promiseanendah.advancedscientificcalculator;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialog extends DialogFragment
{
  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.about_layout, null);

    //let the version change programmatically as the version name changes
    TextView versionText = (TextView)v.findViewById(R.id.txt_app_version);
    versionText.setText(String.format("%s %s", getResources().getString(R.string.str_version), BuildConfig.VERSION_NAME));

    builder.setView(v);

    builder.setPositiveButton("Close", new Dialog.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialogInterface, int id)
      {
        //do nothing, just close
      }
    });

    return builder.create();
  }
}