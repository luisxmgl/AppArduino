package com.example.appaqui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Usuarios.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT UNIQUE, clave TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun insertarUsuario(usuario: String, clave: String): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("usuario", usuario)
            put("clave", clave)
        }
        return db.insert("usuarios", null, valores) != -1L
    }

    fun validarUsuario(usuario: String, clave: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario=? AND clave=?", arrayOf(usuario, clave))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // 🆕 AÑADIDO: función para obtener todos los usuarios registrados
    fun obtenerTodosLosUsuarios(): List<String> {
        val usuarios = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT usuario FROM usuarios", null)
        while (cursor.moveToNext()) {
            usuarios.add(cursor.getString(0))
        }
        cursor.close()
        return usuarios
    }

    // 🆕 AÑADIDO: función para eliminar un usuario por nombre
    fun eliminarUsuario(usuario: String): Boolean {
        val db = this.writableDatabase
        val filasAfectadas = db.delete("usuarios", "usuario = ?", arrayOf(usuario))
        return filasAfectadas > 0
    }
}
