package com.example.timeline.ui.screens

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timeline.ui.theme.TaskTrackerTheme
import com.example.timeline.util.AlarmService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task Reminder"

        setContent {
            TaskTrackerTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "ALARM",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 4.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = taskTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        Button(
                            onClick = {
                                dismissAlarm(taskId)
                            },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("DISMISS", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        }
                    }
                }
            }
        }
    }

    private fun dismissAlarm(taskId: Int) {
        // 1. Stop the ringing service
        val serviceIntent = Intent(this, AlarmService::class.java)
        stopService(serviceIntent)

        // 2. Cancel the standard notification (from AlarmReceiver)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (taskId != -1) {
            notificationManager.cancel(taskId)
        }

        // 3. Clear repeat logic in DB
        val database = com.example.timeline.data.local.AppDatabase.getDatabase(this)
        @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.IO) {
            val task = database.taskDao().getTaskById(taskId)
            if (task != null) {
                database.taskDao().updateTask(task.copy(reminderRepeatCount = 0))
            }
        }

        // 4. Close the activity
        finish()
    }
}
