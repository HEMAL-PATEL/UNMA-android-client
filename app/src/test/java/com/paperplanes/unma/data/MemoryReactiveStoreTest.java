package com.paperplanes.unma.data;

import com.paperplanes.unma.common.Optional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

/**
 * Created by abdularis on 21/01/18.
 */

public class MemoryReactiveStoreTest {

    MemoryReactiveStore<Integer, String> mrs =
            new MemoryReactiveStore<>(String::hashCode);

    @Test
    public void storeSingular_test() throws Exception {
        // test
        mrs.storeSingular("str1");

        TestSubscriber<Optional<List<String>>> testSubscriber = new TestSubscriber<>();
        mrs.getAll().subscribe(testSubscriber);

        testSubscriber.assertNotComplete();
        testSubscriber.assertValue(listOptional -> {
            return listOptional.isNotNull() &&
                    listOptional.get().size() == 1 &&
                    listOptional.get().get(0).equals("str1");
        });
    }

    @Test
    public void storeAll_test() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("str1");
        list.add("str2");
        list.add("str3");

        // test
        mrs.storeAll(list);

        TestSubscriber<Optional<List<String>>> testSubscriber = new TestSubscriber<>();
        mrs.getAll().subscribe(testSubscriber);

        testSubscriber.assertValue(listOptional -> listOptional.isNotNull() && listOptional.get().size() == 3);
    }

    @Test
    public void replaceAll_test() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("str1");

        List<String> list2 = new ArrayList<>();
        list2.add("str1");
        list2.add("str2");
        list2.add("str3");

        // test
        mrs.replaceAll(list);
        mrs.replaceAll(list2);

        TestSubscriber<Optional<List<String>>> testSubscriber = new TestSubscriber<>();
        mrs.getAll().subscribe(testSubscriber);

        testSubscriber.assertValue(listOptional -> listOptional.isNotNull() && listOptional.get().size() == 3);
    }

}
