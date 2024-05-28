package com.example.foodie.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodie.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(var userRepo: UserRepository) : ViewModel() {
    private val _updateStatus = MutableLiveData<List<String>>()
    val updateStatus: MutableLiveData<List<String>> = _updateStatus

    private val _currentTextError = MutableLiveData<String>()
    val currentTextError: MutableLiveData<String> = _currentTextError

    private val _newTextError = MutableLiveData<String>()
    val newTextError: MutableLiveData<String> = _newTextError

    private val _againNewTextError = MutableLiveData<String>()
    val againNewTextError: MutableLiveData<String> = _againNewTextError

    fun checkUpdate(
        action: String,
        currentText: String,
        newText: String,
        againNewText: String,
        email: String,
        username: String,
        password: String
    ) {
        when (action) {
            "email" -> {
                when (newText) {
                    "" -> {
                        _newTextError.value = "Debes ingresar tu nueva dirección de correo electrónico"
                    }

                    againNewText -> {
                        _updateStatus.value = arrayListOf("email", newText, email, username, password)
                    }

                    else -> {
                        _againNewTextError.value =
                            "Nuevo correo electrónico"
                    }
                }
            }

            "password" -> {
                if (currentText == "") {
                    _currentTextError.value = "Debes ingresar tu contraseña actual"
                } else if (currentText != password) {
                    _currentTextError.value = "La contraseña ingresada es incorrecta"
                } else if (newText == "") {
                    _newTextError.value = "Debes ingresar tu nueva contraseña"
                } else if (newText == againNewText) {
                    _updateStatus.value = arrayListOf("password", newText, email, username, password)
                } else {
                    _againNewTextError.value =
                        "La contraseña ingresada debe ser la misma que la del campo Nueva Contraseña"
                }
            }
        }
    }

    fun updateAccount(updateStatus: List<String>) {
        userRepo.updateAccount(updateStatus)
    }
}