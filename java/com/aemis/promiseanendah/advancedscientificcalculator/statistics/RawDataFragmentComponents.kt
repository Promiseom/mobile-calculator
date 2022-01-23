package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.R
import com.aemis.promiseanendah.advancedscientificcalculator.statistics.StatisticsGroupedDataRawInput.Companion.TAG

/**
 * Represents and holds data for a single raw data item that that the user will input
 */
class RawDataItem(isSelected: Boolean, serialNo : Int, dataValue : Double, frequency : Int) : Parcelable
{
    var isSelected: Boolean = false
    // order of item or its position among other items
    var serialNo : Int = 0
    var dataValue : Double = 0.0
    var dataFrequency : Int = 0

    init
    {
        this.isSelected = isSelected
        this.serialNo = serialNo
        this.dataValue = dataValue
        this.dataFrequency = frequency
    }

    private constructor(parcel : Parcel?) : this(false,0, 0.0, 0)
    {
        isSelected = parcel!!.readInt() == 1
        serialNo = parcel!!.readInt()
        dataValue = parcel.readDouble()
        dataFrequency = parcel.readInt()
    }

    override fun writeToParcel(parcel : Parcel?, value : Int)
    {
        parcel?.writeInt(if(isSelected) 1 else 0)
        parcel?.writeInt(serialNo)
        parcel?.writeDouble(dataValue)
        parcel?.writeInt(dataFrequency)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    @JvmField
    val CREATOR : Parcelable.Creator<RawDataItem> = object : Parcelable.Creator<RawDataItem>
    {
        override fun createFromParcel(parcel : Parcel?): RawDataItem
        {
            return RawDataItem(parcel)
        }

        override fun newArray(size: Int): Array<RawDataItem>
        {
            return newArray(size)
        }
    }
}

class RawDataListAdapter(context : Context, dataItems : MutableList<RawDataItem>) : BaseAdapter()
{
    private var dataList : MutableList<RawDataItem> = mutableListOf()
    private var inflater : LayoutInflater? = null
    private var mContext : Context = context

    private var checkBox : CheckBox? = null
    private var txtSerialNumber : TextView? = null
    private var txtData : TextView? = null
    private var txtDataFrequency : TextView? = null

    init
    {
        dataList = dataItems
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItem(pos: Int): Any
    {
        return dataList[pos]
    }

    override fun getItemId(pos: Int): Long
    {
        return getItem(pos).hashCode().toLong()
    }

    override fun getCount(): Int
    {
        return dataList.size
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View?
    {
        var myView : View? = view
        if(myView == null)
        {
            //create layout
            myView = inflater?.inflate(R.layout.raw_data_item_list_layout, LinearLayout(mContext))
        }

        checkBox = myView?.findViewById(R.id.row_checkbox)
        txtSerialNumber = myView?.findViewById(R.id.serial_no)
        txtData = myView?.findViewById(R.id.data_item)
        txtDataFrequency = myView?.findViewById(R.id.data_frequency)

        myView?.isClickable = true

        checkBox?.setOnCheckedChangeListener{cb,isChecked->
            Toast.makeText(mContext, "Checkbox hashcode - ${cb.hashCode()}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Checkbox with hashcode - ${cb.hashCode()} has been clicked")
        }

        myView?.setOnClickListener(){
            Toast.makeText(mContext, "I'm at pos ${pos + 1}", Toast.LENGTH_SHORT).show()
        }

        val dataItem = getItem(pos) as RawDataItem
        txtSerialNumber?.text = dataItem.serialNo.toString()
        txtData?.text = dataItem.dataValue.toString()
        txtDataFrequency?.text = dataItem.dataFrequency.toString()

        return myView
    }

    /**
     * Works through the list of raw data items and corrects the serial number after an item is deleted
     */
    private fun correctDataListSerialNo(dataList : MutableList<RawDataItem>)
    {
        var i = 0
        while(i < dataList.size)
        {
            dataList[i].serialNo = ++i
        }
        Log.d(StatisticsGroupedDataRawInput.TAG, "Done correcting data list serial no")
    }

    /**
     * Overriden so I can know when it is called
     */
    override fun notifyDataSetChanged()
    {
        correctDataListSerialNo(dataList)
        super.notifyDataSetChanged()
        Log.d(StatisticsGroupedDataRawInput.TAG, "The change in the list adapter has been updated...")
    }

    /**
     * Attempts to edit the data item at specified position.
     * @param pos - Is the index (position) of the item to edit.
     */
    protected fun editItemAt(pos : Int, newDataItem : RawDataItem) : RawDataInputDialog
    {
        val dataItem : RawDataItem = getItem(pos) as RawDataItem

        var dialog : RawDataInputDialog = RawDataInputDialog()
        dialog.dialogTitle = "Edit Item"
        dialog.contentInputType = android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        dialog.defaultDataContent = txtData?.text as String
        dialog.defaultFrequencyContent = txtDataFrequency?.text as String
        dialog.isEditingContent = true

        dialog.setOnDialogCloseListener(object : DialogListener
        {
            override fun onDialogClose(dialogResult: DialogResult) : Boolean
            {
                if(dialogResult == DialogResult.Ok)
                {
                    dataItem.dataValue = dialog.getValue1()
                    dataItem.dataFrequency = dialog.getValue2()
                    notifyDataSetChanged()
                }
                return true
            }
        })
        return dialog
    }

    /**
     * Removes a data item at specified position
     * @param pos - Is the index (position) of the item to remove,
     */
    protected fun removeItemAt(pos : Int)
    {
        dataList.removeAt(pos)
        correctDataListSerialNo(dataList)
        //notifyDataSetChanged()
        Log.d(StatisticsGroupedDataRawInput.TAG, "Item at $pos has been deleted!")
    }
}