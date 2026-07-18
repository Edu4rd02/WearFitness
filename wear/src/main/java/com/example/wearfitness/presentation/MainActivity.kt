package com.example.wearfitness.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.wearfitness.presentation.theme.WearFitnessTheme
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private var heartRate by mutableIntStateOf(78)
    private lateinit var heartRateSensorManager: HeartRateSensorManager
    private val heartRatePermissionLauncher = registerForActivityResult(ActivityResultContracts
        .RequestPermission()) { isGranted ->
        if (isGranted){
            heartRateSensorManager.startListening()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        heartRateSensorManager = HeartRateSensorManager(context = this, onHeartRateChange = {
            newHeartRate -> heartRate = newHeartRate
        })

        if(heartRateSensorManager.hasHeartRateSensor && !heartRateSensorManager.hasPermission()) {
            heartRatePermissionLauncher.launch(
                heartRateSensorManager.requiredPermission
            )
        }
        setContent {
            WearFitnessTheme {
                WearFitnessApp(
                    heartRateSensorValue = heartRate,
                    hasHeartRateSensor = heartRateSensorManager.hasHeartRateSensor
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.startListening()
        }
    }


    override fun onStop() {
        super.onStop()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.stopListening()
        }
    }


}