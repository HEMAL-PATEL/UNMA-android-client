package com.paperplanes.unma.login;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Authentication;
import com.paperplanes.unma.model.LoginResult;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by abdularis on 20/01/18.
 */

public class LoginViewModelTest {

    ResourceProvider resourceProvider;
    Authentication authentication;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        resourceProvider = mock(ResourceProvider.class);
        authentication = mock(Authentication.class);
        when(resourceProvider.getString(anyInt())).thenReturn("error message");
        when(authentication.login("14.14.1.0002", "123")).thenReturn(Single.error(new Throwable()));
        when(authentication.login("14.14.1.0002", "1")).thenReturn(Single.just(new LoginResult()));
    }

    @Test
    public void login_correct() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("14.14.1.0002", "1");
        assertNull(viewModel.getGeneralErr().getValue());
        assertNotNull(viewModel.getLoginResult().getValue());
        verify(authentication).login("14.14.1.0002", "1");
    }

    @Test
    public void login_wrongAccount() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("14.14.1.0002", "123");
        assertNotNull(viewModel.getGeneralErr().getValue());
    }

    @Test
    public void login_wrongNpmFormat() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("14.141.0002", "123");
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

    @Test
    public void login_emptyNpm() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("", "123");
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

    @Test
    public void login_emptyPwd() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("14.14.1.0002", "");
        assertNotNull(viewModel.getPasswordErr().getValue());
    }

    @Test
    public void login_emptyAll() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authentication);

        viewModel.login("", "");
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

}
