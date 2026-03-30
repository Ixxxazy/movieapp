package com.ruslan.movieapp.ui.profile

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var avatarUri by remember { mutableStateOf(profile.avatarUri) }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        position = profile.position
        resumeUrl = profile.resumeUrl
        avatarUri = profile.avatarUri
    }

    val context = LocalContext.current

    // Функция сохранения изображения в локальное хранилище
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

    // Временный файл для фото с камеры
    val photoFile = remember { File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg") }
    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    // Launcher для галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(it)
            savedPath?.let { path ->
                avatarUri = path
            }
        }
    }

    // Launcher для камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            val savedPath = saveImageToInternalStorage(photoUri)
            savedPath?.let { path ->
                avatarUri = path
            }
        }
    }

    // Launcher для разрешений
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
                    IconButton(onClick = {
                        viewModel.saveProfile(
                            UserProfile(
                                fullName = fullName,
                                position = position,
                                avatarUri = avatarUri,
                                resumeUrl = resumeUrl
                            )
                        )
                        onSave()
                    }) {
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
            // Аватар с выбором
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(120.dp)
                    ) {
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
                        Button(onClick = { selectFromGallery() }) {
                            Text("Галерея")
                        }
                        Button(onClick = { selectFromCamera() }) {
                            Text("Камера")
                        }
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
                label = { Text("Ссылка на резюме (PDF)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://example.com/resume.pdf") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Подсказка: чтобы изменить данные, просто отредактируйте текст в полях выше. Пустое поле означает, что данные будут удалены.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}