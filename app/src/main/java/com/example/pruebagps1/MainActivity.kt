package com.example.pruebagps1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// Etiqueta que identifica el activity Actual. Puede ser cualquier texto.
private const val TAG = "MainActivity"
// Código que identifica la solicitud del permiso de localización. Puede ser cualquier número.
private const val CODIGO_SOLICITUD_PERMISO_UBICACION = 99

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var btnObtenerCoordenadas: Button

    private lateinit var etLongitud: EditText

    private lateinit var etLatitud: EditText

    private lateinit var etAltitud: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnObtenerCoordenadas = findViewById(R.id.btnObtenerCoordenadas)
        etLatitud = findViewById(R.id.etLatitud)
        etLongitud = findViewById(R.id.etLongitud)
        etAltitud = findViewById(R.id.etAltitude)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnObtenerCoordenadas.setOnClickListener {
            Log.d(TAG, "Click para obtener la ultima ubicacion")
            if (verificarPermisos()) {
                obtenerCoordenadas()
            }
            else {
                solicitarPermisos()
            }
        }
    }

    /**
     * Función que obtiene la última posición conocida.
     *
     * Se agregó el decorator @SuppressLint("MissingPermission") para evitar un warning que advierte
     * que es necesaria la verificación de permisos para acceder a la ubicación, pero esta
     * función se llama desde varios puntos donde la verficación de permisos se hace a través de
     * otra función.
     */
    @SuppressLint("MissingPermission")
    private fun obtenerCoordenadas() {
        Log.d(TAG, "Leer ultimas coordenadas")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Obtener la última localización conocida
                etLatitud.setText(location?.latitude.toString())
                etLongitud.setText(location?.longitude.toString())
                etAltitud.setText(location?.altitude.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Verifica que el usuario otorgue los permisos para acceder a la ubicación
     */
    private fun verificarPermisos(): Boolean {

        Log.d(TAG, "Verificar permisos")

        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * Solicita al usuario su autorización para el permiso de acceso a la ubicación
     */
    private fun solicitarPermisos() {
        Log.d(TAG, "Solicitar permisos")

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            CODIGO_SOLICITUD_PERMISO_UBICACION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        /**
         * Esta función es llamada luego de la solicitud de permisos para acceso a localización.
         * -  En primer lugar se verifica que el código de la solicitud de permisos corresponda a una
         *    solicitud de permisos de localización.
         * -  Luego se verifica cual fue el resultado de la solicitud:
         *        - Si se canceló la solicitud, el array de permisos otorgados está vacío
         *        - Si el primer elemento en el array de permisos otorgados es igual a un permiso
         *          otorgado se procede con la operación de obtener la coordenada actual.
         *        - Para cualquier otro caso, la solicitud de permisos fue denegada
         */
        when(requestCode) {
            CODIGO_SOLICITUD_PERMISO_UBICACION -> when {
                grantResults.isEmpty() -> Log.d(TAG, "Solicitud cancelada")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> obtenerCoordenadas()
                else -> Toast.makeText(this, "Los permisos son necesarios :(", Toast.LENGTH_LONG).show()
            }
        }
    }
}