package com.paperplanes.unma.auth;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public abstract class SessionManager {

    private PublishSubject<LogoutEvent> mPublishLogout;

    public SessionManager() {
        mPublishLogout = PublishSubject.create();
    }

    public void setSession(Session session) {
    }

    public abstract boolean isSessionSet();

    public abstract Session getSession();

    public void clearSession(LogoutEvent.Cause cause) {
        mPublishLogout.onNext(new LogoutEvent(cause));
    }

    public void observeOnLogout(Scheduler callScheduler,
                                Consumer<? super LogoutEvent> callback) {
        mPublishLogout
                .observeOn(callScheduler)
                .subscribe(callback);
    }

    public void observeOnLogout(Scheduler callScheduler,
                                LogoutEvent.Cause causeFilter,
                                Consumer<? super LogoutEvent> callback) {
        mPublishLogout
                .observeOn(callScheduler)
                .filter(event -> event.getCause() == causeFilter)
                .subscribe(callback);
    }

    public static class LogoutEvent {
        public enum Cause {
            Intentional,
            UnIntentional
        }

        private Cause mCause;

        LogoutEvent(Cause cause) {
            mCause = cause;
        }

        public Cause getCause() {
            return mCause;
        }
    }
}
