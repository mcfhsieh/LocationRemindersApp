package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var reminder: ReminderDataItem
    private lateinit var binding: ActivityReminderDescriptionBinding

    companion object {

        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        reminder = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.reminderDataItem = reminder
//        DONE: Add the implementation of the reminder details

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.reminder_detail_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.root

    }

    override fun onBackPressed() {
        if (intent.action == "notification") {
            val backToStartIntent = Intent(this, RemindersActivity::class.java)
            startActivity(backToStartIntent)
        }else super.onBackPressed()

    }

    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        val latLng = LatLng(reminder.latitude!!, reminder.longitude!!)
//        map.addCircle(CircleOptions()
//            .center(latLng)
//            .radius(SaveReminderFragment.GEOFENCE_RADIUS_IN_METERS.toDouble())
//            .fillColor(R.color.pink)
//            .strokeColor(R.color.colorAccent)
//            .strokeWidth(GEOFENCE_STROKE_WIDTH)
//        )
        val locationMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(reminder.location)
        )
        locationMarker.showInfoWindow()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
