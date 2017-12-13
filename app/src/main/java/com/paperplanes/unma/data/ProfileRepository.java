package com.paperplanes.unma.data;

import com.google.firebase.iid.FirebaseInstanceId;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.data.network.api.ProfileApi;
import com.paperplanes.unma.data.network.api.response.ProfileRespData;
import com.paperplanes.unma.model.Profile;
import com.paperplanes.unma.model.ProfileUpdateResult;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 07/12/17.
 */

public class ProfileRepository {

    private FlowableProcessor<Optional<Profile>> mProcessor;
    private ProfileStore mProfileStore;
    private ProfileApi mApi;

    public ProfileRepository(ProfileApi api, ProfileStore profileStore) {
        mApi = api;
        mProfileStore = profileStore;
        mProcessor = PublishProcessor.create();
    }

    public Single<ProfileUpdateResult> updatePassword(String oldPassword, String newPassword) {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        return mApi.updatePassword(oldPassword, newPassword, fcmToken)
                .subscribeOn(Schedulers.io())
                .flatMap(resp -> {
                    ProfileUpdateResult res = new ProfileUpdateResult();
                    res.setSuccess(resp.isSuccess());
                    res.setMessage(resp.getMessage());

                    if (resp.isSuccess() && resp.getData() != null) {
                        res.setAccessToken(resp.getData().getAccessToken());
                        res.setExpire(resp.getData().getExpire());
                        res.setName(resp.getData().getName());
                        res.setUsername(resp.getData().getUsername());
                    }

                    return Single.just(res);
                });
    }

    public void clear() {
        mProfileStore.clear();
    }

    public Flowable<Optional<Profile>> get() {
        Optional<Profile> profileOptional = Optional.of(mProfileStore.get());
        return mProcessor.startWith(profileOptional);
    }

    public Completable fetch() {
        return mApi.getProfile()
                .subscribeOn(Schedulers.io())
                .flatMap(resp -> {
                    if (resp.isSuccess() && resp.getData() != null)
                        return Single.just(resp.getData());
                    return Single.never();
                })
                .map(resp -> {
                    ProfileRespData.ClassRespData respCls = resp.getRespClass();
                    Profile.ProfileClass cls = new Profile.ProfileClass(
                            respCls.getProg(),
                            respCls.getName(),
                            respCls.getYear(),
                            respCls.getType()
                    );

                    return new Profile(
                            resp.getName(),
                            resp.getUsername(),
                            cls
                    );
                })
                .doOnSuccess(profile -> mProfileStore.store(profile))
                .doOnSuccess(profile -> mProcessor.onNext(Optional.of(profile)))
                .toCompletable();
    }

}
