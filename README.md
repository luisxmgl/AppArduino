# AppArduino

Aplicación Android desarrollada en Kotlin que permite el registro e inicio de sesión de usuarios utilizando una base de datos local SQLite. Está diseñada para integrarse con hardware Arduino mediante comunicación por WiFi o USB, permitiendo el envío de comandos desde la app hacia el dispositivo.

## Funcionalidades principales
- Registro de nuevos usuarios con validación de campos
- Inicio de sesión con verificación contra la base de datos local
- Persistencia de datos mediante SQLite (`Usuarios.db`)
- Visualización de usuarios registrados en Logcat para depuración
- Comunicación con Arduino mediante comandos simples (`ON`, `OFF`, `BUZZERON`)
- Diseño modular que facilita la migración entre simulación y hardware real
- Eliminación de usuarios (desactivada en esta versión para fines de entrega)

## Requisitos técnicos
- Android Studio Arctic Fox o superior
- SDK mínimo: 31
- Kotlin como lenguaje principal
- Arduino con módulo WiFi (ESP8266/ESP32) o conexión USB
- Permisos declarados en `AndroidManifest.xml` para comunicación serial y red

## Estructura del proyecto
- `app/src/main/java/...`: Código fuente Kotlin (`MainActivity.kt`, `DBHelper.kt`, etc.)
- `app/src/main/res/layout/`: Interfaces XML
- `app/src/main/res/values/`: Recursos como `strings.xml`
- `app/src/main/AndroidManifest.xml`: Declaración de actividades y permisos
- `Usuarios.db`: Base de datos SQLite (opcional para revisión)

## Autor
Luis Inostroza  
[luis.inostroza1998@gmail.com](mailto:luis.inostroza1998@gmail.com)

Proyecto desarrollado como entrega académica, con enfoque en modularidad, integración con hardware y buenas prácticas de desarrollo Android.
