package com.paperplanes.udas.presentation.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.paperplanes.udas.App;
import com.paperplanes.udas.R;
import com.paperplanes.udas.domain.model.Announcement;
import com.paperplanes.udas.domain.usecase.GetAnnouncementsInteractor;
import com.paperplanes.udas.domain.usecase.LogoutInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.DefaultObserver;

public class AnnouncementsActivity extends AppCompatActivity {

    @Inject
    LogoutInteractor mLogoutInteractor;

    @Inject
    GetAnnouncementsInteractor mAnnouncementsInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        ((App) getApplication()).getAppComponent().inject(this);

        mAnnouncementsInteractor.exec(null)
                .subscribe(announcements ->
                        {
                            for (Announcement ann: announcements) {
                                Log.v("AnnouncementList", ann.getTitle());
                            }
                        },
                        throwable -> Log.v("AnnouncementList", throwable.getMessage()));

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
    }

    public void doLogout() {
        mLogoutInteractor.exec(null)
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean success) {
                        if (success) {
                            goToLoginActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(AnnouncementsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                doLogout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
