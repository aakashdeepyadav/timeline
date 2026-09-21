package com.example.timeline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timeline.util.DateUtils
import com.example.timeline.viewmodel.TaskViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val task = tasks.find { it.id == taskId }

    if (task == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) { Icon(Icons.Rounded.Share, null) }
                    IconButton(onClick = { /* Bookmark */ }) { Icon(Icons.Rounded.BookmarkBorder, null) }
                    IconButton(onClick = { 
                        viewModel.delete(task)
                        onNavigateBack()
                    }) { Icon(Icons.Rounded.Delete, null, tint = Color.Red) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        task.type.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (task.priority == com.example.timeline.data.local.Priority.HIGH) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Icon(Icons.Rounded.FlashOn, null, modifier = Modifier.size(12.dp), tint = Color.Red)
                            Text("High Priority", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            CountdownCard(task.date)

            Spacer(modifier = Modifier.height(24.dp))

            DetailInfoSection(task)

            if (task.subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SubtasksSection(task.subtasks)
            }

            if (!task.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Instructions & Coverage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleCompletion(task) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.isCompleted) Color(0xFF00695C) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(if (task.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (task.isCompleted) "Completed" else "Mark Complete")
                }
                
                OutlinedButton(
                    onClick = { onEditClick(task.id) },
                    modifier = Modifier.height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Rounded.Edit, null)
                }
            }
        }
    }
}

@Composable
fun CountdownCard(targetDate: Long) {
    val remaining = targetDate - System.currentTimeMillis()
    val days = TimeUnit.MILLISECONDS.toDays(remaining).coerceAtLeast(0)
    val hours = (TimeUnit.MILLISECONDS.toHours(remaining) % 24).coerceAtLeast(0)
    val mins = (TimeUnit.MILLISECONDS.toMinutes(remaining) % 60).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF3F51B5), Color(0xFF2196F3))))
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Cyan, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COUNTDOWN ACTIVE", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("$days days remaining", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeBox(days.toString(), "DAYS")
                    TimeBox(hours.toString(), "HOURS")
                    TimeBox(mins.toString(), "MINS")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = { 0.68f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color.Cyan,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun TimeBox(value: String, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), MaterialTheme.shapes.medium).padding(12.dp).width(60.dp)
    ) {
        Text(value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(unit, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun DetailInfoSection(task: com.example.timeline.data.local.TaskEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoRow(Icons.Rounded.CalendarToday, "DATE & SCHEDULE", DateUtils.formatDate(task.date))
        if (!task.location.isNullOrBlank()) {
            InfoRow(Icons.Rounded.LocationOn, "LOCATION", task.location)
        }
        if (task.reminderTime != null) {
            InfoRow(Icons.Rounded.Notifications, "REMINDERS SCHEDULED", "At time of event")
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SubtasksSection(subtasks: List<String>) {
    Text("Subtasks Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    subtasks.forEach { subtask ->
        var checked by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Text(subtask, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
