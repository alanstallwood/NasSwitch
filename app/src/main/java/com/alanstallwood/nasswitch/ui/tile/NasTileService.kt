package com.alanstallwood.nasswitch.ui.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.alanstallwood.nasswitch.NasApplication
import com.alanstallwood.nasswitch.domain.repository.NasRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NasTileService : TileService() {
    
    private val repository: NasRepository
        get() = (application as NasApplication).container.repository

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    @Volatile private var isBusy = false

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    private fun updateTileState() {
        if (isBusy) return

        serviceScope.launch {
            val state = repository.getState()
            qsTile.state = if (state.online) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.label = if (state.online) "NAS ON" else "NAS OFF"
            qsTile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        if (isBusy) return
        isBusy = true

        serviceScope.launch {
            val state = repository.getState()
            if (state.online) {
                repository.shutdown()
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "NAS shutting down…", Toast.LENGTH_SHORT).show()
                }
            } else {
                repository.wake()
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "NAS waking up…", Toast.LENGTH_SHORT).show()
                }
            }

            updateTileState()
            isBusy = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
