package com.example.appaqui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "usuarios.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT UNIQUE, clave TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun registrarUsuario(usuario: String, clave: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("usuario", usuario)
            put("clave", clave)
        }
        return db.insert("usuarios", null, valores) != -1L
    }

    fun validarUsuario(usuario: String, clave: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE usuario = ? AND clave = ?",
            arrayOf(usuario, clave)
        )
        val valido = cursor.count > 0
        cursor.close()
        return valido
    }
}
