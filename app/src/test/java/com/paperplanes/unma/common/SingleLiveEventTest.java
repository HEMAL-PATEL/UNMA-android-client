package com.paperplanes.unma.common;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class SingleLiveEventTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    class TestObserver implements Observer {
        @Override
        public void onChanged(@Nullable Object o) {}
    }

    @Test
    public void singleEvent_test() {
        SingleLiveEvent<Object> event = new SingleLiveEvent<>();

        TestObserver o1 = mock(TestObserver.class);
        TestObserver o2 = mock(TestObserver.class);

        event.setValue(new Object());

        event.observeForever(o1);
        event.observeForever(o2);

        verify(o1).onChanged(any());
        verify(o2, never()).onChanged(any());
    }

}
