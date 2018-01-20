package com.paperplanes.unma.profileupdate;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Session;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.ProfileRepository;
import com.paperplanes.unma.model.ProfileUpdateResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class ProfileUpdateViewModelTest {

    ResourceProvider resourceProv;
    ProfileRepository repo;
    SessionManager sessMan;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        resourceProv = mock(ResourceProvider.class);
        repo = mock(ProfileRepository.class);
        sessMan = mock(SessionManager.class);

        ProfileUpdateResult profUpdateResult = new ProfileUpdateResult();
        profUpdateResult.setSuccess(true);
        when(repo.updatePassword("123", "abc")).thenReturn(Single.just(profUpdateResult));
        when(repo.updatePassword("xyz", "123")).thenReturn(Single.error(new Throwable()));
        when(resourceProv.getString(anyInt())).thenReturn("error message");
    }

    @Test
    public void changePassword_correct() {
        ProfileUpdateViewModel viewModel = new ProfileUpdateViewModel(resourceProv, repo, sessMan);

        viewModel.changePassword("123", "abc");

        assertNull(viewModel.getError().getValue());
        verify(sessMan).setSession(any(Session.class));
    }

    @Test
    public void changePassword_empty() {
        ProfileUpdateViewModel viewModel = new ProfileUpdateViewModel(resourceProv, repo, sessMan);

        viewModel.changePassword("", "");
        assertNotNull(viewModel.getError().getValue());
    }

    @Test
    public void changePassword_samePassword() {
        ProfileUpdateViewModel viewModel = new ProfileUpdateViewModel(resourceProv, repo, sessMan);

        viewModel.changePassword("abc", "abc");
        assertNotNull(viewModel.getError().getValue());
    }
}
