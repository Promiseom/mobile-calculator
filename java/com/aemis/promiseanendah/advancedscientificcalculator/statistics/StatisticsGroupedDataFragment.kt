package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import aemis.calculator.Exceptions.InvalidArgumentException
import android.annotation.TargetApi
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.TextView
import java.lang.NullPointerException

import aemis.calculator.MCTGroupedData
import aemis.calculator.MCTGroupedData.ClassInfo
import android.view.*
import com.aemis.promiseanendah.advancedscientificcalculator.DialogListener
import com.aemis.promiseanendah.advancedscientificcalculator.DialogResult
import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity
import com.aemis.promiseanendah.advancedscientificcalculator.R

class StatisticsGroupedDataFragment : Fragment()
{
    companion object
    {
        const val DATA_LIST_KEY = "group_list_key"
        const val STAT_MEASURES_RESULT = "stat_measures_result"
    }

    private var listItems : MutableList<Group> = mutableListOf()

    /**
     * Used by the group data validator
     * */
    private var prevClassInfo : ClassInfo? = null
    private var groupedDataCalculator = MCTGroupedData()

    /**
     * Holds the result of the statistics calculation.
     */
    private var statResult : Array<Double>? = null

    /**
     * The empty layout is displayed if his value is false
     * else displays the view containing the list.
     */
    private var emptyState : Boolean = true

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.statistics_grouped_fragment_content, container, false)
    }

    /**
     * Add statistics grouped data menu
     */
    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater)
    {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.stat_grouped_data_menu, menu)
    }

    /**
     * Handles menu clicks
     */
    override fun onOptionsItemSelected(item : MenuItem) : Boolean
    {
        val itemId = item.itemId
        when(itemId)
        {
            R.id.action_calculate_result -> {
                try
                {
                    statResult = calculateResult(listItems)
                }catch(args : InvalidArgumentException)
                {
                    (Snackbar.make(view!!, args.message as CharSequence, Snackbar.LENGTH_LONG)).show()
                    return false
                }

                Log.d(StatisticsGroupedDataRawInput.TAG, "Done calculating statistical measures.")
                displayResultDialog()
            }
            R.id.action_view_stat_result -> {
                displayResultDialog()
            }
            else -> return false
        }
        return true
    }

    private fun displayResultDialog() : Boolean
    {
        //make sure the result has been calculated
//        if(statResult == null)
//        {
//            (Snackbar.make(view!!, "Cannot display result!, Data has not been calculated.", Snackbar.LENGTH_LONG)).show()
//            return false
//        }

        //view the statistical measures using the result dialog
        val resultDialog = GroupResultDialog()
        //val argument = Bundle()
        //argument.putDoubleArray(StatisticsFragment.RESULT_ARRAY, statResult!!.toDoubleArray())

        //resultDialog.arguments = argument
        resultDialog.show(fragmentManager, "Result Dialog")
        Log.d(StatisticsGroupedDataRawInput.TAG, "Displaying the statistics measures result.")
        return true
    }

    override fun onActivityCreated(instanceState: Bundle?)
    {
        super.onActivityCreated(instanceState)
        (host as MainActivity).supportActionBar?.title = "Grouped Data"

        if(instanceState == null)
        {
            listItems = arrayListOf()
        }else
        {
            listItems = instanceState.getParcelableArray(DATA_LIST_KEY).toMutableList() as MutableList<Group>
        }

        if(listItems.size < 1)
        {
            setEmptyViewState(true)

        }else
        {
            //the last item is the previous class
            val lastClassGroup = listItems[listItems.size - 1]
            prevClassInfo = ClassInfo(lastClassGroup.lBoundary, lastClassGroup.hBoundary, 10.0, lastClassGroup.freq, lastClassGroup.cumFreq)
        }

        val adapter = DataListAdapter(context, listItems)

        val fView : View? = view

        var recyclerView : RecyclerView? = fView!!.findViewById(R.id.grouped_list_container)
        recyclerView!!.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        var simpleCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean
            {
                TODO("will not be implemeted yet.") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int)
            {
                try
                {
                    listItems.removeAt(viewHolder!!.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    reOrderGroupDataList(listItems, adapter)
                    Snackbar.make(fView, "Group ${viewHolder.layoutPosition + 1} removed", Snackbar.LENGTH_SHORT).show()

                    //restore the empty state if there're no more items to display
                    if(listItems.size < 1)
                    {
                        setEmptyViewState(true)
                    }
                }catch(args : NullPointerException)
                {
                    Log.e(StatisticsGroupedDataRawInput.TAG, "Unable to completed item removal from recycler view because viewHolder is null")
                }
            }
        }
        var helper = ItemTouchHelper(simpleCallback)
        helper.attachToRecyclerView(recyclerView)

        //floating action button adds new blank group to the table
        var actionButton : FloatingActionButton? = fView?.findViewById(R.id.add_group_action_button)
        actionButton?.setOnClickListener()
        {
            //popup dialog to get group data
            val inputDialog = GroupedDataInputDialog()

            val arguments = Bundle()
            arguments.putString(GroupedDataInputDialog.DIALOG_TITLE, "Add Group Data")
            arguments.putString(GroupedDataInputDialog.GROUP_VAL_SERIAL_NO, (listItems.size + 1).toString())
            arguments.putBoolean(GroupedDataInputDialog.IS_ADDING_GROUP, true)

            //can input class size if this is the first item class group
            if(listItems.size <= 0)
            {
                arguments.putBoolean(GroupedDataInputDialog.CAN_INPUT_CLASS_SIZE, true)
            }else
            {
                arguments.putString(GroupedDataInputDialog.GROUP_VAL_CLASS_SIZE, listItems[0].mClassSize.toString())
            }

            inputDialog.arguments = arguments
            inputDialog.setOnDialogCloseListener(object : DialogListener
            {
                override fun onDialogClose(dialogResult: DialogResult) : Boolean
                {
                    if(dialogResult == DialogResult.Ok)
                    {
                        val high : Double = arguments.getString(GroupedDataInputDialog.GROUP_VAL_UPPER_BOUNDARY, "0").toDouble()
                        val low : Double = arguments.getString(GroupedDataInputDialog.GROUP_VAL_LOWER_BOUNDARY, "0").toDouble()
                        val classSize : Double = arguments.getString(GroupedDataInputDialog.GROUP_VAL_CLASS_SIZE, "0").toDouble()
                        val freq : Int = arguments.getString(GroupedDataInputDialog.GROUP_VAL_FREQUENCY, "0").toInt()
                        /*create a new class with a lower limit and a higher limit with frequency
                         set the cumulative frequency of this class based on the cumulative frequency of the previous class

                         This type will be passed to the calculator for validation, if there's an error
                         in the class, an exception will be thrown else the validation method will return true

                         The newly created class group is added to the list only if its data is valid
                        */

                        val newClassInfo = ClassInfo(low, high, classSize, freq)
                        if(prevClassInfo == null)
                        {
                            Log.d(StatisticsGroupedDataRawInput.TAG, "Validating data")
                            newClassInfo.cumFrequency = freq
                            groupedDataCalculator.validateClass(newClassInfo, newClassInfo)
                        }else
                        {
                            newClassInfo.cumFrequency = prevClassInfo!!.cumFrequency + newClassInfo.frequency
                            groupedDataCalculator.validateClass(newClassInfo, prevClassInfo!!)
                        }

                        prevClassInfo = newClassInfo

                        listItems.add(Group(listItems.size + 1, newClassInfo.lowerBoundary, newClassInfo.upperBoundary, classSize, newClassInfo.frequency, newClassInfo.classMark, newClassInfo.cumFrequency))
                        recyclerView.adapter.notifyItemInserted(listItems.size)
                        //scroll to last item in the list
                        recyclerView.smoothScrollToPosition(listItems.size)
                        Log.d(StatisticsGroupedDataRawInput.TAG, "Adding a new item")

                        //make the list visible if hidden since a group has been added
                        if(emptyState) setEmptyViewState(false)
                    }
                    return true
                }
            })
            inputDialog.show(fragmentManager, "group_item_editor_dialog")
        }
    }

    /**
     * Calculates the MCT of the grouped data.
     * Returns a single entity that contains the all the result values.
     * @param allGroups is the list of group data items that will be used to calculate
     * the final result.
     * @throws InvalidArgumentException when the list of groups passed to this method is invalid.
     * An empty group list is invalid.
     */
    private fun calculateResult(allGroups : MutableList<Group>) : Array<Double> {

        if(allGroups.isEmpty())
        {
            throw InvalidArgumentException("Cannot calculate statistical measures with empty group data.")
        }

        //since MCTGroupedData needs ClassInfo
        val classInfos = arrayListOf<ClassInfo>()

        for (gItem in allGroups) {
            classInfos.add(ClassInfo(gItem.lBoundary, gItem.hBoundary, gItem.mClassSize, gItem.freq, gItem.cumFreq))
        }

        val measures: Array<Double> = groupedDataCalculator.getStatMeasures(classInfos)
        val modeList: Array<Double> = groupedDataCalculator.getMode(classInfos)
        return measures
    }

    /**
     * Displays or hides the empty view state. A view to be displayed
     * when the recycler view is empty
     */
    private fun setEmptyViewState(state: Boolean)
    {
        val fView : View? = view

        var listContainer : View? = fView?.findViewById(R.id.view_fragment_content)
        var emptyStateContent : View? = fView?.findViewById(R.id.view_empty_content)

        if(state) listContainer?.visibility = View.INVISIBLE else listContainer?.visibility = View.VISIBLE
        if(state) emptyStateContent?.visibility = View.VISIBLE else emptyStateContent?.visibility = View.INVISIBLE

        emptyState = state
    }

    /***
     * Class representing a single class in the frequency table
     */
    private inner class Group(serialNumber : Int, lowerBoundary : Double, upperBoundary : Double, classSize : Double, frequency : Int, classMark : Double, cumFrequency : Int) : Parcelable
    {
        var sNumber : Int = serialNumber
        var lBoundary : Double = lowerBoundary
        var hBoundary : Double = upperBoundary
        var mClassSize : Double = classSize
        var freq : Int = frequency
        var groupClassMark : Double = classMark
        var cumFreq : Int = cumFrequency

        //logistic data
        var isSelected : Int = 0  //0 for false and 1 for true

        private constructor(parcel : Parcel?) : this(0, 0.0, 0.0, 0.0,0, 0.0, 0)
        {
            sNumber = parcel!!.readInt()
            lBoundary = parcel.readDouble()
            hBoundary = parcel.readDouble()
            mClassSize = parcel.readDouble()
            freq = parcel.readInt()
            groupClassMark = parcel.readDouble()
            cumFreq = parcel.readInt()
            isSelected = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel?, value: Int)
        {
            parcel?.writeInt(sNumber)
            parcel?.writeDouble(lBoundary)
            parcel?.writeDouble(hBoundary)
            parcel?.writeDouble(mClassSize)
            parcel?.writeInt(freq)
            parcel?.writeDouble(groupClassMark)
            parcel?.writeInt(cumFreq)
            parcel?.writeInt(isSelected)
        }

        override fun describeContents(): Int
        {
            return 0
        }

        @JvmField
        val CREATOR : Parcelable.Creator<Group> = object : Parcelable.Creator<Group>
        {
            override fun createFromParcel(parcel: Parcel?): Group
            {
                return Group(parcel)
            }

            override fun newArray(size: Int): Array<Group>
            {
                return newArray(size)
            }
        }
    }

    private inner class DataListAdapter(context : Context?, dataList : MutableList<Group>) : RecyclerView.Adapter<DataListAdapter.ViewHolder>()
    {
        var layoutInflater : LayoutInflater? = LayoutInflater.from(context)
        var mDataList = dataList

        @TargetApi(21)
        inner class ViewHolder(view : View, adapter : DataListAdapter) : RecyclerView.ViewHolder(view)
        {
            var mAdapter = adapter
            var viewSerialNo : TextView? = view.findViewById(R.id.txt_serial_no)
            var viewUpperBoundary : TextView? = view.findViewById(R.id.txt_upper_boundary)
            var viewLowerBoundary : TextView? = view.findViewById(R.id.txt_lower_boundary)
            var viewClassSize : TextView? = view.findViewById(R.id.txt_class_size)
            var viewClassMark : TextView? = view.findViewById(R.id.txt_class_mark)
            var viewFrequency : TextView? = view.findViewById(R.id.txt_frequency)
            var viewCumulativeFrequency: TextView? = view.findViewById(R.id.txt_cumulative_frequency)

            init
            {
                view.setOnClickListener()
                {
                    //Toast.makeText(context, "Clicked item", Toast.LENGTH_SHORT).show()
                    val mDialog = GroupedDataInputDialog()
                    val itemPos = layoutPosition

                    //set all the arguments
                    val arguments = Bundle()
                    arguments.putString(GroupedDataInputDialog.DIALOG_TITLE, "Edit Group Data")
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_SERIAL_NO, mDataList[itemPos].sNumber.toString())
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_UPPER_BOUNDARY, mDataList[itemPos].hBoundary.toString())
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_LOWER_BOUNDARY, mDataList[itemPos].lBoundary.toString())
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_CLASS_SIZE, mDataList[itemPos].mClassSize.toString())
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_FREQUENCY, mDataList[itemPos].freq.toString())
                    arguments.putString(GroupedDataInputDialog.GROUP_VAL_CUMULATIVE_FREQUENCY, mDataList[itemPos].cumFreq.toString())
                    arguments.putBoolean(GroupedDataInputDialog.IS_ADDING_GROUP, false)

                    mDialog.setOnDialogCloseListener(object : DialogListener
                    {
                        override fun onDialogClose(dialogResult: DialogResult) : Boolean
                        {
                            if(dialogResult == DialogResult.Ok)
                            {

                            }
                            return true
                        }
                    })

                    mDialog.arguments = arguments
                    mDialog.show(fragmentManager, "item_editor_dialog")
                }

                //handle group item selected
                viewSerialNo?.isClickable = true
                viewSerialNo?.setOnClickListener()
                {
                    var pos = layoutPosition
                    if(mDataList[pos].isSelected == 1)
                    {
                        //deselected
                        mDataList[pos].isSelected = 0
                    }else
                    {
                        //selected
                        mDataList[pos].isSelected = 1
                    }
                    mAdapter.notifyItemChanged(pos)
                }
            }
        }

        private fun handleItemSelection()
        {
            TODO("Implement method to handle item selection.")
        }

        private fun handleItemDeselection()
        {
            TODO("Implement method to handle the item deselection.")
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
        {
            Log.d(StatisticsGroupedDataRawInput.TAG, "Recycler list view item created")
            var view = layoutInflater!!.inflate(R.layout.grouped_data_list2_layout, parent, false)
            return ViewHolder(view, this)
        }

        override fun getItemCount(): Int
        {
            return mDataList.size
        }

        @TargetApi(21)
        override fun onBindViewHolder(holder : ViewHolder?, position: Int)
        {
            Log.d(StatisticsGroupedDataRawInput.TAG, "Data connected to view holder")
            val dataList = mDataList[position]
            holder?.viewSerialNo?.text = dataList.sNumber.toString()
            holder?.viewUpperBoundary?.text = dataList.hBoundary.toString()
            holder?.viewLowerBoundary?.text = dataList.lBoundary.toString()
            holder?.viewClassSize?.text = dataList.mClassSize.toString()
            holder?.viewClassMark?.text = dataList.groupClassMark.toString()
            holder?.viewFrequency?.text = dataList.freq.toString()
            holder?.viewCumulativeFrequency?.text = dataList.cumFreq.toString()

            //update view background based on selection
            if(mDataList[position].isSelected == 0)
            {
                //deselected
                holder?.viewSerialNo?.background = resources.getDrawable(R.drawable.ic_circle_color_accent, context.theme)
            }else
            {
                //selected
                holder?.viewSerialNo?.background = resources.getDrawable(R.drawable.ic_circle_background_color_primary, context.theme)
            }
        }
    }

    /***
     * Corrects the serial number of the data items to the correct ascending order,
     * This is useful when an item has been deleted
     */
    private fun reOrderGroupDataList(dataList : MutableList<Group>, adapter : DataListAdapter)
    {
        var index = 0
        while(index < dataList.size)
        {
            dataList[index].sNumber = index + 1
            adapter.notifyItemChanged(index)
            index++
        }
    }

    override fun onSaveInstanceState(outState : Bundle)
    {
        super.onSaveInstanceState(outState)

        //save the data item
        outState.putParcelableArray(DATA_LIST_KEY, listItems.toTypedArray())
        Log.d(StatisticsGroupedDataRawInput.TAG, "Saving instance data")
    }
}