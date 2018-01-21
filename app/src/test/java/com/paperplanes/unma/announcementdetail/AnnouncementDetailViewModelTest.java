package com.paperplanes.unma.announcementdetail;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Description;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 20/01/18.
 */

public class AnnouncementDetailViewModelTest {

    Announcement a1;
    Announcement a2;
    Announcement a3;
    Announcement a4;
    AnnouncementRepository repo;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    AnnouncementDetailViewModel viewModel;

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        a1 = new Announcement("a1", "hello", null, "me", new Date(), null, false);
        a2 = new Announcement("a2", "hi", new Description("", "", 0, true), "me", new Date(), null, true);
        a3 = new Announcement("a3", "hi!", new Description("", "", 0, false), "me", new Date(), null, false);
        a4 = null;

        repo = mock(AnnouncementRepository.class);
        when(repo.get("a1")).thenReturn(Flowable.just(Optional.of(a1)));
        when(repo.get("a2")).thenReturn(Flowable.just(Optional.of(a2)));
        when(repo.get("a3")).thenReturn(Flowable.just(Optional.of(a3)));
        when(repo.get("a4")).thenReturn(Flowable.just(Optional.of(a4)));
        when(repo.fetch(anyString())).thenReturn(Completable.complete());
        when(repo.getDescriptionContent(a3)).thenReturn(Single.just(a3));
        when(repo.markAsRead(a1)).thenReturn(Completable.complete());
        when(repo.markAsRead(a3)).thenReturn(Completable.complete());
    }

    private void resetViewModel() {
        viewModel = new AnnouncementDetailViewModel(repo, null);
    }

    @Test
    public void loadCurrentAnnouncement_test1() {
        // testing
        resetViewModel();
        // for a1
        viewModel.setCurrentAnnouncementId("a1");
        verify(repo).get("a1");
        assertNotNull(viewModel.getAnnouncement().getValue());
        verify(repo).markAsRead(a1);
    }

    @Test
    public void loadCurrentAnnouncement_test2() {
        resetViewModel();
        // for a2
        viewModel.setCurrentAnnouncementId("a2");
        verify(repo).get("a2");
        assertNotNull(viewModel.getOnDescriptionLoaded().getValue());
        assertNotNull(viewModel.getAnnouncement().getValue());
        verify(repo, never()).markAsRead(a2);
    }

    @Test
    public void loadCurrentAnnouncement_test3() {
        resetViewModel();
        // for a3
        viewModel.setCurrentAnnouncementId("a3");
        verify(repo).get("a3");
        verify(repo).getDescriptionContent(a3);
        assertNotNull(viewModel.getOnDescriptionLoaded().getValue());
        assertNotNull(viewModel.getAnnouncement().getValue());
        verify(repo).markAsRead(a3);
    }

    @Test
    public void loadCurrentAnnouncement_test4() {
        resetViewModel();
        // for a4
        viewModel.setCurrentAnnouncementId("a4");
        verify(repo).fetch("a4");
    }

}
