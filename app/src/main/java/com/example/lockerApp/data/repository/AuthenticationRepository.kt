package com.example.lockerApp.data.repository

import android.util.Patterns.EMAIL_ADDRESS
import com.example.lockerApp.data.Configs
import com.example.lockerApp.data.model.User
import com.example.lockerApp.data.model.ValidationError
import com.example.lockerApp.data.rest.ApiInterface
import com.example.lockerApp.data.rest.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

sealed class Either<out A, out B> {
    class Left<A>(val value: A): Either<A, Nothing>()
    class Right<B>(val value: B): Either<Nothing, B>()
}

class AuthenticationRepository(private val service : ApiInterface, private val configs : Configs, private val dispatcher: CoroutineDispatcher) {

    var loggedUser : User? = null

    fun isUserAlreadyLogged() : Boolean{
        val token = configs.token ?: return false
        return if(token.isNotBlank()){
            AuthInterceptor.sessionToken = token
            true
        }else{
            false
        }
    }

    suspend fun validateSignup(user : User) : Either<String, String>{
        if(user.firstName.isBlank()) return Either.Left("Must not be blank!")
        if(user.lastName.isBlank()) return Either.Left("Must not be blank!")
        if(!isEmailValid(user.email)) return Either.Left("Invalid e-mail!")

        val msgPassword = isValidPassword(user.password)
        if(msgPassword.isNotBlank()) return Either.Left("Password: $msgPassword")

        return submitNewUser(user)
    }

    suspend fun validateSignin(email : String, password: String) : Either<String, Unit>{
        if(!isEmailValid(email)) return Either.Left("Invalid e-mail!")
        val msgPassword = isValidPassword(password)
        if(msgPassword.isNotBlank()) return Either.Left("Password: $msgPassword")

        return submitSignin(email, password)
    }

    fun isEmailValid(email : String) : Boolean{
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): String {
        if (password.length !in 4..12) return "Must have 4-12 characters"
        if (password.none { it.isUpperCase() }) return "Must contains at least on Uppercase"
        if (password.none { it.isDigit() }) return "Must contains at least on digit"
        if (password.none { !it.isLetterOrDigit() }) return "Must contains at least on symbol"
        return ""
    }

    suspend fun submitNewUser(user : User) = withContext(dispatcher){
        try{
            val response = service.submitNewUser(createNewUserBody(user))?.awaitResponse() ?: return@withContext Either.Left("NewUser service null")

            if(response.isSuccessful){
                loggedUser = user
                return@withContext Either.Right(user.email)
            }

            return@withContext when(response.code()){
                409 -> Either.Left("E-mail já cadastrado")
                422 -> {
                    if(response.errorBody() != null){
                        val error = Gson().fromJson(response.errorBody()?.string(), ValidationError::class.java)
                        Either.Left("${error.fieldErrors.first().field}: ${error.fieldErrors.first().message}")
                    }else{
                        Either.Left("Unknown error.")
                    }

                }else -> Either.Left("Unknown error (${response.code()}")
            }
        }catch (e : Exception){
            return@withContext Either.Left("Error: ${e.message}")
        }
    }

    fun createNewUserBody(user : User) : JsonObject{
        return JsonObject().apply {
            addProperty("firstName", user.firstName)
            addProperty("lastName", user.lastName)
            addProperty("email", user.email)
            addProperty("password", user.password)
        }
    }

    suspend fun submitSignin(email : String, password: String) : Either<String, Unit> = withContext(dispatcher){
        val response = service.submitSignin(createUserInfoBody(email, password))

        if(response.isSuccessful) {
            configs.token = response.body()?.token
            AuthInterceptor.sessionToken = configs.token
            return@withContext Either.Right(Unit)
        }

        return@withContext when(response.code()){
            401 -> {
                if(response.errorBody() != null){
                    val error = Gson().fromJson(response.errorBody()?.string(), ValidationError::class.java)
                    Either.Left("Failed to signin: ${error.description}")
                }else{
                    Either.Left("Unknown error: Error body null")
                }
            }
            else -> Either.Left("Unknown error: Code ${response.code()}")
        }
    }

    fun createUserInfoBody(email : String, password: String) : JsonObject{
        return JsonObject().apply {
            addProperty("email", email)
            addProperty("password", password)
        }
    }


}