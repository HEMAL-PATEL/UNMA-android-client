package com.paperplanes.unma.announcementmedialist;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.announcementlist.AnnouncementListViewModel;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class MediaListViewModelTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    AnnouncementRepository repo;
    AnnouncementListViewModel viewModel;

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        List<Announcement> a = new ArrayList<>();
        a.add(new Announcement("", "", null, "", new Date(), new Attachment(), false));
        a.add(new Announcement("", "", null, "", new Date(), null, false));
        a.add(new Announcement("", "", null, "", new Date(), null, false));

        repo = mock(AnnouncementRepository.class);
        when(repo.getAnnouncements()).thenReturn(Flowable.just(a));
        when(repo.fetchAnnouncements()).thenReturn(Completable.complete());

        viewModel = new MediaListViewModel(repo, mock(SessionManager.class), null);
    }

    @Test
    public void loadMediaList_test() {
        viewModel.startListenToData();
        assertNotNull(viewModel.getAnnouncements().getValue());
        assertEquals(1, viewModel.getAnnouncements().getValue().size());
    }

}
