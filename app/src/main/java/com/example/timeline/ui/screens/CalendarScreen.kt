package com.example.timeline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timeline.data.local.TaskEntity
import com.example.timeline.ui.components.ProfileIconButton
import com.example.timeline.ui.components.SearchTopBar
import com.example.timeline.ui.components.TimelineItem
import com.example.timeline.ui.components.AddTaskFab
import com.example.timeline.util.DateUtils
import com.example.timeline.viewmodel.AuthViewModel
import com.example.timeline.viewmodel.TaskViewModel
import java.util.Calendar

import androidx.compose.ui.res.painterResource
import com.example.timeline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
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
    var selectedDate by remember { mutableLongStateOf(DateUtils.getStartOfDay(System.currentTimeMillis())) }
    
    val filteredTasks = tasks.filter { 
        if (isSearchActive && searchQuery.isNotBlank()) true
        else DateUtils.getStartOfDay(it.date) == selectedDate 
    }

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
                TopAppBar(
                    title = { 
                        Text(
                            "Calendar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Rounded.Search, null) }
                        ProfileIconButton(user = currentUser, onClick = onProfileClick)
                    }
                )
            }
        },
        floatingActionButton = {
            AddTaskFab(onClick = onAddTaskClick)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!isSearchActive) {
                CalendarGrid(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    tasks = tasks
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                if (isSearchActive) "Search Results" else "Timeline for ${DateUtils.formatDate(selectedDate)}",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Text("No events found", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 32.dp), textAlign = TextAlign.Center)
                    }
                } else {
                    items(filteredTasks) { task ->
                        TimelineItem(task = task, isLast = false, onClick = { onTaskClick(task.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    tasks: List<TaskEntity>
) {
    val calendar = Calendar.getInstance().apply { 
        timeInMillis = selectedDate
        set(Calendar.DAY_OF_MONTH, 1) 
    }
    val firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Adjusted for Mon-Sun
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val monthYearFormatter = remember { java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()) }
            Text(
                monthYearFormatter.format(java.util.Date(selectedDate)),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(onClick = { onDateSelected(DateUtils.getStartOfDay(System.currentTimeMillis())) }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                Text("Today")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp)
        ) {
            items(firstDayOfWeek) { Box(modifier = Modifier.aspectRatio(1f)) }
            
            items(daysInMonth) { day ->
                val dateCal = Calendar.getInstance().apply { 
                    timeInMillis = selectedDate
                    set(Calendar.DAY_OF_MONTH, day + 1) 
                }
                val dateMillis = DateUtils.getStartOfDay(dateCal.timeInMillis)
                val isSelected = dateMillis == selectedDate
                val hasTasks = tasks.any { DateUtils.getStartOfDay(it.date) == dateMillis }
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
                        .clickable { onDateSelected(dateMillis) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            (day + 1).toString(),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (hasTasks && !isSelected) {
                            Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                    }
                }
            }
        }
    }
}
