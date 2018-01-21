package com.paperplanes.unma.data;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 21/01/18.
 */

public class AnnouncementRepositoryTest {


    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }


}
