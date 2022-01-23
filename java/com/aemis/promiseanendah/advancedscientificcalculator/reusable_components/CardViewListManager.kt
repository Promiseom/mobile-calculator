package com.aemis.promiseanendah.advancedscientificcalculator.reusable_components

import android.annotation.TargetApi
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import com.aemis.promiseanendah.advancedscientificcalculator.MainActivity
import com.aemis.promiseanendah.advancedscientificcalculator.R
import com.aemis.promiseanendah.advancedscientificcalculator.statistics.StatisticsGroupedDataRawInput
import java.lang.NullPointerException

/**
 * Contains methods that can be used to set all the components that need to created externally.
 * This components will be created externally and then added to this class using this interface buttons.
 */
class CardViewListManager : Fragment()
{
    companion object
    {
        //key used to save the list in preferences and instance bundle
        const val DATA_LIST_KEY = "list_key"
        const val FRAGMENT_TITLE = "fragment_title_key";
    }

    /**
     * Contains a list of the data items (items to be displayed).
     */
    protected var listItems : MutableList<Any> = mutableListOf()

    /**
     * The empty layout is displayed if his value is false
     * else displays the view containing the list.
     */
    protected var emptyState : Boolean = true

    protected var fragmentTitle : String = ""

    override fun onCreate(instanceState : Bundle?)
    {
        super.onCreate(instanceState)
        fragmentTitle = arguments.getString(FRAGMENT_TITLE, "Title")
    }

    private fun initListState(savedInstance : Bundle?)
    {
        if(savedInstance == null)
        {
            listItems = arrayListOf()
        }else
        {
            //attempt to restore the data items when the device orientation
            listItems = savedInstance.getParcelableArray(DATA_LIST_KEY).toMutableList() as MutableList<Any>
        }

        if(listItems.size < 1)
        {
            setEmptyViewState(true)
        }
    }

    override fun onActivityCreated(instanceState: Bundle?)
    {
        super.onActivityCreated(instanceState)
        (host as MainActivity).supportActionBar?.title = fragmentTitle

        initListState(instanceState)

        val adapter = DataListAdapter(context, listItems)

        val fView : View? = view

        var recyclerView : RecyclerView? = fView!!.findViewById(R.id.grouped_list_container)
        recyclerView!!.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        var simpleCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean
            {
                TODO("will not be implemented yet.") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int)
            {
                try
                {
                    listItems.removeAt(viewHolder!!.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)

                    TODO("Inform listener, item has been removed from list.")

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
    }

    /**
     * Displays or hides the empty view state. A view to be displayed
     * when the recycler view is empty
     */
    private fun setEmptyViewState(state: Boolean) {
        val fView: View? = view

        var listContainer: View? = fView?.findViewById(R.id.view_fragment_content)
        var emptyStateContent: View? = fView?.findViewById(R.id.view_empty_content)

        if (state) listContainer?.visibility = View.INVISIBLE else listContainer?.visibility = View.VISIBLE
        if (state) emptyStateContent?.visibility = View.VISIBLE else emptyStateContent?.visibility = View.INVISIBLE

        emptyState = state
    }

    protected inner class DataListAdapter(context : Context?, dataList : MutableList<Any>) : RecyclerView.Adapter<DataListAdapter.ViewHolder>()
    {
        var layoutInflater : LayoutInflater? = LayoutInflater.from(context)
        var mDataList = dataList

        /**
         * For custom implementation.
         */
        @TargetApi(21)
        inner class ViewHolder(view : View, adapter : DataListAdapter) : RecyclerView.ViewHolder(view)

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
            ////////////////////////CUSTOM IMPLEMENTATION////////////////////////
        }
    }

    override fun onSaveInstanceState(outState : Bundle)
    {
        super.onSaveInstanceState(outState)

        //save the data item
        //outState.putParcelableArray(DATA_LIST_KEY, listItems.toTypedArray())
        Log.d(StatisticsGroupedDataRawInput.TAG, "Saving instance data")
    }
}