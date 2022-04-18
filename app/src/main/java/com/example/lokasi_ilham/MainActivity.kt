// Memuat nama package dari aplikasi
package com.example.lokasi_ilham

// Mengimport ekstensi yang digunakan pada komponen-komponen di MainActivity.kt
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.lokasi_ilham.databinding.ActivityMainBinding
import java.util.*

//Mendemonstrasikan penggunaan API Lokasi untuk mengambil lokasi terakhir yang diketahui untuk perangkat

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    // Menyediakan titik masuk ke Fused Location Provider API
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mainBinding.btnLocation.setOnClickListener {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    // mendapatkan data lokasi dari perangkat (latitude, longitude)
                    val location: Location? = task.result
                    if (location != null) {
                        // menggunakan geocoder untuk mendapatkan data lokasi
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        //menampilkan nilai dari geocoder
                        mainBinding.apply {
                            tvLatitude.text = "Latitude\n${list[0].latitude}"
                            tvLongitude.text = "Longitude\n${list[0].longitude}"
                            tvCountryName.text = "Country\n${list[0].countryName}"
                            tvLocality.text = "District\n${list[0].locality}"
                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                        }
                    }
                }
            } else {

                // Menampilkan  Permision atau perizinan untuk mengaktifkan GPS perangkat
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    // Fungsi untuk mengecek lokasi aktif yang telah didapatkan oleh GPS
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Merupakan Boolean untuk perizinan mengambil koordinat pada lokasi perangkat
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permission untuk meminta kembali untuk mendapatkan koordinat lokasi
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    //Menampillkan Hasil dari Permintaan izin /user
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}