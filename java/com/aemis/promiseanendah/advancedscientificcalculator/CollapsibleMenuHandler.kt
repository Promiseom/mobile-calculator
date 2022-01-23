//author Promise Anendah
//anendahpromise@gmail.com
//date

package com.aemis.promiseanendah.advancedscientificcalculator

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.AsyncTask
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View

/**
 * CollapsibleMenuHandler 1.0
 * Should only be used with Navigation view
 * Collapse (hide) or show MenuItems when a particular item is clicked
 * Handles the collapsing of a single group
 * Menu items are collapsed by default
 */
class CollapsibleMenuHandler(mItem : MenuItem, navMenu : Menu)
{
    private var groupId = mItem.groupId
    private var parentMenuItem = mItem
    private var navMenu = navMenu
    private var isMakeVisible = true

    //list containing the menu items whose visibility will be toggled
    private var groupItems : MutableList<MenuItem> = mutableListOf()

    init
    {
        Log.d(MainActivity.TAG, "Preparing collapsible menu...")
        //set the action view of the menu item that will be animated on toggle
        //parentMenuItem.setActionView(actionViewId)
        parentMenuItem.isCheckable = false
        parentMenuItem.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener
        {
            override fun onMenuItemClick(p0: MenuItem?): Boolean
            {
                Log.d(MainActivity.TAG, "Menu Item has been clicked")
                toggleMenuVisibility()
                return true
            }
        })

        //search the entire menu/submenus and add to groupItem list any item with matching groupId
        //parentMenuItem will be exempted from the list since it will always be visible
        var i = 0
        while(i < this.navMenu.size())
        {
            var item : MenuItem = this.navMenu.getItem(i)
            if(item.itemId == parentMenuItem.itemId)
            {
                i++
                //don't pick this item
                continue
            }

            //check item has a submenu
            if(item.hasSubMenu())
            {
                //check all the items in the submenu
                var subMenu : SubMenu = item.subMenu
                var j = 0
                while(j < subMenu.size())
                {
                    var subItem : MenuItem = subMenu.getItem(j)
                    if(subItem.itemId == parentMenuItem.itemId)
                    {
                        j++
                        //don't pick this item
                        continue
                    }

                    if(subItem.groupId == this.groupId)
                    {
                        subItem.isVisible = false
                        this.groupItems.add(subItem)
                    }
                    j++
                }
            }else
            {
                if(item.groupId == this.groupId)
                {
                    item.isVisible = false
                    this.groupItems.add(item)
                }
            }
            i++
        }
        Log.d(MainActivity.TAG, "Group Items Size: " + menuItemsLength())
    }

    /**
     * Returns the number of items in the menu.
     */
    fun menuItemsLength() : Int { return groupItems.size }

    /**
     * Reveals all the menu items that are hidden and hides the visible.
     */
    fun toggleMenuVisibility()
    {
        var anime: ObjectAnimator? = null
        if (isMakeVisible) {
            Log.d(MainActivity.TAG, "Revealing menu items")
            anime = ObjectAnimator.ofFloat(parentMenuItem.actionView, View.ROTATION.name, 0f, -180f)
        } else {
            Log.d(MainActivity.TAG, "Collapsing menu")
            anime = ObjectAnimator.ofFloat(parentMenuItem.actionView, View.ROTATION.name, -180f, 0f)
        }

        //anime.setInterpolator { Float -> Float }
        //anime.duration = 200
       anime?.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationRepeat(p0: Animator?)
            {
            }

            override fun onAnimationEnd(p0: Animator?)
            {
                //toggle visibility of the menu items
                var toggleAnime : AnimeMenuToggler = AnimeMenuToggler(isMakeVisible)
                toggleAnime.execute(groupItems)
                isMakeVisible = !isMakeVisible
            }

            override fun onAnimationCancel(p0: Animator?)
            {
            }

            override fun onAnimationStart(p0: Animator?)
            {
            }
        })
        anime.start()
    }

    public class AnimeMenuToggler(isToggleOn : Boolean) : AsyncTask<MutableList<MenuItem>, MenuItem, Void?>()
    {
        //is the menu collapsed or not
        private var isToggleOn : Boolean
        init
        {
            this.isToggleOn = isToggleOn
        }

        override fun onProgressUpdate(vararg values: MenuItem)
        {
            values[0].isVisible = isToggleOn
        }

        override fun doInBackground(vararg p0: MutableList<MenuItem>): Void?
        {
            for(item in p0[0])
            {
                Log.d(MainActivity.TAG, "Setting visibility of menu item to " + isToggleOn)
                publishProgress(item)
                if(isToggleOn)
                {
                    try
                    {
                        Thread.sleep(20)
                    }catch(e : InterruptedException)
                    {
                        //ignore the interrupt
                    }
                }
            }
            return null
        }
    }
}