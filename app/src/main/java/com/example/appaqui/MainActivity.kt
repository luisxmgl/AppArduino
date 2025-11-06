package com.example.appaqui

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.app.PendingIntent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.net.HttpURLConnection
import java.net.URL // 🆕 AÑADIDO

class MainActivity : AppCompatActivity() {

    private lateinit var tvDistance: TextView
    private lateinit var tvLedStatus: TextView
    private lateinit var btnActivarAlerta: Button
    private lateinit var btnDesactivarAlerta: Button
    private lateinit var btnAlarmaManualOn: Button
    private lateinit var btnAlarmaManualOff: Button
    private lateinit var btnCerrarSesion: Button

    private lateinit var imgInacapLogo: ImageView
    private lateinit var tapSound: MediaPlayer
    private lateinit var alarmaSound: MediaPlayer

    private var alertaActiva = false
    private var alarmaPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var serialPort: UsbSerialPort? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val usuario = prefs.getString("usuario", null)
        if (usuario == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDistance = findViewById(R.id.tvDistance)
        tvLedStatus = findViewById(R.id.tvLedStatus)
        btnActivarAlerta = findViewById(R.id.btnActivarAlerta)
        btnDesactivarAlerta = findViewById(R.id.btnDesactivarAlerta)
        btnAlarmaManualOn = findViewById(R.id.btnAlarmaManualOn)
        btnAlarmaManualOff = findViewById(R.id.btnAlarmaManualOff)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        imgInacapLogo = findViewById(R.id.imgInacapLogo)

        tapSound = MediaPlayer.create(this, R.raw.tap)
        alarmaSound = MediaPlayer.create(this, R.raw.alarma)
        alarmaSound.isLooping = true

        tvDistance.text = getString(R.string.distancia_default)
        tvLedStatus.text = getString(R.string.led_apagado)

        connectToArduino()

        btnActivarAlerta.setOnClickListener {
            tapSound.start()
            alertaActiva = true
            tvLedStatus.text = "Modo alerta activado"
            enviarComando("ON") // 🔄 MODIFICADO AQUÍ
        }

        btnDesactivarAlerta.setOnClickListener {
            tapSound.start()
            alertaActiva = false
            tvLedStatus.text = "Modo alerta desactivado"
            stopAlarma()
            enviarComando("OFF") // 🔄 MODIFICADO AQUÍ
        }

        btnAlarmaManualOn.setOnClickListener {
            tapSound.start()
            startAlarma()
            enviarComando("BUZZERON") // 🔄 MODIFICADO AQUÍ
        }

        btnAlarmaManualOff.setOnClickListener {
            tapSound.start()
            stopAlarma()
            enviarComando("BUZZEROFF") // 🔄 MODIFICADO AQUÍ
        }

        btnCerrarSesion.setOnClickListener {
            getSharedPreferences("sesion", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 🆕 DEPURACIÓN: mostrar usuarios registrados en Logcat
        val db = DBHelper(this)
        val lista = db.obtenerTodosLosUsuarios()
        android.util.Log.d("USUARIOS_REGISTRADOS", lista.joinToString())



        startReadingLoop()
    }

    private fun connectToArduino() {
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) return

        val driver: UsbSerialDriver = availableDrivers[0]

        // 🔄 MODIFICADO AQUÍ: añadido FLAG_IMMUTABLE para evitar warning en Android 12+
        val permissionIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("com.example.appaqui.USB_PERMISSION"),
            PendingIntent.FLAG_IMMUTABLE
        )

        usbManager.requestPermission(driver.device, permissionIntent)

        val connection = usbManager.openDevice(driver.device) ?: return

        serialPort = driver.ports[0]
        serialPort?.open(connection)
        serialPort?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    }

    private fun sendToArduino(message: String) {
        serialPort?.write((message + "\n").toByteArray(), 1000)
    }

    // 🔄 MODIFICADO AQUÍ: ahora lee distancia desde Arduino vía WiFi
    private fun readFromArduino(): Int? {
        val ipArduino = "192.168.1.123" // ← reemplaza con la IP real del Monitor Serial
        val url = "http://$ipArduino/distancia"

        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 1000
            connection.readTimeout = 1000
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().readText()
            val value = response.trim().removePrefix("DISTANCIA:").trim()
            value.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun startReadingLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val distance = readFromArduino()
                if (distance != null) {
                    tvDistance.text = "Distancia: $distance cm"
                    if (alertaActiva && distance < 10) {
                        tvLedStatus.text = getString(R.string.movimiento_detectado)
                        startAlarma()
                    } else if (alertaActiva) {
                        tvLedStatus.text = getString(R.string.led_apagado)
                        stopAlarma()
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun startAlarma() {
        if (!alarmaPlaying) {
            alarmaSound.start()
            alarmaPlaying = true
        }
    }

    private fun stopAlarma() {
        if (alarmaPlaying) {
            alarmaSound.pause()
            alarmaSound.seekTo(0)
            alarmaPlaying = false
        }
    }

    // 🆕 AÑADIDO: función para enviar comandos al Arduino vía WiFi
    private fun enviarComando(comando: String) {
        val ipArduino = "192.168.1.123" // ← reemplaza con la IP real
        val url = "http://$ipArduino/$comando"

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                connection.inputStream.close()
            } catch (_: Exception) {}
        }.start()
    }
}
