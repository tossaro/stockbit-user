package com.stockbit.mini.userlib.modules.signin

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.stockbit.mini.userlib.repositories.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SignInViewModelTest {
    @Mock
    private lateinit var view: View
    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: SignInViewModel
    private lateinit var loadingIndicatorLiveData: LiveData<Boolean>

    private val email = "hamzah.tossaro@gmail.com"
    private val passwordValid = "123789"

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        MockitoAnnotations.initMocks(this)

        viewModel = SignInViewModel(userRepository)
        loadingIndicatorLiveData = viewModel.loadingIndicator
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @After
    fun teardown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `empty email should give error`() = runBlocking {
        viewModel.userIdString.value = ""
        viewModel.signIn(view)

        assertNotEquals(viewModel.errorUserIdString, "")
        return@runBlocking
    }

    @Test
    fun `empty password should give error`() = runBlocking {
        viewModel.passwordString.value = ""
        viewModel.signIn(view)

        assertNotEquals(viewModel.errorPasswordString, "")
        return@runBlocking
    }

    @Test
    fun `sign in should show and hide loading`() = runBlocking {
        var showLoading = loadingIndicatorLiveData.value
        showLoading?.let { assertFalse(it) }

        viewModel.userIdString.value = email
        viewModel.passwordString.value = passwordValid
        viewModel.signIn(view)

        showLoading = loadingIndicatorLiveData.value
        showLoading?.let { assertTrue(it) }
        return@runBlocking
    }

}