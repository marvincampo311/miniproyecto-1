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

    // Constante para identificar la acción de alternar visibilidad
    companion object {
        private const val TOGGLE_VISIBILITY_ACTION = "com.example.inventoryapp.TOGGLE_VISIBILITY"
        private var isInventoryVisible = false // Estado global para la visibilidad del inventario
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
        if (intent.action == TOGGLE_VISIBILITY_ACTION) {
            // Se ha recibido la acción de alternar visibilidad
            isInventoryVisible = !isInventoryVisible // Cambia el estado

            // Re-obtener el ID del widget para actualizarlo
            val appWidgetId = intent.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                updateAppWidget(context, appWidgetManager, appWidgetId) // Actualiza la UI
            }
        }
    }

    /**
     * Función auxiliar para actualizar la vista de un widget específico.
     * Aquí es donde realmente se modifican los elementos del layout.
     */
    @SuppressLint("RemoteViewLayout")
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // RemoteViews se utiliza para interactuar con el layout del widget,
        // ya que el widget se ejecuta en un proceso separado.
        val views = RemoteViews(context.packageName, R.layout.inventory_widget_layout)

        // Actualizar el valor del inventario y el icono de visibilidad
        if (isInventoryVisible) {
            // Si el inventario es visible, mostramos un valor de ejemplo y el icono de ojo cerrado
            views.setTextViewText(R.id.inventory_value, "💲 1234") // Valor real (simulado)
            views.setImageViewResource(R.id.toggle_visibility_icon, R.drawable.ic_visibility_off)
        } else {
            // Si está oculto, mostramos los asteriscos y el icono de ojo abierto
            views.setTextViewText(R.id.inventory_value, context.getString(R.string.hidden_inventory_value))
            views.setImageViewResource(R.id.toggle_visibility_icon, R.drawable.ic_visibility_on)
        }

        // Configurar el PendingIntent para el icono del ojo (toggle visibility)
        val toggleVisibilityIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = TOGGLE_VISIBILITY_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId, // Usamos el appWidgetId como request code para unicidad
            toggleVisibilityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.toggle_visibility_icon, togglePendingIntent)

        // Configurar el PendingIntent para el botón "Gestionar Inventario"
        // Este PendingIntent abrirá la actividad principal de la app (MainActivity).
        val appIntent = Intent(context, MainActivity::class.java) // Asegúrate de que MainActivity sea tu actividad principal
        val pendingAppIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0, // Request code
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.manage_inventory_button, pendingAppIntent)

        // Configurar el PendingIntent para el icono de configuración (engranaje)
        // Por ahora, también abrirá MainActivity, pero podrías tener una SettingsActivity.
        views.setOnClickPendingIntent(R.id.settings_icon, pendingAppIntent)


        // Indicar al AppWidgetManager que actualice el widget con las nuevas vistas
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}