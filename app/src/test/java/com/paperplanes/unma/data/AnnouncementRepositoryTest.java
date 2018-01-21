package com.paperplanes.unma.data;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.paperplanes.unma.data.database.DatabaseAccess;
import com.paperplanes.unma.data.network.api.AnnouncementApi;
import com.paperplanes.unma.data.pendingtask.MarkReadTask;
import com.paperplanes.unma.data.pendingtask.MarkReadTaskDatabase;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 21/01/18.
 */

public class AnnouncementRepositoryTest {

    @Mock
    MemoryReactiveStore<String, Announcement> memoryStore;
    @Mock
    AnnouncementApi annApi;
    @Mock
    DatabaseAccess dbAccess;
    @Mock
    MarkReadTaskDatabase dbMarkRead;
    @Mock
    AnnouncementRepositoryPrefs repoPrefs;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Test
    public void markAsRead_successTest() {
        AnnouncementRepository repo = create_repo();

        reset(annApi);
        when(annApi.markAsRead(anyString())).thenReturn(Completable.complete());

        repo.markAsRead(create_announcement(null, null, false))
                .blockingAwait();

        verify(dbAccess).updateAnnouncementAsRead(anyString());
        verify(memoryStore).notifySingularObserver(anyString());
        verify(memoryStore).notifyObjectListObserver();
    }

    @Test
    public void markAsRead_failedTest() {
        AnnouncementRepository repo = create_repo();

        reset(annApi);
        when(annApi.markAsRead(anyString())).thenReturn(Completable.error(new Throwable()));

        repo.markAsRead(create_announcement(null, null, false))
                .blockingGet();

        verify(dbMarkRead).insert(any());
    }

    @Test
    public void getDescContent_localTest() {
        AnnouncementRepository repo = create_repo();
        Announcement ann = create_announcement(new Description("url", "", 123, true), null, false);

        reset(dbAccess);
        when(dbAccess.getDescriptionContent(anyString())).thenReturn("description_content");

        repo.getDescriptionContent(ann).blockingGet();

        verify(dbAccess).getDescriptionContent(anyString());
        assertEquals("description_content", ann.getDescription().getContent());
    }

    @Test
    public void getDescContent_localNullTest() {
        AnnouncementRepository repo = create_repo();
        Announcement ann = create_announcement(new Description("url", "", 123, true), null, false);

        reset(dbAccess);
        when(dbAccess.getDescriptionContent(anyString())).thenReturn(null);
        reset(annApi);
        when(annApi.downloadDescription(anyString())).thenReturn(Single.just(ResponseBody.create(MediaType.parse("text/plain"), "description_content")));

        repo.getDescriptionContent(ann).blockingGet();

        verify(dbAccess).getDescriptionContent(anyString());
        verify(dbAccess).updateDescriptionContent(anyString(), anyString());
        assertTrue(ann.getDescription().isOffline());
        assertEquals("description_content", ann.getDescription().getContent());
    }

    @Test
    public void processAllPendingTask_test() {
        AnnouncementRepository repo = create_repo();
        List<MarkReadTask> tasks = new ArrayList<>();
        tasks.add(new MarkReadTask("id1"));
        tasks.add(new MarkReadTask("id2"));

        reset(dbMarkRead);
        when(dbMarkRead.getAll()).thenReturn(tasks);
        reset(annApi);
        when(annApi.markAsRead(anyString())).thenReturn(Completable.complete());

        repo.processAllPendingTask().blockingAwait();

        verify(annApi, times(2)).markAsRead(anyString());
        verify(dbMarkRead).clearAll();
    }

    private AnnouncementRepository create_repo() {
        return new AnnouncementRepository(
                memoryStore, annApi, dbAccess, dbMarkRead, repoPrefs
        );
    }

    private Announcement create_announcement(Description desc, Attachment attach, boolean read) {
        return new Announcement(
                "id",
                "title",
                desc,
                "publisher",
                new Date(),
                attach,
                read
        );
    }
}
