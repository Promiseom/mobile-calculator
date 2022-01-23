package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity
import com.aemis.promiseanendah.advancedscientificcalculator.R
import java.lang.NullPointerException

class StatisticsGroupedDataRawInput : Fragment()
{

    companion object
    {
        val TAG : String = "stat_grouped_data"
        val DB_TABLE_NAME = "frequency_table"
        val KEY_DATA_ALL_ITEMS_SELECTION = "all_item_selection"
        //col names
        val KEY_ID = "_id"
        val KEY_IS_SELECTED = "is_selected"
        val KEY_SERIAL_NO = "serial_no"
        val KEY_DATA_VALUE = "value"
        val KEY_DATA_FREQUENCY = "frequency"
    }

    //array of col names
    private val col_names = arrayListOf(KEY_ID, KEY_IS_SELECTED, KEY_SERIAL_NO, KEY_DATA_VALUE, KEY_DATA_FREQUENCY)

    private val CREATE_TABLE_STRING = "CREATE TABLE $DB_TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY, " +
            "$KEY_IS_SELECTED, $KEY_SERIAL_NO, $KEY_DATA_VALUE, $KEY_DATA_FREQUENCY);"

    private val STATISTICS_RAW_DATA_PREF_FILENAME = "stat_raw_input_filename";
    //used to save the preference mode the user selected (0 for raw data and 1 for table input)
    private val PREF_DATA_INPUT_MODE : String = "pref_data_input_mode"
    //used to save a boolean value which indicates if the input mode has alreadt been selected
    //this value should normaly be checked before proceeding to check the value of the selected mode
    private val PREF_DATA_INPUT_MODE_SELECTED : String = "pref_data_input_mode_selected"
    private val KEY_DATA_lIST : String = "data_list_raw_data_item"
    private var dataList : MutableList<RawDataItem> = mutableListOf()
    private var emptyState : Boolean = true;
    private var mMenu: Menu? = null
    // current row selection mode
    private var mIsRowSelected = false
    private var mNumSelectedRows = 0
    private var isAllItemsChecked = false
    private var allItemsCheck: CheckBox? = null
    private lateinit var mDB: GroupedDataOpenHelper

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, instanceBundle: Bundle?) : View?
    {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.stat_raw_data_input_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        (host as MainActivity).supportActionBar?.title = "Grouped Data"

        val fView : View? = view

        //open/create the database
        mDB = GroupedDataOpenHelper(context, DB_TABLE_NAME, CREATE_TABLE_STRING)

        if(savedInstanceState == null)
        {
            //display dialog for user to choose input type
            displayInputModeDialog()
            dataList = loadFragmentData()
            Log.d(MainActivity.TAG, "StatisticsGroupedDataRawInput: Instance State is null")
        }else
        {
            // restore the dataList
            try
            {
                isAllItemsChecked = if(savedInstanceState.getChar(KEY_DATA_ALL_ITEMS_SELECTION) == '1') true else false
                dataList = savedInstanceState.getParcelableArray(KEY_DATA_lIST).toMutableList<Parcelable>() as MutableList<RawDataItem>
                Log.d(TAG, "Raw Data has been restored!")
            }catch(args : ClassCastException)
            {
                dataList = mutableListOf()
                Log.e(TAG, "ClassCastException: Invalid cast operation")
            }catch(args : TypeCastException)
            {
                dataList = mutableListOf()
                Log.e(TAG, "TypeCastException: Invalid Cast Operation")
            }
        }

        setEmptyViewState(dataList.size < 1)

        //display the list
        try
        {
            val adapter = RawDataAdapter(context, dataList)

            //set an observer that will get called when the dataset changes
            //this will be used to either set the list content or to set the empty list content
            adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                setEmptyViewState(dataList.size < 1)
                Log.d(TAG, "The dataset has changed")
                }
            })

            allItemsCheck = fView?.findViewById<CheckBox>(R.id.all_items_check)
            allItemsCheck?.isChecked = isAllItemsChecked
            allItemsCheck?.setOnClickListener{
                isAllItemsChecked = !isAllItemsChecked
                for(item in dataList) { item.isSelected = isAllItemsChecked }
                adapter.notifyDataSetChanged()
                //Toast.makeText(context, "Checkbox clicked", Toast.LENGTH_SHORT).show()
            }

            val recyclerView = fView?.findViewById<RecyclerView>(R.id.raw_data_list)
            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = LinearLayoutManager(context)

            val btnAddData: FloatingActionButton? = fView?.findViewById(R.id.btn_add_data)
            btnAddData?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View)
            {
                var serialNo: Int = dataList.size

                val dialog = RawDataInputDialog()
                dialog.dialogTitle = "Add Item"
                //dialog.contentInputType = android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
                dialog.setOnDialogCloseListener(object : DialogListener
                {
                    override fun onDialogClose(dialogResult: DialogResult) : Boolean
                    {
                        if(dialogResult == DialogResult.Ok)
                        {
                            // prevent duplicate items with the same dataValue
                            var items = dataList.filter { it.dataValue == dialog.getValue1() }
                            if(items.size == 1){
                                items[0].dataFrequency += dialog.getValue2()
                                Log.d(TAG, "Frequency of a data item has been updated!")
                            }else{
                                serialNo++
                                dataList.add(RawDataItem(false, serialNo, dialog.getValue1(), dialog.getValue2()))
                                Log.d(TAG, "New Data has been added!")
                            }

                            updateAllItemsCheck()

                            adapter.notifyItemInserted(dataList.size)
                            adapter.notifyDataSetChanged()
                            //scroll to last item in the list
                            recyclerView?.smoothScrollToPosition(dataList.size)
                        }
                        return true
                    }
                })
                dialog.show(fragmentManager, "numeric_input_dialog")
            }
        })
        }catch(arg : NullPointerException)
        {
            Log.d(TAG, "Fragment View not available")
            //Toast.makeText(context, "Fragment View Not available", Toast.LENGTH_SHORT)
        }
    }

    private fun updateAllItemsCheck()
    {
        isAllItemsChecked = mNumSelectedRows == dataList.size
        allItemsCheck?.isChecked = isAllItemsChecked
    }

    private fun setEmptyViewState(state: Boolean)
    {
        val fView : View? = view

        val listContainer : View? = fView?.findViewById(R.id.list_container)
        val emptyStateContent : View? = fView?.findViewById(R.id.empty_view_content)

        if(state) listContainer?.visibility = View.INVISIBLE else listContainer?.visibility = View.VISIBLE
        if(state) emptyStateContent?.visibility = View.VISIBLE else emptyStateContent?.visibility = View.INVISIBLE

        emptyState = state
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        super.onCreateOptionsMenu(menu, inflater)
        mMenu = menu
        inflater?.inflate(R.menu.stat_grouped_raw_data_menu, menu)
        Log.d(TAG, "Options menu has been created")
        updateRowSelectionMode()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val adapter = view?.findViewById<RecyclerView>(R.id.raw_data_list)?.adapter

        when(item?.itemId){
            R.id.title -> return false
            R.id.del_selection -> {
                val removables = dataList.filter{it.isSelected}
                dataList.removeAll(removables)
                reorderItems()
            }
            R.id.cancel_selection -> {
                for(data in dataList) {data.isSelected = false}
            }
            R.id.action_create_groups-> {
                Toast.makeText(context, "Creating Group Data Distribution of from Frequency Distribution", Toast.LENGTH_LONG).show()
                return false
            }
            else-> return false
        }
        adapter?.notifyDataSetChanged()
        Log.d(TAG, "Selection has been reset")
        updateRowSelectionMode()
        return true
    }

    /**
     * Displays a dialog for user to choose input type
     */
    private fun displayInputModeDialog()
    {
        val inputModeDialog = DialogStatInputMode()
        inputModeDialog.retainInstance = true
        inputModeDialog.setOnCloseListener(object : DialogListener
        {
            override fun onDialogClose(dialogResult: DialogResult) : Boolean
            {
                try
                {
                    if (dialogResult == DialogResult.Ok)
                    {
                        if (inputModeDialog.getSelectedOption() == 1)
                        {
                            //Toast.makeText(context, "Dialog Result Ok", Toast.LENGTH_SHORT).show()
                            //FragmentManager cannot be null
                            val fm: FragmentManager = fragmentManager
                            val ft: FragmentTransaction = fm.beginTransaction()
                            val groupedStatFragment: Fragment = StatisticsGroupedDataFragment()
                            ft.replace(R.id.fragment_container, groupedStatFragment)
                            ft.commit()
                        }
                    } else
                    {
                        //Toast.makeText(context, "Dialog Result Cancel", Toast.LENGTH_SHORT).show()
                    }
                }catch(arg : NullPointerException)
                {
                    Log.d(TAG, "StatisticsGroupedDataRawInput: context is obsolete and null")
                }
                return true
            }
        })
        inputModeDialog.show(fragmentManager, "statistics_grouped_data_input_mode")
    }

    override fun onSaveInstanceState(outState : Bundle)
    {
        super.onSaveInstanceState(outState)

        //save the dataList
        outState.putChar(KEY_DATA_ALL_ITEMS_SELECTION, if(isAllItemsChecked) '1' else '0')
        outState.putParcelableArray(KEY_DATA_lIST, dataList.toTypedArray())
    }

    override fun onStop(){
        super.onStop()
        saveFragmentData()
        mDB.close()
    }

    /**
     * Saves fragment data to database
     */
    private fun saveFragmentData(){

        //if the database already contains data upgrade the database so as to change data
        mDB.onUpgrade(mDB.writableDatabase, GroupedDataOpenHelper.DB_VERSION, GroupedDataOpenHelper.DB_VERSION)

        val values = ContentValues()
        for(a in dataList){
            val isSelected = if(a.isSelected) 1 else 0
            values.put(KEY_IS_SELECTED, isSelected)
            values.put(KEY_SERIAL_NO, a.serialNo)
            values.put(KEY_DATA_VALUE, a.dataValue)
            values.put(KEY_DATA_FREQUENCY, a.dataFrequency)

            val result = mDB.writableDatabase?.insert(DB_TABLE_NAME, null, values)
            result?.equals(-1)?.not()?.let {
                Log.d(TAG, "Data has been saved")
            }
        }
    }

    /**
     * Loads the fragment data from the database
     */
    private fun loadFragmentData(queryCommand: String = ""): MutableList<RawDataItem>{
        val readDataCommand = "SELECT * FROM ${DB_TABLE_NAME} ORDER BY ${KEY_SERIAL_NO} ASC"
        val cursor = mDB.readableDatabase.rawQuery(readDataCommand, null)
        val dataList = mutableListOf<RawDataItem>()
        while(cursor.moveToNext()){
            val isSelected = cursor.getInt(cursor.getColumnIndex(KEY_IS_SELECTED)) == 1
            val serialNo  = cursor.getInt(cursor.getColumnIndex(KEY_SERIAL_NO))
            val dataItem  = cursor.getDouble(cursor.getColumnIndex(KEY_DATA_VALUE))
            val dataFrequency  = cursor.getInt(cursor.getColumnIndex(KEY_DATA_FREQUENCY))

            dataList.add(RawDataItem(isSelected, serialNo, dataItem, dataFrequency))
        }
        cursor.close()
        return dataList
    }

    private fun updateRowSelectionMode(){
        mNumSelectedRows = dataList.filter( {it.isSelected == true}).size
        // if current selection mode is different from the new mode
        val isSelected = mNumSelectedRows > 0
        if(mIsRowSelected != isSelected){
            mMenu?.let{
                it.clear()
                val menuResource = if(isSelected) R.menu.stat_grouped_selection_menu else R.menu.stat_grouped_raw_data_menu
                val fragmentTitle = if(isSelected) "" else "Grouped Data"
                activity.menuInflater.inflate(menuResource, mMenu)
                (host as MainActivity).supportActionBar?.title = fragmentTitle
                mIsRowSelected = isSelected
            }
        }
        if(isSelected) { mMenu?.getItem(0)?.title = mNumSelectedRows.toString() }
    }

    private fun setSelectionHighLightMode(rowParent: View, isSelected: Boolean){
        val color = if(isSelected) R.color.colorPrimary else R.color.transparent
//        rowParent.setBackgroundColor(color)
    }

    private fun reorderItems(){
        var serialNo = 1
        for(item in dataList) { item.serialNo = serialNo++}
        view?.findViewById<RecyclerView>(R.id.raw_data_list)?.adapter?.notifyDataSetChanged()
    }

    inner class RawDataAdapter(context: Context, dataList: MutableList<RawDataItem>): RecyclerView.Adapter<RawDataAdapter.ViewHolder>(){

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mContext = context
        val mDataList = dataList

        inner class ViewHolder(view: View, adapter: RawDataAdapter): RecyclerView.ViewHolder(view){
            val mView = view
            val checkbox = mView.findViewById<CheckBox>(R.id.row_checkbox)
            val seriaNo = mView.findViewById<TextView>(R.id.serial_no)
            val dataValue = mView.findViewById<TextView>(R.id.data_item)
            val dataFrequency = mView.findViewById<TextView>(R.id.data_frequency)

            init{
                mView.isClickable = true
                mView.setOnClickListener{
                    //Toast.makeText(mContext, "List $layoutPosition has been clicked", Toast.LENGTH_SHORT).show()
                    val dialog = RawDataInputDialog()
                    dialog.dialogTitle = "Edit Item"
                    dialog.defaultDataContent =  mDataList[layoutPosition].dataValue.toString()
                    dialog.defaultFrequencyContent =  mDataList[layoutPosition].dataFrequency.toString()

                    dialog.setOnDialogCloseListener(object: DialogListener{
                        override fun onDialogClose(dialogResult: DialogResult): Boolean {
                            if(dialogResult == DialogResult.Ok){
                                // prevent duplicate items with the same datavalue
                                val items = mDataList.filter{ it.dataValue == dialog.getValue1() }
                                if(items.size == 1 && !mDataList[layoutPosition].equals(items[0]))
                                {
                                    items[0].dataFrequency += dialog.getValue2()
                                    mDataList.removeAt(layoutPosition)
                                    reorderItems()
                                    updateAllItemsCheck()
                                    updateRowSelectionMode()
                                }else
                                {
                                    mDataList[layoutPosition].dataValue = dialog.getValue1()
                                    mDataList[layoutPosition].dataFrequency = dialog.getValue2()
                                }
                                adapter.notifyItemRemoved(layoutPosition)
                                adapter.notifyDataSetChanged()
                            }
                            return true
                        }
                    })
                    dialog.show(fragmentManager, "numeric_input_dialog")
                }
                checkbox?.setOnCheckedChangeListener{_,isChecked->
                    mDataList[layoutPosition].isSelected = isChecked
                    updateRowSelectionMode()
                    setSelectionHighLightMode(checkbox.parent as View, isChecked)
                    updateAllItemsCheck()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            Log.d(StatisticsGroupedDataRawInput.TAG, "An item has been added!")
            val view = inflater.inflate(R.layout.raw_data_item_list_layout, parent, false)

            //remove the root linear layout in the inflated view so we can use the Layoutmanager
            val myView = view as LinearLayout
            myView.removeView(myView.findViewById(R.id.data_row))

            return ViewHolder(myView, this)
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val dataItem = mDataList[position]

            holder?.checkbox?.isChecked = dataItem.isSelected
            holder?.seriaNo?.text = dataItem.serialNo.toString()
            holder?.dataValue?.text = dataItem.dataValue.toString()
            holder?.dataFrequency?.text = dataItem.dataFrequency.toString()
            Log.d(StatisticsGroupedDataRawInput.TAG, "Binding view")
        }
    }
}