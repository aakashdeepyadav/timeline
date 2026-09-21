package com.example.timeline.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.timeline.data.local.TaskEntity

@Composable
fun TimelineItem(
    task: TaskEntity,
    isLast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Slimmer Timeline Column
        Box(
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            val lineColor = MaterialTheme.colorScheme.outlineVariant
            
            Canvas(modifier = Modifier.matchParentSize()) {
                val centerX = size.width / 2
                
                if (!isLast) {
                    drawLine(
                        color = lineColor,
                        start = Offset(centerX, 32.dp.toPx()),
                        end = Offset(centerX, size.height),
                        strokeWidth = 1.dp.toPx() // Thinner line
                    )
                }
            }

            Surface(
                modifier = Modifier.padding(top = 12.dp).size(20.dp),
                shape = CircleShape,
                color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getIconForType(task.type),
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        TaskCard(
            task = task,
            onClick = onClick,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .weight(1f)
        )
    }
}
