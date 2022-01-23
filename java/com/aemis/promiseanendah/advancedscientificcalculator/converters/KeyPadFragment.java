package com.aemis.promiseanendah.advancedscientificcalculator.converters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aemis.promiseanendah.advancedscientificcalculator.R;

import java.util.ArrayList;

import static com.aemis.promiseanendah.advancedscientificcalculator.converters.NumberBaseConverterFragment.TAG;

/**
 * Created by Promise Anendah on 11/25/2017.
 */
public class KeyPadFragment extends Fragment implements View.OnClickListener{

	public static final String SELECTED_EDIT_VIEW_ID = "selected text editor";
	public static final String CURRENT_FRAGMENT_LAYOUT = "current fragment layout";

	protected ArrayList<Button> keyPadButtons = new ArrayList<>();
	protected OnEditorTextChangeListener listener;
	protected EditText primaryEditor;

	private boolean setActiveButtonsOnStart = false;
	//this is the layout of this fragment this value should be set before the fragment is layout is loaded

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle)
	{
		return inflater.inflate(R.layout.simple_key_pad_fragment, container, false);
	}

	//each time the editor is changed this method is called through the sync method
	public void setPrimaryEditor(EditText editor)
	{
		this.primaryEditor = editor;
	}

	public OnEditorTextChangeListener getOnEditorTextChangeListener(){return this.listener;}

	public void setActiveButtonsOnStart(boolean activateButtons)
	{
		this.setActiveButtonsOnStart = activateButtons;
	}

	@Override
	public void onActivityCreated(Bundle  savedInstanceBundle)
	{
		super.onActivityCreated(savedInstanceBundle);
		Log.d(TAG, "KeyPad Fragment has been started");
		View v = getView();
		keyPadButtons.add((Button) v.findViewById(R.id.btn_0));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_1));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_2));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_3));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_4));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_5));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_6));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_7));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_8));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_9));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_minus));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_cancel));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_delete));
		keyPadButtons.add((Button) v.findViewById(R.id.btn_dot));

		for(Button btn : keyPadButtons)
		{
			btn.setOnClickListener(this);
		}

		/**
		 * if the NumberBaseConverterFragment has set this to true then this method will
		 * be called when the onActivityCreated method is called
		 */
		if(this.setActiveButtonsOnStart)
		{
			setActiveButtons(((NumberBaseConverterFragment)getParentFragment()).getFromBase());
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onClick(View view)
	{
		//get the currently selected view on the main host of this fragment which is another fragment
		//if the view on focus is an EditText view then modify the value of the view
		Button btn = (Button)view;
		String currentContent = primaryEditor.getText().toString();
		try {
			switch (view.getId()) {
				case R.id.btn_cancel:
					primaryEditor.setText("0");
					break;
				case R.id.btn_delete:
					primaryEditor.setText(currentContent.substring(0, currentContent.length() - 1));  //remove one character from the left
					if(primaryEditor.getText().length() == 0)
					{
						primaryEditor.setText("0");
					}
					break;
				case R.id.btn_minus:
					//if the number already contains a minus then ignore if not make the number a negative number
					if (!currentContent.contains("-")) {
						String newContent = "-";
						primaryEditor.setText(newContent.concat(currentContent)); //the new content contains a minus
					} else {
						//make the number positive
						primaryEditor.setText(currentContent.substring(1, currentContent.length()));
					}
					break;
				case R.id.btn_dot:
					//if the number already contains a dot ignore, else add a dot
					if (!currentContent.contains(".")) {
						primaryEditor.setText(currentContent.concat("."));
					}
					return;
				default:
					if (currentContent.equals("0") || currentContent.equals("0.0")) {
						primaryEditor.setText(btn.getText().toString());
					} else if (currentContent.equals("-0") || currentContent.equals("-0.0")) {
						primaryEditor.setText("-".concat(btn.getText().toString()));
					} else {
						primaryEditor.setText(currentContent.concat(btn.getText().toString()));
					}
			}
			this.primaryEditor.setSelection(this.primaryEditor.getText().toString().length());
			if(this.listener != null)
			{
				this.listener.onEditorTextChange(primaryEditor);
			}
		}catch(StringIndexOutOfBoundsException e)
		{
			//ignore
		}

	}

	public void setOnEditorTextChange(OnEditorTextChangeListener listener)
	{
		this.listener = listener;
	}

	/**
	 * Disable some key s based on the primaryUnit.
	 * Called by the NumberBaseConverterFragment
	 * @param numberBase is the numberBase which will decide the enabled and disabled buttons
	 * @return true if the buttons were set, false if otherwise
	 */
	public boolean setActiveButtons(int numberBase)
	{
		boolean isSet = false;
		Log.d(TAG, "Setting Active Buttons");
		//only the first 10 keyPadButtons are numeral
		for(int a = 0; a < keyPadButtons.size() - 4; a++)
		{
			isSet = true;
			if(a >= numberBase)
			{
				keyPadButtons.get(a).setEnabled(false);
			}else if(!keyPadButtons.get(a).isEnabled())
			{
				keyPadButtons.get(a).setEnabled(true);
			}
		}
		return isSet;
	}

	//################################FOR DEBUG################################
	public void getKeyPadButtonsSize()
	{
		Log.d(TAG, "The keypad buttons size: " + keyPadButtons.size());
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming the KeyPadFragment, keypad button size: " + keyPadButtons.size());
		try
		{
			((NumberBaseConverterFragment)getParentFragment()).onChildFragmentCreated();
		}catch(ClassCastException arg)
		{
			//this exception is thrown when the NumberBaseConverterFragment is not the fragment is not the parent of this fragment
		}

	}

}
