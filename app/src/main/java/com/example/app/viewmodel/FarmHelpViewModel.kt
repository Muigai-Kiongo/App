package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class FarmHelpUiState(
    val currentStep: Int = 1,
    val description: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showConfirmation: Boolean = false,
    val submissionSuccess: Boolean = false
)

class FarmHelpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FarmHelpUiState())
    val uiState: StateFlow<FarmHelpUiState> = _uiState

    fun nextStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
    }

    fun prevStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
    }

    fun setDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun setSelectedImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun showConfirmationDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showConfirmation = show)
    }

    fun reset() {
        _uiState.value = FarmHelpUiState()
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = File.createTempFile("camera_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // This must match <provider> in the manifest
            imageFile
        )
    }
    fun setErrorMessage(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun submitPost(context: Context) {
        val state = _uiState.value
        if (state.selectedImageUri == null || state.description.isBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Image and description required.",
                isLoading = false,
                showConfirmation = false
            )
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null, showConfirmation = false)

        viewModelScope.launch {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(state.selectedImageUri)
            val file = File(context.cacheDir, "upload.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val requestFile = MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val descBody = state.description.toRequestBody("text/plain".toMediaTypeOrNull())
            PostRepository().createPost(
                image = requestFile,
                description = descBody,
                onResult = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submissionSuccess = response?.status == "success",
                        errorMessage = if (response?.status != "success") "Server error" else null,
                        currentStep = if (response?.status == "success") 3 else _uiState.value.currentStep
                    )
                },
                onError = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            )
        }
    }
}