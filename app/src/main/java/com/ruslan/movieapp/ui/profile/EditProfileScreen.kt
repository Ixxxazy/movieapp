package com.ruslan.movieapp.ui.profile

import android.Manifest
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ruslan.movieapp.domain.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle(initialValue = UserProfile())

    var fullName by remember { mutableStateOf(profile.fullName) }
    var position by remember { mutableStateOf(profile.position) }
    var avatarUri by remember { mutableStateOf(profile.avatarUri) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var reminderTime by remember { mutableStateOf(profile.reminderTime) }
    var timeError by remember { mutableStateOf<String?>(null) }

    fun isValidTime(time: String): Boolean {
        val timePattern = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        return timePattern.matches(time)
    }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        position = profile.position
        avatarUri = profile.avatarUri
        resumeUrl = profile.resumeUrl
        reminderTime = profile.reminderTime
        timeError = if (reminderTime.isNotBlank() && !isValidTime(reminderTime)) "Неверный формат времени" else null
        Log.d("EditProfile", "Loaded profile: reminderTime=$reminderTime")
    }

    val context = LocalContext.current

    fun showTimePicker() {
        val parts = reminderTime.split(":").let {
            if (it.size == 2 && it[0].toIntOrNull() != null && it[1].toIntOrNull() != null) {
                it[0].toInt() to it[1].toInt()
            } else {
                Calendar.getInstance().let { cal ->
                    cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
                }
            }
        }
        val picker = TimePickerDialog(
            context,
            { _, hour, minute ->
                reminderTime = String.format("%02d:%02d", hour, minute)
                timeError = if (isValidTime(reminderTime)) null else "Неверный формат времени"
                Log.d("EditProfile", "Time selected: $reminderTime")
            },
            parts.first,
            parts.second,
            true
        )
        picker.show()
    }

    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val photoFile = remember { File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg") }
    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(it)
            savedPath?.let { path -> avatarUri = path }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            val savedPath = saveImageToInternalStorage(photoUri)
            savedPath?.let { path -> avatarUri = path }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        if (cameraGranted) {
            cameraLauncher.launch(photoUri)
        }
    }

    fun selectFromGallery() {
        galleryLauncher.launch("image/*")
    }

    fun selectFromCamera() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    val isSaveEnabled = reminderTime.isBlank() || isValidTime(reminderTime)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSaveEnabled) {
                                Log.d("EditProfile", "Saving profile: reminderTime=$reminderTime")
                                viewModel.saveProfile(
                                    UserProfile(
                                        fullName = fullName,
                                        position = position,
                                        avatarUri = avatarUri,
                                        resumeUrl = resumeUrl,
                                        reminderTime = reminderTime
                                    )
                                )
                                onSave()
                            }
                        },
                        enabled = isSaveEnabled
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Аватар
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(120.dp)) {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(avatarUri?.let { File(it).takeIf { f -> f.exists() }?.absolutePath })
                                .crossfade(true)
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "Аватар",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { selectFromGallery() }) { Text("Галерея") }
                        Button(onClick = { selectFromCamera() }) { Text("Камера") }
                    }
                }
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Введите ФИО") }
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: Android Developer") }
            )

            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Ссылка на резюме") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://example.com/resume.pdf") }
            )

            // Поле для времени с кнопкой выбора
            OutlinedTextField(
                value = reminderTime,
                onValueChange = { newTime ->
                    reminderTime = newTime
                    timeError = if (newTime.isNotBlank() && !isValidTime(newTime)) "Неверный формат времени (HH:MM)" else null
                },
                label = { Text("Время любимой пары") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("HH:MM") },
                isError = timeError != null,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker() }) {
                        Icon(Icons.Default.Schedule, contentDescription = "Выбрать время")
                    }
                },
                supportingText = {
                    if (timeError != null) {
                        Text(
                            text = timeError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Формат: 14:30")
                    }
                }
            )

            Text(
                text = "Подсказка: в указанное время придет уведомление-напоминание о начале пары.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}