package com.example.appaqui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etClave: EditText
    private lateinit var btnRegistrar: Button

    // 🔄 MODIFICADO AQUÍ: se usa DBHelper en lugar de UserDatabaseHelper
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        // 🔄 MODIFICADO AQUÍ: inicialización con DBHelper
        dbHelper = DBHelper(this)

        btnRegistrar.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val clave = etClave.text.toString()

            // 🆕 VALIDACIÓN: campos vacíos
            if (usuario.isBlank() || clave.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.insertarUsuario(usuario, clave)) { // 🔄 MODIFICADO AQUÍ: función correcta
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error: usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
