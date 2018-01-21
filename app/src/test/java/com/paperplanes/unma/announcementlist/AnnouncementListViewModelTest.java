package com.paperplanes.unma.announcementlist;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.model.Announcement;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class AnnouncementListViewModelTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    AnnouncementRepository repo;
    SessionManager sessMan;

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        List<Announcement> a = new ArrayList<>();
        a.add(new Announcement(
                "",
                "",
                null,
                "",
                new Date(),
                null,
                false));
        a.add(new Announcement(
                "",
                "",
                null,
                "",
                new Date(),
                null,
                false));
        a.add(new Announcement(
                "",
                "",
                null,
                "",
                new Date(),
                null,
                false));

        sessMan = mock(SessionManager.class);
        repo = mock(AnnouncementRepository.class);
        when(repo.getAnnouncements()).thenReturn(Flowable.just(a));
        when(repo.fetchAnnouncements()).thenReturn(Completable.complete());
    }

    @Test
    public void loadDataList_test() {
        AnnouncementListViewModel viewModel = new AnnouncementListViewModel(repo, sessMan);

        viewModel.startListenToData();

        assertNotNull(viewModel.getAnnouncements().getValue());
        verify(repo).getAnnouncements();
        verify(repo).fetchAnnouncements();
    }

    @Test
    public void refreshForFirstTime_test() {
        AnnouncementListViewModel viewModel = new AnnouncementListViewModel(repo, sessMan);

        viewModel.startListenToData();
        viewModel.startListenToData();

        verify(repo, timeout(1)).fetchAnnouncements();
    }

    @Test
    public void refreshDataList_test() {
        AnnouncementListViewModel viewModel = new AnnouncementListViewModel(repo, sessMan);

        viewModel.refresh();

        verify(repo).fetchAnnouncements();
    }
}
