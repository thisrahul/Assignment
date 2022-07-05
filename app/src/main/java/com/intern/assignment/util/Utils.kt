package com.intern.assignment.util

import android.app.ProgressDialog
import android.content.Context


 class Utils {

    //method for progressDialog
    companion object{
         fun myDialog(context: Context?): ProgressDialog? {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Please wait...")
            return progressDialog
        }
    }

}