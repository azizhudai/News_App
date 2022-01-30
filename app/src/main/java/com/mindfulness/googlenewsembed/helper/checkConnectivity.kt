package com.mindfulness.googlenewsembed.helper

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mindfulness.googlenewsembed.R

fun checkConnectivity(activity: AppCompatActivity) {
    val manager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = manager.activeNetworkInfo

    if (null == activeNetwork) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val intent = Intent(activity, activity::class.java)
        // set message of alert dialog
        dialogBuilder.setMessage("Make sure that WI-FI or mobile data is turned on, then try again")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, id ->
                ActivityCompat.recreate(activity)
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                activity.finish()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("No Internet Connection")
        alert.setIcon(R.drawable.news_logo)
        // show alert dialog
        alert.show()
    }
}
fun isConnectivity(activity: AppCompatActivity):Boolean{
    val manager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = manager.activeNetworkInfo
    return null != activeNetwork
}