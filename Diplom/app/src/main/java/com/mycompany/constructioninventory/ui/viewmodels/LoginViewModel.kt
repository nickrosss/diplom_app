package com.mycompany.constructioninventory.ui.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.constructioninventory.data.LoginRepository
import com.mycompany.constructioninventory.models.User
import com.mycompany.constructioninventory.network.LoginUser
import com.mycompany.constructioninventory.network.LoginUserData
import com.mycompany.constructioninventory.network.RegisterUser
import com.mycompany.constructioninventory.network.RegisterUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val user = MutableStateFlow<User?>(null)
    val loginError = MutableStateFlow<String?>(null)

    fun register(
        username: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val result = loginRepository.register(
                    RegisterUser(
                    RegisterUserData(username, email, password)
                    )
                )
                Log.d("LoginVM", "RESULT: $result")
                user.value = result

                // Сохраняем ID пользователя в SharedPreferences после успешного входа
                saveUserId(result.id)
                saveToken(result.token)

                Log.d("LoginVM", "TOKEN: ${result.token}")
                Log.d("LoginVM", "ID: ${result.id}")
            } catch (e: Exception) {
                loginError.value = "Registration failed: ${e.message}"
            } finally {
                if (loginError.value != null) {
                    Log.e("LoginViewModel", "Registration error: ${loginError.value}")
                }
            }
        }
    }

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val result = loginRepository.login(LoginUser(LoginUserData(email, password)))
                Log.d("LoginVM", "RESULT: $result")
                user.value = result

                // Сохраняем ID пользователя в SharedPreferences после успешного входа
                saveUserId(result.id)
                saveToken(result.token)
                Log.d("LoginVM", "TOKEN: ${result.token}")
                Log.d("LoginVM", "ID: ${result.id}")
            } catch (e: Exception) {
                loginError.value = "Login failed: ${e.message}"
            } finally {
                if (loginError.value != null) {
                    Log.e("LoginViewModel", "Login error: ${loginError.value}")
                }
            }
        }
    }

    // Функция для сохранения ID пользователя в SharedPreferences
    private fun saveUserId(userId: String) {
        with (sharedPreferences.edit()) {
            putString("user_id", userId)
            apply()
        }
    }

    private fun saveToken(token: String) {
        with (sharedPreferences.edit()) {
            putString("user_token", token)
            apply()
        }
    }
}