package com.example.porterkillian.localmedia

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.porterkillian.localmedia2.CaptureActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_maps.*
import com.example.porterkillian.localmedia2.PhotoViewActivity



@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, OnMyLocationClickListener {
    var latitude: Double = 0.0
    var longitude = 0.0
    override fun onMyLocationClick(p0: Location) {

    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.porterkillian.localmedia2.R.layout.activity_maps)
        setTitle("Local Media")
        cameraButton.setOnClickListener(){
            val intent = Intent(this, CaptureActivity::class.java)
            var  loc = mMap.getMyLocation()
            intent.putExtra("latitude",loc.latitude.toString())
            intent.putExtra("longitude",loc.longitude.toString())
            startActivity(intent)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.porterkillian.localmedia2.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 123)
            return
        }
        mMap.setOnMapClickListener {
            val taskEditText = EditText(this)
            val dialog = AlertDialog.Builder(this)
                .setTitle("search size")
                .setMessage("enter a radius")
                .setView(taskEditText)
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this, PhotoViewActivity::class.java)
                    intent.putExtra("latitude", it.latitude.toString());
                    intent.putExtra("longitude", it.longitude.toString());
                    intent.putExtra("radius", taskEditText.text.toString());
                    startActivity(intent)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
                .create();
            dialog.show();
        }
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        }

    }



