// src/main/java/com/example/inventoryapp/InventoryWidgetProvider.kt
package com.example.miiproyecto1 // Asegúrate de que coincida con tu paquete

// Debe ser tu paquete. Verifica que esta línea no genere error
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.util.Log // Para mensajes de depuración
import com.example.miiproyecto1.R

/**
 * Clase principal para nuestro Widget de Inventario.
 * Extiende AppWidgetProvider para manejar los eventos del widget.
 */
class InventoryWidgetProvider : AppWidgetProvider() {

    // Constantes y variables de estado (COMPLETADO)
    companion object {
        private const val TOGGLE_VISIBILITY_ACTION = "com.example.miiproyecto1.TOGGLE_VISIBILITY"

        // AÑADE ESTA LÍNEA para definir la constante
        private const val MANAGE_INVENTORY_ACTION = "com.example.miiproyecto1.MANAGE_INVENTORY"

        // Variable global para el estado de visibilidad.
        private var isInventoryVisible = false

        // Valores simulados de inventario (Criterios 8, 9, 10)
        private const val INVENTORY_VALUE_FORMATTED = "$ 326.000,00"
        private const val HIDDEN_VALUE = "$ ****"
    }

    /**
     * Este método se llama para actualizar un widget en intervalos regulares
     * o cuando se ha configurado para actualizarse al iniciar.
     * También se llama cuando el widget se añade por primera vez.
     *
     * @param context El contexto de la aplicación.
     * @param appWidgetManager Un objeto AppWidgetManager para realizar operaciones con el widget.
     * @param appWidgetIds Un array de IDs para todos los widgets de este tipo.
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Itera sobre todos los widgets de este tipo (puede haber múltiples instancias)
        appWidgetIds.forEach { appWidgetId ->
            // Actualiza la vista de cada widget individual
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    /**
     * Este método se llama cuando un widget es eliminado del host.
     * Aquí podríamos limpiar cualquier recurso asociado a ese widget.
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d("Widget", "Widget eliminado")
    }

    /**
     * Este método se llama cuando un widget es añadido por primera vez al host.
     */
    override fun onEnabled(context: Context) {
        Log.d("Widget", "Widget habilitado")
    }

    /**
     * Este método se llama cuando la última instancia de este widget es eliminada.
     */
    override fun onDisabled(context: Context) {
        Log.d("Widget", "Widget deshabilitado")
    }

    /**
     * Este método recibe intents personalizados enviados a nuestro widget.
     * Lo usaremos para manejar el clic en el icono del ojo.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Obtener el ID del widget para la actualización
        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        when (intent.action) {
            // Criterios 7 y 10: Alternar visibilidad (icono del ojo)
            TOGGLE_VISIBILITY_ACTION -> {
                isInventoryVisible = !isInventoryVisible // Cambia el estado
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }

            // Criterio 13: Clic en el botón o icono de gestionar/ajustes
            MANAGE_INVENTORY_ACTION -> {
                // Redirigir a la "HU 2.0 Ventana Login".
                // Asumimos que esta es la MainActivity, o una LoginActivity dedicada.
                val loginIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(loginIntent)
            }
        }
    }

    /**
     * Función auxiliar para actualizar la vista de un widget específico.
     * Aquí es donde realmente se modifican los elementos del layout.
     */
    @SuppressLint("RemoteViewLayout")
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.inventory_widget_layout)

        // 1. Criterio 7 y 10: Actualizar el valor y el icono del ojo
        if (isInventoryVisible) {
            // Criterio 7: Mostrar el saldo y cambiar el icono a ojo cerrado
            views.setTextViewText(R.id.inventory_value, INVENTORY_VALUE_FORMATTED)
            views.setImageViewResource(R.id.toggle_visibility_icon, R.drawable.ic_visibility_off)
        } else {
            // Criterio 10: Ocultar el saldo y cambiar el icono a ojo abierto
            views.setTextViewText(R.id.inventory_value, HIDDEN_VALUE)
            views.setImageViewResource(R.id.toggle_visibility_icon, R.drawable.ic_visibility_on)
        }

        // 2. Configuración de PendingIntent para el icono del ojo (Criterios 7, 10)
        val toggleVisibilityIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = TOGGLE_VISIBILITY_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleVisibilityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.toggle_visibility_icon, togglePendingIntent)


        // 3. Configuración de PendingIntent para Gestión (Criterio 13)
        val manageIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = MANAGE_INVENTORY_ACTION
        }
        val managePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId + 1, // Usamos un código diferente para el botón
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Aplicar el PendingIntent al botón y al icono de ajustes (Criterio 13)
        views.setOnClickPendingIntent(R.id.manage_inventory_button, managePendingIntent)
        views.setOnClickPendingIntent(R.id.settings_icon, managePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}