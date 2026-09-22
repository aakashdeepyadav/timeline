package com.example.timeline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.example.timeline.ui.theme.*
import com.example.timeline.ui.components.DateHeader
import com.example.timeline.ui.components.*
import com.example.timeline.viewmodel.AuthViewModel
import com.example.timeline.util.DateUtils
import com.example.timeline.viewmodel.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
                HomeTopBar(
                    onSearchClick = { isSearchActive = true },
                    onProfileClick = onProfileClick,
                    user = currentUser
                )
            }
        },
        floatingActionButton = {
            AddTaskFab(onClick = onAddTaskClick)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                DashboardHeader()
            }
            
            item {
                CalendarStrip(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            item {
                SummaryBadges(tasks)
            }

            item {
                DateHeader(dateMillis = selectedDate)
            }

            if (filteredTasks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No plans for this day", color = Color.Gray)
                    }
                }
            } else {
                itemsIndexed(filteredTasks) { index, task ->
                    TimelineItem(
                        task = task,
                        isLast = index == filteredTasks.size - 1,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    user: com.google.firebase.auth.FirebaseUser?
) {
    TopAppBar(
        title = {
            Text(
                "TimeLine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Rounded.Search, contentDescription = "Search")
            }
            ProfileIconButton(user = user, onClick = onProfileClick)
        }
    )
}

@Composable
fun DashboardHeader() {
    Column(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
        Text(
            "My Timeline",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CalendarStrip(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = DateUtils.getStartOfDay(System.currentTimeMillis())
    calendar.add(Calendar.DAY_OF_YEAR, -3)
    
    val days = (0..14).map {
        val time = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        time
    }

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val monthYearFormatter = remember { java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()) }
            Text(
                text = monthYearFormatter.format(java.util.Date(selectedDate)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(days.size) { index ->
                val date = days[index]
                val isSelected = date == selectedDate
                val cal = Calendar.getInstance().apply { timeInMillis = date }
                val dayName = when(cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Mon"
                    Calendar.TUESDAY -> "Tue"
                    Calendar.WEDNESDAY -> "Wed"
                    Calendar.THURSDAY -> "Thu"
                    Calendar.FRIDAY -> "Fri"
                    Calendar.SATURDAY -> "Sat"
                    Calendar.SUNDAY -> "Sun"
                    else -> ""
                }
                val dayNumber = cal.get(Calendar.DAY_OF_MONTH)

                Surface(
                    onClick = { onDateSelected(date) },
                    shape = MaterialTheme.shapes.medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(60.dp).height(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(dayName, style = MaterialTheme.typography.labelSmall)
                        Text(dayNumber.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (isSelected) {
                             Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.onPrimary, CircleShape))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryBadges(tasks: List<TaskEntity>) {
    val today = DateUtils.getStartOfDay(System.currentTimeMillis())
    val todayTasks = tasks.count { DateUtils.getStartOfDay(it.date) == today && !it.isCompleted }
    val deadlines = tasks.count { it.type == TaskType.DEADLINE && !it.isCompleted }

    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(BrandIndigo, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text("$todayTasks Today", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(BrandRed, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text("$deadlines Deadline", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
