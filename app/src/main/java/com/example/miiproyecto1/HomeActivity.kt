package com.example.miiproyecto1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miiproyecto1.databinding.ActivityHomeBinding

/**
 * Actividad Home (Ventana Inventario)
 * Criterio HU 3.0: Muestra la pantalla de gestión de inventario.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí puedes agregar la lógica para el botón o cualquier otra interacción en la pantalla Home
        // Por ejemplo, un listener para el botón de "Gestionar Inventario"
        binding.buttonHome.setOnClickListener {
            // Ejemplo de acción: Mostrar un mensaje
            // Toast.makeText(this, "Abriendo gestión de inventario...", Toast.LENGTH_SHORT).show()
        }
    }
}

