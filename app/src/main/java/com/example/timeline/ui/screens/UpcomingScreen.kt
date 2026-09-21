package com.example.timeline.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.timeline.data.local.TaskEntity
import com.example.timeline.data.local.TaskType
import com.example.timeline.ui.components.DateHeader
import com.example.timeline.ui.components.ProfileIconButton
import com.example.timeline.ui.components.SearchTopBar
import com.example.timeline.ui.components.TimelineItem
import com.example.timeline.ui.components.getIconForType
import com.example.timeline.ui.theme.*
import com.example.timeline.util.DateUtils
import com.example.timeline.viewmodel.AuthViewModel
import com.example.timeline.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(
    viewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onProfileClick: () -> Unit
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf<TaskType?>(null) }
    
    val upcomingTasks = tasks
        .filter { !it.isCompleted && it.date >= DateUtils.getStartOfDay(System.currentTimeMillis()) }
        .filter { if (selectedTypeFilter != null) it.type == selectedTypeFilter else true }
        .sortedBy { it.date }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onCloseClick = { 
                        isSearchActive = false
                        viewModel.onSearchQueryChange("")
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { Text("Upcoming", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search")
                        }
                        ProfileIconButton(user = currentUser, onClick = onProfileClick)
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!isSearchActive) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTypeFilter == null,
                            onClick = { selectedTypeFilter = null },
                            label = { Text("All") }
                        )
                    }
                    items(TaskType.entries) { type ->
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = { selectedTypeFilter = type },
                            label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getIconForType(type),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (!isSearchActive) {
                    item {
                        UpcomingAgendaCard(upcomingTasks)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (upcomingTasks.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("No upcoming tasks found", color = Color.Gray)
                        }
                    }
                } else {
                    val groupedTasks = upcomingTasks.groupBy { DateUtils.getStartOfDay(it.date) }
                    val sortedDates = groupedTasks.keys.sorted()

                    sortedDates.forEach { date ->
                        item {
                            DateHeader(dateMillis = date)
                        }
                        
                        items(groupedTasks[date] ?: emptyList()) { task ->
                            TimelineItem(
                                task = task,
                                isLast = false,
                                onClick = { onTaskClick(task.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingAgendaCard(tasks: List<TaskEntity>) {
    val highPriorityCount = tasks.count { it.priority == com.example.timeline.data.local.Priority.HIGH }
    val examsCount = tasks.count { it.type == TaskType.EXAM }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            "Upcoming Agenda",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Track your future milestones.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItem(count = highPriorityCount, label = "Priority", color = AccentRed)
            StatItem(count = examsCount, label = "Exams", color = AccentPurple)
        }
    }
}

@Composable
fun StatItem(count: Int, label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
