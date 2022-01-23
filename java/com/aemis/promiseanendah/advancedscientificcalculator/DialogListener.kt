package com.aemis.promiseanendah.advancedscientificcalculator

enum class DialogResult{Ok, Cancel}

/**
 * Callback interface for any dialog that needs to communicate with its caller.
 *
 * Note that only dialogs that need the user to perform some action like,
 * confirmation, input or selection need to implement this interface.
 *
 * This interface is not useful for dialogs that only display information to the user.
 */
interface DialogListener
{
    /**
     * Called when the dialog is about to close.
     * @param dialogResult - Signal to the parent that to dialog operation has either been
     * completed successfully or discarded. If a dialog is meant to add a item to a list then
     * DialogResult.ok signals the item whose details has been received through the dialog should
     * be added while DialogResult.Cancel means the add operation should be cancelled.
     * @return - A return value of true means the dialogs action was successful and can be closed,
     * while a return value of false means the dialogs action has been rejected by the parent.
     *
     * Some dialogs will use this return value to determine if they'll close or not.
     */
    fun onDialogClose(dialogResult : DialogResult) : Boolean
}