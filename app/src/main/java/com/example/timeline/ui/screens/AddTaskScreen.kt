package com.example.timeline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timeline.data.local.Priority
import com.example.timeline.data.local.TaskEntity
import com.example.timeline.data.local.TaskType
import com.example.timeline.ui.components.getIconForType
import com.example.timeline.util.DateUtils
import com.example.timeline.viewmodel.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    taskId: Int? = null
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val existingTask = remember(taskId, tasks) { tasks.find { it.id == taskId } }

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var date by remember { mutableLongStateOf(existingTask?.date ?: System.currentTimeMillis()) }
    var hasTime by remember { mutableStateOf(existingTask?.hasTime ?: false) }
    var time by remember { mutableLongStateOf(existingTask?.time ?: System.currentTimeMillis()) }
    var type by remember { mutableStateOf(existingTask?.type ?: TaskType.TASK) }
    var priority by remember { mutableStateOf(existingTask?.priority ?: Priority.MEDIUM) }
    var category by remember { mutableStateOf(existingTask?.category ?: "General") }
    var isReminderEnabled by remember { mutableStateOf(existingTask?.isReminderEnabled ?: false) }
    var reminderOption by remember { mutableStateOf("At time of event") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date = it }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newCalendar = Calendar.getInstance()
                    newCalendar.timeInMillis = date
                    newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                    time = newCalendar.timeInMillis
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("New Task", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                "Quick Log",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("TASK TITLE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Enter task title...") },
                trailingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("DESCRIPTION & NOTES", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Add more details...") },
                minLines = 3,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("CATEGORY TYPE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskType.entries.forEach { taskType ->
                    FilterChip(
                        selected = type == taskType,
                        onClick = { type = taskType },
                        label = { Text(taskType.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = { Icon(getIconForType(taskType), null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(20.dp))
                         Spacer(modifier = Modifier.width(8.dp))
                         Column(modifier = Modifier.weight(1f)) {
                             Text("TASK DATE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                             Text(DateUtils.formatDate(date), fontWeight = FontWeight.Bold)
                         }
                         Button(onClick = { showDatePicker = true }, shape = MaterialTheme.shapes.small) {
                             Text("Edit")
                         }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Specific Time", fontWeight = FontWeight.Bold)
                            Text("When disabled, item will appear as 'All Day'", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Switch(checked = hasTime, onCheckedChange = { hasTime = it })
                    }
                    
                    if (hasTime) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(20.dp))
                             Spacer(modifier = Modifier.width(8.dp))
                             Column(modifier = Modifier.weight(1f)) {
                                 Text("START TIME", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                 Text(DateUtils.formatTime(time), fontWeight = FontWeight.Bold)
                             }
                             Button(onClick = { showTimePicker = true }, shape = MaterialTheme.shapes.small) {
                                 Text("Edit")
                             }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("URGENCY & PRIORITY", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Priority.entries.forEach { p ->
                    PriorityOption(
                        priority = p,
                        isSelected = priority == p,
                        onSelect = { priority = p },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("NOTIFICATIONS & REMINDER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Notifications, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Task Reminder", fontWeight = FontWeight.Bold)
                        Text("Trigger notification at event time", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Switch(checked = isReminderEnabled, onCheckedChange = { isReminderEnabled = it })
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val reminderTime = if (hasTime) time else null
                        
                        val taskToSave = TaskEntity(
                            id = existingTask?.id ?: 0,
                            title = title,
                            description = if (description.isBlank()) null else description,
                            date = DateUtils.getStartOfDay(date),
                            time = if (hasTime) time else null,
                            hasTime = hasTime,
                            type = type,
                            category = if (category.isBlank()) "General" else category,
                            priority = priority,
                            isReminderEnabled = isReminderEnabled,
                            reminderTime = reminderTime,
                            createdAt = existingTask?.createdAt ?: System.currentTimeMillis()
                        )
                        
                        if (existingTask != null) {
                            viewModel.update(taskToSave)
                        } else {
                            viewModel.insert(taskToSave)
                        }
                        
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = title.isNotBlank()
            ) {
                Icon(if (existingTask != null) Icons.Rounded.Check else Icons.Rounded.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (existingTask != null) "Update Task" else "Add to Timeline", fontWeight = FontWeight.Bold)
            }
            
            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Save as Draft & Close", color = Color.Gray)
            }
        }
    }
}

@Composable
fun PriorityOption(priority: Priority, isSelected: Boolean, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onSelect,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(12.dp).background(
                    when(priority) {
                        Priority.LOW -> Color.Green
                        Priority.MEDIUM -> Color.Blue
                        Priority.HIGH -> Color.Red
                    }, CircleShape
                ))
                RadioButton(selected = isSelected, onClick = onSelect)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold)
        }
    }
}
