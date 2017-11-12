package com.paperplanes.udas.domain.usecase;

import com.paperplanes.udas.domain.data.AnnouncementDataSource;
import com.paperplanes.udas.domain.executor.ExecutionScheduler;
import com.paperplanes.udas.domain.executor.PostExecutionScheduler;
import com.paperplanes.udas.domain.model.Announcement;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by abdularis on 08/11/17.
 */

public class GetAnnouncementsInteractor extends BaseInteractor<List<Announcement>, Void> {

    private AnnouncementDataSource mDataSource;

    @Inject
    public GetAnnouncementsInteractor(AnnouncementDataSource dataSource,
                                      ExecutionScheduler execution,
                                      PostExecutionScheduler postExecution) {
        super(execution, postExecution);
        mDataSource = dataSource;
    }

    @Override
    protected Observable<List<Announcement>> buildObservable(Void params) {
        return mDataSource.getAnnouncementList();
    }
}
