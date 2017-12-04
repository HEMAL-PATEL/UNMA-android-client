package com.paperplanes.unma.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.announcementdetail.AnnouncementDetailActivity;
import com.paperplanes.unma.announcementlist.AnnouncementListFragment;
import com.paperplanes.unma.announcementmedialist.MediaListFragment;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.ErrorUtil;
import com.paperplanes.unma.login.LoginActivity;
import com.paperplanes.unma.settings.AboutActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_CURRENT_FRAGMENT = "CURRENT_FRAGMENT";

    private static final String TAG_FRAG_ANNOUNCEMENT_LIST = "ANNOUNCEMENT_LIST";
    private static final String TAG_FRAG_MEDIA_LIST = "MEDIA_LIST";

    private String mCurrentFragmentTag;

    @BindView(R.id.content_layout) CoordinatorLayout mSnackbarContainer;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    @Inject
    SessionManager mSessionManager;

    private ActionBarDrawerToggle mDrawerToggle;

    private AnnouncementListFragment mAnnouncementListFragment;
    private MediaListFragment mMediaListFragment;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((App) getApplication()).getAppComponent().inject(this);

        setSupportActionBar(mToolbar);

        initNavigationDrawer();
        initViewModel();
        initFragments();

        if (savedInstanceState != null) {
            mCurrentFragmentTag = savedInstanceState.getString(STATE_CURRENT_FRAGMENT, TAG_FRAG_ANNOUNCEMENT_LIST);
            int menuId;
            if (mCurrentFragmentTag.equals(TAG_FRAG_MEDIA_LIST)) {
                menuId = R.id.menu_media;
            } else {
                menuId = R.id.menu_announcements;
            }

            selectNavigationItem(mNavigationView.getMenu().findItem(menuId));
        }
        else {
            selectNavigationItem(mNavigationView.getMenu().getItem(0));
        }
    }

    private void initFragments() {
        mMediaListFragment = new MediaListFragment();
        mAnnouncementListFragment = new AnnouncementListFragment();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel.class);
        mViewModel.getLogoutResult().observe(this, success -> {
            if (success != null && success) onLogoutSuccess();
        });
        mViewModel.getLogoutErr().observe(this, this::onLogoutFailed);
    }

    private void initNavigationDrawer() {
        mNavigationView.setNavigationItemSelectedListener(this::selectNavigationItem);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        TextView name = mNavigationView.getHeaderView(0).findViewById(R.id.nav_name);
        TextView username = mNavigationView.getHeaderView(0).findViewById(R.id.nav_username);

        if (mSessionManager.isSessionSet()) {
            name.setText(mSessionManager.getSession().getName());
            username.setText(mSessionManager.getSession().getUsername());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (!mCurrentFragmentTag.equals(TAG_FRAG_ANNOUNCEMENT_LIST)) {
            selectNavigationItem(mNavigationView.getMenu().findItem(R.id.menu_announcements));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_FRAGMENT, mCurrentFragmentTag);
    }

    private boolean selectNavigationItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_announcements:
                item.setChecked(true);
                goToAnnouncements();
                break;
            case R.id.menu_media:
                item.setChecked(true);
                goToMedia();
                break;
            case R.id.menu_settings:
                goToSettings();
                break;
            case R.id.menu_about:
                goToAbout();
                break;
            case R.id.menu_logout:
                onLogoutSelected();
                break;
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    private void onLogoutSelected() {
        mViewModel.logout();
    }

    private void goToAnnouncements() {
        mCurrentFragmentTag = TAG_FRAG_ANNOUNCEMENT_LIST;
        mToolbar.setTitle(getString(R.string.app_name));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content_layout, mAnnouncementListFragment, TAG_FRAG_ANNOUNCEMENT_LIST)
                .commit();
    }

    private void goToMedia() {
        mCurrentFragmentTag = TAG_FRAG_MEDIA_LIST;
        mToolbar.setTitle("Attachment Files");
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content_layout, mMediaListFragment, TAG_FRAG_MEDIA_LIST)
                .commit();
    }

    private void goToSettings() {
        // TODO implement settings menu
    }

    private void goToAbout() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    public void onLogoutSuccess() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void onLogoutFailed(Throwable throwable) {
        Snackbar.make(
                mSnackbarContainer,
                ErrorUtil.getErrorStringForThrowable(this, throwable),
                Snackbar.LENGTH_LONG)
                .show();
    }
}
