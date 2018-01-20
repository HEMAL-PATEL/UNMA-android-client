package com.paperplanes.unma.profiledetail;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.data.ProfileRepository;
import com.paperplanes.unma.model.Profile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class ProfileDetailViewModelTest {

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
        repo = mock(ProfileRepository.class);
        sessMan = mock(SessionManager.class);

        when(repo.fetch()).thenReturn(Completable.complete());
        when(repo.get()).thenReturn(Flowable.just(Optional.of(new Profile("", "", null))));
    }

    @Test
    public void loadProfile_firstRunTest() {
        ProfileDetailViewModel viewModel = new ProfileDetailViewModel(repo, sessMan);

        viewModel.loadProfile();
        assertNotNull(viewModel.getProfile().getValue());
        verify(repo).fetch();
    }

    @Test
    public void loadProfile_secondTimeTest() {
        ProfileDetailViewModel viewModel = new ProfileDetailViewModel(repo, sessMan);

        viewModel.loadProfile();
        viewModel.loadProfile();
        reset(repo);

        assertNotNull(viewModel.getProfile().getValue());
        verify(repo, never()).fetch();
    }
}
