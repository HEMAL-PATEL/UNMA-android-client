package com.paperplanes.unma.login;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Authentication;
import com.paperplanes.unma.model.LoginResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

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
    Authentication authStudent;
    Authentication authLecturer;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        resourceProvider = mock(ResourceProvider.class);
        authStudent = mock(Authentication.class);
        authLecturer = mock(Authentication.class);

        when(resourceProvider.getString(anyInt())).thenReturn("error message");
        when(authStudent.login("14.14.1.0002", "123")).thenReturn(Single.error(new Throwable()));
        when(authStudent.login("14.14.1.0002", "1")).thenReturn(Single.just(new LoginResult()));
    }

    @Test
    public void login_correct() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("14.14.1.0002", "1", 1);
        assertNull(viewModel.getGeneralErr().getValue());
        assertNotNull(viewModel.getLoginResult().getValue());
        verify(authStudent).login("14.14.1.0002", "1");
    }

    @Test
    public void login_wrongAccount() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("14.14.1.0002", "123", 1);
        assertNotNull(viewModel.getGeneralErr().getValue());
    }

    @Test
    public void login_wrongNpmFormat() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("14.141.0002", "123", 1);
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

    @Test
    public void login_emptyNpm() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("", "123", 1);
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

    @Test
    public void login_emptyPwd() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("14.14.1.0002", "", 1);
        assertNotNull(viewModel.getPasswordErr().getValue());
    }

    @Test
    public void login_emptyAll() {
        LoginViewModel viewModel = new LoginViewModel(resourceProvider, authStudent, authLecturer);

        viewModel.login("", "", 1);
        assertNotNull(viewModel.getUsernameErr().getValue());
    }

}
