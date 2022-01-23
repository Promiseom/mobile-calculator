package com.aemis.promiseanendah.advancedscientificcalculator.calculators;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity;
import com.aemis.promiseanendah.advancedscientificcalculator.R;
import com.aemis.promiseanendah.advancedscientificcalculator.calculators.CalculatorFragment;

import java.util.ArrayList;

import aemis.calculator.Exceptions.InvalidArgumentException;
import aemis.calculator.Mathematics;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Promise Anendah on 1/31/2018.
 */

public class ScientificCalculatorFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{

    public static final String TAG = "scientific_calculator";
    private ArrayList<Button> numberKeys = new ArrayList<>();
    private TextView txtShift, txtHex, txtOct, txtDec, txtBin;

    private EditText expressionTextMain;
    private TextView expressionResultTextMain;
    //any time it is a long click set this to true so all the click function will ignore its function
    private boolean isLongClick = false;
    //used to signify in error in the calculator
    //the 2 main errors are Syntax Error and Math Error, when these error occur, the calculator will not respond
    //to any order click apart from the AC key which will clear the screen and the error
    private boolean calculatorError = false;

    //true when there is already a dot in the number where the cursor is
    private boolean dotPresentInNumber = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceBundle)
    {
        return inflater.inflate(R.layout.scientific_calculator_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle)
    {
        Log.d(TAG, "Activity Started");
        super.onActivityCreated(savedInstanceBundle);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Scientific");
        ((MainActivity)getActivity()).getSupportActionBar().hide();
        //get references to some of the views in this fragment
        View view = getView();

        //add the number key buttons
        this.numberKeys.add((Button) view.findViewById(R.id.btn_0));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_1));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_2));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_3));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_4));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_5));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_6));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_7));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_8));
        this.numberKeys.add((Button) view.findViewById(R.id.btn_9));

        //set listeners for all the buttons
        for (Button btn : this.numberKeys) {
            btn.setTag(CalculatorFragment.TAG_NUMERIC_KEYS);
            //this content description is used to get all the buttons in this fragment
            btn.setContentDescription("Button");
        }

        //set the properties of views that make up the display
        //the expressionText and expressionResultText variables are declared final so they can be accessed from an inner class
        //the expressionText and expressionResultText variables will only be accessible in the onActivityCreated Scope

        final EditText expressionText = (EditText) view.findViewById(R.id.mathExpressionText);
        final TextView expressionResultText = (TextView) view.findViewById(R.id.expressionResultText);
        //these will be used in other parts of this class, with exception of inner classes
        this.expressionTextMain = expressionText;
        this.expressionResultTextMain = expressionResultText;

        //prevent the clicking of this EditText to display cursor and bring up keyboard
        //effectively making it non-editable

        expressionText.setTextIsSelectable(false);
        expressionText.setSelected(false);

        txtShift = (TextView) view.findViewById(R.id.txt_shift);
        txtHex = (TextView) view.findViewById(R.id.txt_hex);
        txtOct = (TextView) view.findViewById(R.id.txt_oct);
        txtDec = (TextView) view.findViewById(R.id.txt_dec);
        txtBin = (TextView) view.findViewById(R.id.txt_bin);

        //disable all by default while enabling dec by default
        txtShift.setVisibility(View.INVISIBLE);
        txtHex.setVisibility(View.INVISIBLE);
        txtOct.setVisibility(View.INVISIBLE);
        txtDec.setVisibility(View.VISIBLE);
        txtBin.setVisibility(View.INVISIBLE);

        //get the function keys
        Button btnShift = (Button) view.findViewById(R.id.btnShift);
        btnShift.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnShift.setContentDescription("Button");

        Button btnButton = (Button) view.findViewById(R.id.btn_menu);
        btnButton.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnButton.setContentDescription("Button");

        Button btnCursorLeft = (Button) view.findViewById(R.id.btn_cursor_left);
        btnCursorLeft.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnCursorLeft.setContentDescription("Button");

        Button btnCursorRight = (Button) view.findViewById(R.id.btn_cursor_right);
        btnCursorRight.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnCursorRight.setContentDescription("Button");

        Button btnFactorial = (Button) view.findViewById(R.id.btn_factorial);
        btnFactorial.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnFactorial.setContentDescription("Button");

        Button btnConstants = (Button) view.findViewById(R.id.btn_constants);
        btnConstants.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnConstants.setContentDescription("Button");

        Button btnDifferentiationOrIntegration = (Button) view.findViewById(R.id.btn_function_differentiation_integration);
        btnDifferentiationOrIntegration.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnDifferentiationOrIntegration.setContentDescription("Button");

        Button btnSquareRoot = (Button) view.findViewById(R.id.btn_function_square_root_and_cube_root);
        btnSquareRoot.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnSquareRoot.setContentDescription("Button");

        Button btnSquare = (Button) view.findViewById(R.id.btn_function_square_and_cube);
        btnSquare.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnSquare.setContentDescription("Button");

        Button btnNRoot = (Button) view.findViewById(R.id.btn_function_exponent_and_n_root);
        btnNRoot.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnNRoot.setContentDescription("Button");

        Button btnLog = (Button) view.findViewById(R.id.btn_function_log);
        btnLog.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnLog.setContentDescription("Button");

        Button btnE = (Button) view.findViewById(R.id.btn_function_e);
        btnE.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnE.setContentDescription("Button");

        Button btnPermutation = (Button) view.findViewById(R.id.btn_function_permutation);
        btnPermutation.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnPermutation.setContentDescription("Button");

        Button btnCombination = (Button) view.findViewById(R.id.btn_function_combination);
        btnCombination.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnCombination.setContentDescription("Button");

        Button btnHyp = (Button) view.findViewById(R.id.btn_function_hyp);
        btnHyp.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnHyp.setContentDescription("Button");

        Button btnSin = (Button) view.findViewById(R.id.btn_function_sin_and_sin_inverse);
        btnSin.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnSin.setContentDescription("Button");

        Button btnCos = (Button) view.findViewById(R.id.btn_function_cos_and_cost_inverse);
        btnCos.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnCos.setContentDescription("Button");

        Button btnTan = (Button) view.findViewById(R.id.btn_function_tan_and_tan_inverse);
        btnTan.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnTan.setContentDescription("Button");

        Button btnGcd = (Button) view.findViewById(R.id.btn_function_gcd);
        btnGcd.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnGcd.setContentDescription("Button");

        Button btnLcm = (Button) view.findViewById(R.id.btn_function_lcm);
        btnLcm.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnLcm.setContentDescription("Button");

        //get the operator keys
        Button btnBracketOpen = (Button) view.findViewById(R.id.btn_operator_bracket_open);
        btnBracketOpen.setTag(CalculatorFragment.TAG_OPERATORS);
        btnBracketOpen.setContentDescription("Button");

        Button btnBracketClose = (Button) view.findViewById(R.id.btn_operator_bracket_close);
        btnBracketClose.setTag(CalculatorFragment.TAG_OPERATORS);
        btnBracketClose.setContentDescription("Button");

        Button btnOperatorDivision = (Button) view.findViewById(R.id.btn_operator_division);
        btnOperatorDivision.setTag(CalculatorFragment.TAG_OPERATORS);
        btnOperatorDivision.setContentDescription("Button");

        Button btnOperatorMultiplication = (Button) view.findViewById(R.id.btn_operator_multiplication);
        btnOperatorMultiplication.setTag(CalculatorFragment.TAG_OPERATORS);
        btnOperatorMultiplication.setContentDescription("Button");

        Button btnOperatorPlus = (Button) view.findViewById(R.id.btn_operator_plus);
        btnOperatorPlus.setTag(CalculatorFragment.TAG_OPERATORS);
        btnOperatorPlus.setContentDescription("Button");

        Button btnOperatorMinus = (Button) view.findViewById(R.id.btn_operator_minus);
        btnOperatorMinus.setTag(CalculatorFragment.TAG_OPERATORS);
        btnOperatorMinus.setContentDescription("Button");

        //get other keys
        Button btnEquals = (Button) view.findViewById(R.id.btn_equals);
        btnEquals.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnEquals.setContentDescription("Button");

        Button btnAns = (Button) view.findViewById(R.id.btn_ans);
        btnAns.setTag(CalculatorFragment.TAG_NUMERIC_KEYS);
        btnAns.setContentDescription("Button");

        Button btnDot = (Button) view.findViewById(R.id.btn_dot);
        btnDot.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnDot.setContentDescription("Button");

        Button btnExp = (Button) view.findViewById(R.id.btn_function_exponent);
        btnExp.setTag(CalculatorFragment.TAG_FUNCTION_KEYS);
        btnExp.setContentDescription("Button");

        Button btnAc = (Button) view.findViewById(R.id.btn_ac);
        btnAc.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnAc.setContentDescription("Button");

        Button btnDel = (Button) view.findViewById(R.id.btn_backspace);
        btnDel.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnDel.setContentDescription("Button");

        Button btnMemorySave = (Button) view.findViewById(R.id.btn_save_memory);
        btnMemorySave.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnMemorySave.setContentDescription("Button");

        Button btnRetrieveData = (Button) view.findViewById(R.id.btn_retrieve_save);
        btnRetrieveData.setTag(CalculatorFragment.TAG_OTHER_KEYS);
        btnRetrieveData.setContentDescription("Button");

        /*The launcher_icon tint color of each button has been set in the layout file, however this only works with
        * API(s) 23 and higher. To create the same visual design for older API(s), the launcher_icon color will be of the button will
        * be set to the same color as the backgroundTint color, and the layout_margin of the buttons set  to a value 2 - 4 to give
        * gaps between the button as launcher_icon color makes the button appear joined together.
        * This is applied to every button in this fragment
        * */

        //get all the buttons in this fragment to apply their common features
        ArrayList<View> allButtons = new ArrayList<>();
        view.findViewsWithText(allButtons, "Button", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        Log.d(TAG, "There are " + allButtons.size() + " buttons in this fragment");
        for(View v : allButtons)
        {
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
           //((Button)v).setTextSize(13f);
            //v.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            //v.setBackground(getResources().getDrawable(R.drawable.round_edged_button));
        }

        //Prevents the system from bringing up the soft input when this view is touched
        //by handling its touch event
        expressionText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                Log.d(TAG, "The calculator operations editor has been clicked");
               return true;
            }
        }
        );

        //OVERRIDE FOR UNIQUE CLICK FUNCTIONS
        //This will be removed subsequently
        btnShift.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isLongClick)
                {
                    vibrate(50);
                    if(txtShift.getVisibility() == View.INVISIBLE)
                    {
                        txtShift.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Setting the text to visible");
                        Log.d(TAG, "Setting the text to invisible");
                    }else
                    {
                        txtShift.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        btnCursorLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isLongClick)
                {
                    vibrate(50);
                    int cursorPosition = expressionText.getSelectionStart();
                    String str = expressionText.getText().toString();
                    //move the cursor to the left of the editor
                    if(cursorPosition > 0) {
                        //since operators have length 3, move the cursor back 3  characters to move over the operand
                        cursorPosition = (str.charAt(cursorPosition - 1) == ' ')? cursorPosition - 3: cursorPosition - 1;
                        expressionText.setSelection(cursorPosition);
                        Log.d(TAG, "Cursor moved to left");
                    }
                    Log.d(TAG, "Cursor position is " + cursorPosition);

                    try {
                        Log.d(TAG, "The expression is " + str);
                        Log.d(TAG, "Content at selection pos: " + getContentAtIndex(str, cursorPosition - 1));
                    }catch(InvalidArgumentException arg)
                    {
                        Log.d(TAG, "Could not get content at selection pos");
                    }
                }
                isLongClick = false;
            }
        });

        btnCursorRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isLongClick)
                {
                    vibrate(50);
                    int currentCursorPosition = expressionText.getSelectionStart();
                    String str = expressionText.getText().toString();
                    //move the cursor to the right of the editor
                    if(currentCursorPosition< expressionText.length()) {
                        currentCursorPosition = (str.charAt(currentCursorPosition) == ' ')? currentCursorPosition + 3 : currentCursorPosition + 1;
                        expressionText.setSelection(currentCursorPosition);
                        Log.d(TAG, "Cursor moved to right");
                    }
                    Log.d(TAG, "Cursor position is " + currentCursorPosition);

                    try {
                        Log.d(TAG, "The expression is " + str);
                        Log.d(TAG, "Content at selection pos: " + getContentAtIndex(str, currentCursorPosition - 1));
                    }catch(InvalidArgumentException arg)
                    {
                        Log.d(TAG, "Could not get content at selection pos");
                    }
                }
                isLongClick = false;
            }
        });

        //these buttons when long clicked change the numeral system of the calculator
        this.numberKeys.get(2).setOnLongClickListener(listener);
        this.numberKeys.get(3).setOnLongClickListener(listener);
        btnOperatorPlus.setOnLongClickListener(listener);
        btnOperatorMinus.setOnLongClickListener(listener);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    /**
     * Button click handler dispatch function, when a button is clicked, this is the function that gets called.
     * After this function is called, it determines the type of button that was clicked and calls the handler
     * Method accordingly for further operations
     * Note that this method for this fragment is only applicable to buttons
     * @param view is the view button that was clicked
     */
    @Override
    public void onClick(View view)
    {
        //ignore the button click functionality if this was a long click
        //this is because this function is called after the long click method
        if(!isLongClick)
        {
            try
            {
                Button btn = (Button)view;
                vibrate(50);
                Log.d(TAG, "This button has been clicked");
            }catch(ClassCastException arg)
            {
                Log.d(TAG, "This is not a button, not vibrating");
            }

            //dispatch the call to the right handler function
            Button clickedButton = (Button) view;
            //ignore all other key presses apart from the AC key
            if(this.calculatorError)
            {
                if(clickedButton.getId() == R.id.btn_ac)
                {
                    //ac button clears the error
                    //restoring the color of the text to normal
                    this.expressionTextMain.setText("");
                    this.expressionTextMain.setTextColor(getResources().getColor(R.color.black));
                    this.calculatorError = false;
                }
            }else
            {
                switch((String)clickedButton.getTag())
                {
                    case CalculatorFragment.TAG_FUNCTION_KEYS: onFunctionKeyClicked(clickedButton);
                        break;
                    case CalculatorFragment.TAG_NUMERIC_KEYS: onNumericKeyClicked(clickedButton);
                        break;
                    case CalculatorFragment.TAG_OPERATORS: onOperatorClicked(clickedButton);
                        break;
                    case CalculatorFragment.TAG_OTHER_KEYS: onOtherKeyClicked(clickedButton);
                        break;
                }
            }
        }
        //revert flag back to normal and allow OnClick(View) events to work properly
        isLongClick = false;
    }

    /*CONTAINS BUTTON CLICK HANDLERS, METHODS ARE CALLED FROM 'onClick(View)' FUNCTION*/
    private void onFunctionKeyClicked(Button clickedButton)
    {
        String btnContent;
        switch(clickedButton.getId())
        {
            case R.id.btn_menu:
                PopupMenu popupMenu = new PopupMenu(getContext(), clickedButton);
                popupMenu.getMenuInflater().inflate(R.menu.scientific_calculator_menu, popupMenu.getMenu());
                popupMenu.show();
                break;
            case R.id.btn_function_log:
            case R.id.btn_function_hyp:
            case R.id.btn_function_sin_and_sin_inverse:
            case R.id.btn_function_cos_and_cost_inverse:
            case R.id.btn_function_tan_and_tan_inverse:
                btnContent = clickedButton.getText().toString() + "()";
                insertContentInExpression(btnContent, btnContent.length() - 1);
                break;
            case R.id.btn_function_gcd:
            case R.id.btn_function_lcm:
            case R.id.btn_function_permutation:
            case R.id.btn_function_combination:
                btnContent = clickedButton.getText().toString() + "(,)";
                insertContentInExpression(btnContent, btnContent.length() - 2);
                break;
        }
    }

    private void onNumericKeyClicked(Button clickedButton)
    {
        //concatenate the numeric key to the expressionText
        String newContent = clickedButton.getText().toString();
        insertContentInExpression(newContent, 1);
    }

    private void onOperatorClicked(Button clickedButton)
    {
        String btnContent = clickedButton.getText().toString();
        String newContent;
        switch(btnContent)
        {
            case "(":
            case ")":
                //ignore
                newContent = btnContent;
                break;
            default:
                newContent = String.format(" %s ", btnContent);
        }
        insertContentInExpression(newContent, newContent.length());
    }

    private void onOtherKeyClicked(Button clickedButton)
    {
        String currentExpression = this.expressionTextMain.getText().toString();
        StringBuilder newExpressionBuilder = new StringBuilder(currentExpression);

        switch(clickedButton.getId())
        {
            case R.id.btn_ac:
                //clear every operation and result
                this.expressionTextMain.setText("");
                this.expressionResultTextMain.setText("0");
                break;
            case R.id.btn_backspace:
                //we'll continue clearing the content as long as the next content is a whitespace
                if(currentExpression.length() > 0)
                {

                }
                break;
            case R.id.btn_equals:
                //take the current expression and calculate
                String currentInfixExpression = this.expressionTextMain.getText().toString();
                if(!(currentInfixExpression.length() <= 0))
                {
                    try
                    {
                        double result = Mathematics.calculateExpression(currentInfixExpression);
                        //display the answer
                        this.expressionResultTextMain.setText(Double.toString(result));
                    }catch(InvalidArgumentException arg)
                    {
                        //modify expression text content to signify an error has occurred
                        //set the text color to red to show its an error
                        this.expressionTextMain.setTextColor(getResources().getColor(R.color.red));
                        this.expressionTextMain.setText(arg.getMessage());
                        this.calculatorError = true;
                    }
                    //Toast.makeText(getContext(), "Calculation successful", Toast.LENGTH_SHORT).show();
                }else
                {
                    //this error should not be made visible to the user
                    Toast.makeText(getContext(), "Stack is empty", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_dot:
                //a dot can only be entered if there's been no other dot entry in the number
                //after a dot is entered another dot cannot be entered until a non-numeric character is entered
                break;
        }
    }

    @Override
    public boolean onLongClick(View view)
    {
        isLongClick = true;
        try
        {
            Button btn = (Button)view;
            vibrate(50);
            Log.d(TAG, "Long click action executed");
        }catch(ClassCastException arg)
        {
            //ignore click of other item
        }
        return false;
    }

    /**
     * Causes the device to vibrate for the specified amount of time
     * @param length
     */
    public void vibrate(int length)
    {
        Vibrator vibrator = (Vibrator)getContext().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(length);
        Log.d(TAG, "Vibrating...");
    }

    /**
     * This listener handles the changing of the calculators numeral system
     */
    View.OnLongClickListener listener = new View.OnLongClickListener()
    {
        /**
         * Handles the long click event of a button, this function is supposed to return true, informing the android
         * operating system that it has handled the event, so the system doesn't raise the OnClick(View) event.
         *
         * This implementation however return false because, by returning true, the vibrate function which causes the phone to vibrate
         * fails to vibrate the device. This means the OnClick(View) event is still raised by the system.
         * The field isLongClick is used by the OnClick(View) function to ignore the click when the value is true
         * @param view
         * @return
         */
        @Override
        public boolean onLongClick(View view)
        {
            isLongClick = true;
            Button btn = (Button)view;
            vibrate(50);
            switch(btn.getId())
            {
                case R.id.btn_2:
                    txtHex.setVisibility(View.INVISIBLE);

                    txtOct.setVisibility(View.INVISIBLE);
                    txtDec.setVisibility(View.INVISIBLE);
                    txtBin.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_3:
                    txtHex.setVisibility(View.INVISIBLE);
                    txtOct.setVisibility(View.VISIBLE);
                    txtDec.setVisibility(View.INVISIBLE);
                    txtBin.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_operator_plus:
                    txtHex.setVisibility(View.INVISIBLE);
                    txtOct.setVisibility(View.INVISIBLE);
                    txtDec.setVisibility(View.VISIBLE);
                    txtBin.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_operator_minus:
                    txtHex.setVisibility(View.VISIBLE);
                    txtOct.setVisibility(View.INVISIBLE);
                    txtDec.setVisibility(View.INVISIBLE);
                    txtBin.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    };

    @Override
    public void onStop()
    {
        super.onStop();
        ((MainActivity)getHost()).getSupportActionBar().show();

    }

    /*The following need to saved while this activity is stopping
    * txtMode indicator, expressionText state with cursor position), resultText state, */
    //This function is private because i imagine it will only ever be called from within this class
    private String getContentAtIndex(String expression, int index) throws InvalidArgumentException
    {
        if(index >= 0 && index < expression.length())
        {
            int startPos = index;
            while(startPos > 0 && expression.charAt(startPos) != ' ')
            {
                startPos--;
            }
            int endPos = index;
            while(endPos < expression.length() - 1 && expression.charAt(endPos) != ' ')
            {
                endPos++;
            }
            return expression.substring(startPos, endPos + 1).trim();
        }
        throw new InvalidArgumentException("The argument " + index + " is out of bound of the expression");
    }

    /**
     *Inserts the sub-expression at the position of the cursor and repositions the cursor to its new position,
     *depending on the content that was added.
     *@param expression is the content to isert in the new expression
     *@param cursorPosition is the index of the cursor in the content that is to be added to the expression.
     */
    private void insertContentInExpression(String expression, int cursorPosition)
    {
        //insert the expression at the position of the cursor
        int currentSelectionPosition = this.expressionTextMain.getSelectionStart();
        StringBuilder strBuilder = new StringBuilder(this.expressionTextMain.getText().toString());
        strBuilder.insert(currentSelectionPosition, expression);
        this.expressionTextMain.setText(strBuilder.toString());
        this.expressionTextMain.setSelection(currentSelectionPosition + cursorPosition);
    }
}
