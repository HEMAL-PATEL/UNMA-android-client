package com.paperplanes.unma.data;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.data.network.api.ProfileApi;
import com.paperplanes.unma.data.network.api.response.JsonResp;
import com.paperplanes.unma.data.network.api.response.ProfileRespData;
import com.paperplanes.unma.model.Profile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.lang.reflect.Type;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by abdularis on 20/01/18.
 */

public class ProfileRepositoryTest {

    ProfileApi profApi;
    ProfileStore profStore;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        profApi = mock(ProfileApi.class);
        profStore = new ProfileStore() {
            Profile mProfile;
            @Override
            public void store(Profile profile) {
                mProfile = profile;
            }

            @Override
            public Profile get() {
                return mProfile;
            }

            @Override
            public void clear() {
                mProfile = null;
            }
        };
    }

    private void setUpApiForProfileResult() {
        String success =
                "{\n" +
                        "\t\"data\": {\n" +
                        "\t\t\"class\": {\n" +
                        "\t\t\t\"name\": \"A\",\n" +
                        "\t\t\t\"prog\": \"Informatika\",\n" +
                        "\t\t\t\"type\": \"Reguler\",\n" +
                        "\t\t\t\"year\": 2014\n" +
                        "\t\t},\n" +
                        "\t\t\"fcm_token\": \"\",\n" +
                        "\t\t\"name\": \"Aris\",\n" +
                        "\t\t\"username\": \"14.14.1.0002\"\n" +
                        "\t},\n" +
                        "\t\"message\": \"User account information\",\n" +
                        "\t\"success\": true\n" +
                        "}";


        Gson gson = new Gson();
        Type succRespType = new TypeToken<JsonResp<ProfileRespData>>(){}.getType();
        JsonResp<ProfileRespData> succResp = gson.fromJson(success, succRespType);
        reset(profApi);
        when(profApi.getProfile()).thenReturn(Single.just(succResp));
    }

    @Test
    public void getProfile_successTest() {
        setUpApiForProfileResult();

        TestSubscriber<Optional<Profile>> testSubscriber = new TestSubscriber<>();
        ProfileRepository repo = new ProfileRepository(profApi, profStore);
        repo.fetch().blockingAwait();

        verify(profApi).getProfile();

        repo.get().subscribe(testSubscriber);

        testSubscriber.assertNotComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);


        Profile p = profStore.get();
        assertNotNull(p);
        assertNotNull(p.getProfileClass());
        assertEquals("Aris", p.getName());
        assertEquals("14.14.1.0002", p.getUsername());
    }


//    private void setUpApiForUpdatePassword() {
//        String success = "{\n" +
//                "\t\"data\": {\n" +
//                "\t\t\"exp\": 1519029703,\n" +
//                "\t\t\"name\": \"Aris\",\n" +
//                "\t\t\"token\": \"random_token\",\n" +
//                "\t\t\"username\": \"14.14.1.0002\"\n" +
//                "\t},\n" +
//                "\t\"message\": \"Password updated\",\n" +
//                "\t\"success\": true\n" +
//                "}";
//
//        Gson gson = new Gson();
//        Type updSuccType = new TypeToken<JsonResp<ProfileUpdateRespData>>(){}.getType();
//        JsonResp<ProfileUpdateRespData> updSucc = gson.fromJson(success, updSuccType);
//        reset(profApi);
//        when(profApi.updatePassword(anyString(), anyString(), anyString())).thenReturn(Single.just(updSucc));
//    }
//
//    @Test
//    public void updatePassword_successTest() {
//        setUpApiForUpdatePassword();
//
//        ProfileRepository repo = new ProfileRepository(profApi, profStore);
//        repo.updatePassword("abc", "123")
//            .subscribe(new DisposableSingleObserver<ProfileUpdateResult>() {
//                @Override
//                public void onSuccess(ProfileUpdateResult res) {
//                    assertTrue(res.isSuccess());
//                    assertEquals("Aris", res.getName());
//                    assertEquals("random_token", res.getAccessToken());
//                    assertEquals("14.14.1.0002", res.getUsername());
//                    assertEquals(1519029703, res.getExpire());
//                }
//
//                @Override
//                public void onError(Throwable throwable) {}
//            });
//        verify(profApi).updatePassword("abc", "123", anyString());
//    }

}
