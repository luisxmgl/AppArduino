package com.example.appaqui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etClave: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrRegistro: Button

    // 🔄 MODIFICADO AQUÍ: se usa DBHelper en lugar de UserDatabaseHelper
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnLogin = findViewById(R.id.btnLogin)
        btnIrRegistro = findViewById(R.id.btnIrRegistro)

        // 🔄 MODIFICADO AQUÍ: inicialización con DBHelper
        dbHelper = DBHelper(this)

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val clave = etClave.text.toString().trim()

            if (usuario.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.validarUsuario(usuario, clave)) {
                // Aquí está! Guardar usuario en SharedPreferences para que MainActivity lo use
                getSharedPreferences("sesion", MODE_PRIVATE)
                    .edit()
                    .putString("usuario", usuario)
                    .apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
