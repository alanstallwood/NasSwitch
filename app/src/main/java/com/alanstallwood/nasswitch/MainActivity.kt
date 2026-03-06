package com.alanstallwood.nasswitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alanstallwood.nasswitch.navigation.NasDestinations
import com.alanstallwood.nasswitch.ui.NasConfigScreen
import com.alanstallwood.nasswitch.ui.NasScreen
import com.alanstallwood.nasswitch.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as NasApplication).container

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel = container.createViewModel()
                val dataStore = container.getDataStore()
                val secureKeyStore = container.getSecureKeyStore()
                val networkUtils = container.getNetworkUtils();

                NavHost(
                    navController = navController,
                    startDestination = NasDestinations.NAS_SCREEN
                ) {
                    composable(NasDestinations.NAS_SCREEN) {
                        NasScreen(
                            viewModel = viewModel,
                            onConfigClick = { navController.navigate(NasDestinations.CONFIG_SCREEN) }
                        )
                    }

                    composable(NasDestinations.CONFIG_SCREEN) {
                        NasConfigScreen(
                            dataStore = dataStore,
                            secureKeyStore = secureKeyStore,
                            networkUtils = networkUtils,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
