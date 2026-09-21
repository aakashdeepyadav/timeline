package com.example.timeline.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.timeline.util.DataMode
import com.example.timeline.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onSignInClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val dataMode by authViewModel.dataMode.collectAsState()
    val lastSynced by authViewModel.lastSynced.collectAsState()
    val currentUserState by authViewModel.currentUser.collectAsState()
    val currentUser = currentUserState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Account & Sync",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (dataMode == DataMode.GOOGLE && currentUser != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { onProfileClick() }
                        ) {
                            if (currentUser.photoUrl != null) {
                                AsyncImage(
                                    model = currentUser.photoUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Rounded.CloudDone, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(currentUser.displayName ?: "Google Account", fontWeight = FontWeight.Bold)
                                Text(currentUser.email ?: "Signed in", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val syncText = if (lastSynced == 0L) "Never synced" else "Last synced: ${com.example.timeline.util.DateUtils.formatDate(lastSynced)}"
                        Text(syncText, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { authViewModel.syncWithDrive() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Sync Now")
                        }
                        
                        TextButton(
                            onClick = { authViewModel.signOut() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign Out", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CloudOff, null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Offline Mode", fontWeight = FontWeight.Bold)
                                Text("Your data is stored locally.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onSignInClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Connect Google Account")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "App Preferences",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column {
                    SettingsItem(icon = Icons.Rounded.Notifications, title = "Notifications")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Palette, title = "Appearance")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.AutoMirrored.Rounded.HelpOutline, title = "Help & Feedback")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Version 1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
    }
}
