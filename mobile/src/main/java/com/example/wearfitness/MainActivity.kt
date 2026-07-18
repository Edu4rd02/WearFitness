package com.example.wearfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PhoneCompanionApp()
            }
        }
    }
}

@Composable
fun PhoneCompanionApp() {
    val context = LocalContext.current
    var stepsGoal by remember {
        mutableIntStateOf(10000)
    }

    var sendStatus by remember {
        mutableStateOf("Not sent")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wear Fitness",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Steps Goal",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (stepsGoal > 500) {
                        stepsGoal -= 500
                    }
                }
            ) {
                Text("-")
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = stepsGoal.toString(),
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Button(
                onClick = {
                    stepsGoal += 500
                }
            ) {
                Text("+")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                sendStatus = "Sending..."

                sendStepsGoalToWatch(
                    context = context,
                    stepsGoal = stepsGoal,
                    onSuccess = {
                        sendStatus = "Sent $stepsGoal steps to the watch"
                    },
                    onError = { error ->
                        sendStatus = "Error: $error"
                    }
                )
            }
        ) {
            Text("Send to Watch")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Status: $sendStatus"
        )
    }
}