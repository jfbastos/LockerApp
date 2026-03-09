package com.example.lockerApp.repository

import com.example.lockerApp.data.Configs
import com.example.lockerApp.data.model.User
import com.example.lockerApp.data.repository.AuthenticationRepository
import com.example.lockerApp.data.repository.Either
import com.example.lockerApp.data.rest.ApiInterface
import com.example.lockerApp.data.rest.AuthInterceptor
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class AuthenticationRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockService: ApiInterface
    private lateinit var mockConfigs: Configs

    private lateinit var repository: AuthenticationRepository

    private fun validUser(
        firstName: String = "John",
        lastName: String  = "Doe",
        email: String     = "john@example.com",
        password: String  = "Pass1@"
    ) = User(firstName, lastName, email, password)

    private fun errorBody(json: String = "") =
        ResponseBody.Companion.create("application/json".toMediaTypeOrNull(), json)

    @Before
    fun setup() {
        mockService = mockk()
        mockConfigs = mockk(relaxed = true)
        repository = AuthenticationRepository(mockService, mockConfigs, testDispatcher)
    }

    @Test
    fun `isUserAlreadyLogged returns false when token is null`() {
        every { mockConfigs.token } returns null
        Assert.assertFalse(repository.isUserAlreadyLogged())
    }

    @Test
    fun `isUserAlreadyLogged returns false when token is blank`() {
        every { mockConfigs.token } returns "   "
        Assert.assertFalse(repository.isUserAlreadyLogged())
    }

    @Test
    fun `isUserAlreadyLogged returns true and sets AuthInterceptor when token is valid`() {
        every { mockConfigs.token } returns "valid-token-123"
        Assert.assertTrue(repository.isUserAlreadyLogged())
        Assert.assertEquals("valid-token-123", AuthInterceptor.sessionToken)
    }

    @Test
    fun `isEmailValid returns true for valid email`() {
        Assert.assertTrue(repository.isEmailValid("user@example.com"))
    }

    @Test
    fun `isEmailValid returns false for missing at-sign`() {
        Assert.assertFalse(repository.isEmailValid("userexample.com"))
    }

    @Test
    fun `isEmailValid returns false for missing domain`() {
        Assert.assertFalse(repository.isEmailValid("user@"))
    }

    @Test
    fun `isEmailValid returns false for blank string`() {
        Assert.assertFalse(repository.isEmailValid(""))
    }

    @Test
    fun `isEmailValid returns false for email with spaces`() {
        Assert.assertFalse(repository.isEmailValid("user @example.com"))
    }

    @Test
    fun `isValidPassword returns empty string for valid password`() {
        Assert.assertEquals("", repository.isValidPassword("Pass1@"))
    }

    @Test
    fun `isValidPassword returns error when password is too short`() {
        Assert.assertEquals("Must have 4-12 characters", repository.isValidPassword("P1@"))
    }

    @Test
    fun `isValidPassword returns error when password is too long`() {
        Assert.assertEquals(
            "Must have 4-12 characters",
            repository.isValidPassword("Password123@@@")
        )
    }

    @Test
    fun `isValidPassword returns error when no uppercase letter`() {
        Assert.assertEquals(
            "Must contains at least on Uppercase",
            repository.isValidPassword("pass1@")
        )
    }

    @Test
    fun `isValidPassword returns error when no digit`() {
        Assert.assertEquals("Must contains at least on digit", repository.isValidPassword("Pass@@"))
    }

    @Test
    fun `isValidPassword returns error when no symbol`() {
        Assert.assertEquals(
            "Must contains at least on symbol",
            repository.isValidPassword("Pass12")
        )
    }

    @Test
    fun `validateSignup returns Left when firstName is blank`() = runTest(testDispatcher) {
        val result = repository.validateSignup(validUser(firstName = ""))
        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("Must not be blank!", (result as Either.Left).value)
    }

    @Test
    fun `validateSignup returns Left when lastName is blank`() = runTest(testDispatcher) {
        val result = repository.validateSignup(validUser(lastName = ""))
        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("Must not be blank!", (result as Either.Left).value)
    }

    @Test
    fun `validateSignup returns Left when email is invalid`() = runTest(testDispatcher) {
        val result = repository.validateSignup(validUser(email = "not-an-email"))
        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("Invalid e-mail!", (result as Either.Left).value)
    }

    @Test
    fun `validateSignup returns Left when password is invalid`() = runTest(testDispatcher) {
        val result = repository.validateSignup(validUser(password = "weak"))
        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.startsWith("Password:"))
    }

    @Test
    fun `validateSignin returns Left when email is invalid`() = runTest(testDispatcher) {
        val result = repository.validateSignin("bad-email", "Pass1@")
        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("Invalid e-mail!", (result as Either.Left).value)
    }

    @Test
    fun `validateSignin returns Left when password is invalid`() = runTest(testDispatcher) {
        val result = repository.validateSignin("user@example.com", "weak")
        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.startsWith("Password:"))
    }

    @Test
    fun `submitNewUser returns Right with email on 2xx response`() = runTest(testDispatcher) {
        val user = validUser()
        coEvery { mockService.submitNewUser(any()) } returns Response.success(null)

        val result = repository.submitNewUser(user)

        Assert.assertTrue(result is Either.Right)
        Assert.assertEquals(user.email, (result as Either.Right).value)
        Assert.assertEquals(user, repository.loggedUser)
    }

    @Test
    fun `submitNewUser returns Left with conflict message on 409`() = runTest(testDispatcher) {
        val user = validUser()
        coEvery { mockService.submitNewUser(any()) } returns Response.error(409, errorBody())

        val result = repository.submitNewUser(user)

        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("E-mail já cadastrado", (result as Either.Left).value)
    }

    @Test
    fun `submitNewUser returns Left on unknown error code`() = runTest(testDispatcher) {
        val user = validUser()
        coEvery { mockService.submitNewUser(any()) } returns Response.error(500, errorBody())

        val result = repository.submitNewUser(user)

        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.contains("500"))
    }

    @Test
    fun `submitNewUser returns Left when service returns null`() = runTest(testDispatcher) {
        val user = validUser()
        coEvery { mockService.submitNewUser(any()) } returns null

        val result = repository.submitNewUser(user)

        Assert.assertTrue(result is Either.Left)
        Assert.assertEquals("NewUser service null", (result as Either.Left).value)
    }

    @Test
    fun `submitNewUser returns Left when exception is thrown`() = runTest(testDispatcher) {
        val user = validUser()
        coEvery { mockService.submitNewUser(any()) } throws RuntimeException("network timeout")

        val result = repository.submitNewUser(user)

        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.contains("network timeout"))
    }

    @Test
    fun `submitSignin returns Left on 401 with error body`() = runTest(testDispatcher) {
        val errorJson = """{"description":"Invalid credentials","fieldErrors":[]}"""
        coEvery { mockService.submitSignin(any()) } returns Response.error(
            401,
            errorBody(errorJson)
        )

        val result = repository.submitSignin("user@example.com", "Pass1@")

        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.contains("Failed to signin"))
    }

    @Test
    fun `submitSignin returns Left with unknown error on 401 with blank body`() =
        runTest(testDispatcher) {
            coEvery { mockService.submitSignin(any()) } returns Response.error(401, errorBody())

            val result = repository.submitSignin("user@example.com", "Pass1@")

            Assert.assertTrue(result is Either.Left)
            Assert.assertTrue((result as Either.Left).value.contains("Unknown error"))
        }

    @Test
    fun `submitSignin returns Left on unknown error code`() = runTest(testDispatcher) {
        coEvery { mockService.submitSignin(any()) } returns Response.error(503, errorBody())

        val result = repository.submitSignin("user@example.com", "Pass1@")

        Assert.assertTrue(result is Either.Left)
        Assert.assertTrue((result as Either.Left).value.contains("503"))
    }

    @Test
    fun `createNewUserBody builds JsonObject with all user fields`() {
        val user = validUser()
        val body: JsonObject = repository.createNewUserBody(user)

        Assert.assertEquals(user.firstName, body.get("firstName").asString)
        Assert.assertEquals(user.lastName, body.get("lastName").asString)
        Assert.assertEquals(user.email, body.get("email").asString)
        Assert.assertEquals(user.password, body.get("password").asString)
    }

    @Test
    fun `createUserInfoBody builds JsonObject with email and password`() {
        val body: JsonObject = repository.createUserInfoBody("user@example.com", "Pass1@")

        Assert.assertEquals("user@example.com", body.get("email").asString)
        Assert.assertEquals("Pass1@", body.get("password").asString)
    }
}