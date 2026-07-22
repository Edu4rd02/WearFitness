package com.example.wearfitness.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.wearfitness.presentation.theme.WearFitnessTheme
import androidx.activity.result.contract.ActivityResultContracts
import com.example.shared.data.FirebaseRepository
import com.google.android.gms.wearable.Wearable
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : ComponentActivity() {
    private var heartRate by mutableIntStateOf(78)
    private var stepsGoal by mutableIntStateOf(10000)
    private lateinit var wearDataListener: WearDataListener
    private lateinit var heartRateSensorManager: HeartRateSensorManager
    private lateinit var repository: FirebaseRepository
    private var firebaseListener: ListenerRegistration? = null
    private val heartRatePermissionLauncher = registerForActivityResult(ActivityResultContracts
        .RequestPermission()) { isGranted ->
        if (isGranted){
            heartRateSensorManager.startListening()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = FirebaseRepository()
        createNotificationChannel(this)
        heartRateSensorManager = HeartRateSensorManager(context = this, onHeartRateChange = {
            newHeartRate ->
                heartRate = newHeartRate
                repository.updateHeartRate(
                    heartRate = heartRate.toLong(),
                    onSuccess = {
                        Log.d(
                            "WearableFirebase",
                            "HeartRate updated from watch"
                        )
                    },
                    onError = { exception ->
                        Log.e(
                            "WearableFirebase",
                            "Error updating heartRate",
                            exception
                        )
                    }
                )
        })

        if(heartRateSensorManager.hasHeartRateSensor && !heartRateSensorManager.hasPermission()) {
            heartRatePermissionLauncher.launch(
                heartRateSensorManager.requiredPermission
            )
        }

        wearDataListener = WearDataListener(onStepsGoalChange = { newGoal ->
            runOnUiThread { stepsGoal = newGoal }
        })
        setContent {
            WearFitnessTheme {
                WearFitnessApp(
                    heartRateSensorValue = heartRate,
                    hasHeartRateSensor = heartRateSensorManager.hasHeartRateSensor,
                    stepsGoalFromPhone = stepsGoal
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.startListening()
        }
        if (::wearDataListener.isInitialized){
            Wearable.getDataClient(this).addListener(wearDataListener)
        }

        if(::repository.isInitialized){
            startFirebaseListener()
        }
    }


    override fun onPause() {
        super.onPause()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.stopListening()
        }

        if (::wearDataListener.isInitialized){
            Wearable.getDataClient(this).removeListener(wearDataListener)
        }
        stopFirebaseListener()
    }

    private fun startFirebaseListener(){
        if(firebaseListener !== null){
            return
        }
        firebaseListener = repository.listenToFitnessData(
            onDataChanged = { fitnessData ->
                runOnUiThread {
                    stepsGoal = fitnessData.dailyGoal.toInt()
                }
            },
            onError = { exception ->
                Log.e("SharedFirebaseWear","Firebase listener error", exception)
            }
        )
    }

    private fun stopFirebaseListener(){
        firebaseListener?.remove()
        firebaseListener = null
    }
}