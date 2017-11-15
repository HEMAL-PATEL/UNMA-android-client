package com.paperplanes.udas.main;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paperplanes.udas.App;
import com.paperplanes.udas.R;
import com.paperplanes.udas.announcementdetail.AnnouncementDetailFragment;
import com.paperplanes.udas.announcementlist.AnnouncementListFragment;
import com.paperplanes.udas.auth.SessionManager;
import com.paperplanes.udas.login.LoginActivity;
import com.paperplanes.udas.model.Announcement;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.content_layout)
    FrameLayout mContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Inject
    SessionManager mSessionManager;

    ActionBarDrawerToggle mDrawerToggle;

    private AnnouncementListFragment mAnnouncementListFragment;
    private AnnouncementDetailFragment mAnnouncementDetailFragment;

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((App) getApplication()).getAppComponent().inject(this);

        setSupportActionBar(mToolbar);

        mAnnouncementDetailFragment = new AnnouncementDetailFragment();
        mAnnouncementListFragment = new AnnouncementListFragment();
        mAnnouncementListFragment.setOnItemClickListener(announcement -> {
            mAnnouncementDetailFragment.setCurrentAnnouncement(announcement);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.content_layout, mAnnouncementDetailFragment)
                    .commit();
        });

        mNavigationView.setNavigationItemSelectedListener(this::selectNavigationItem);
        selectNavigationItem(mNavigationView.getMenu().getItem(0));

        mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mPresenter.setView(this);

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
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> fragmentList = fm.getFragments();
            if (fragmentList != null && fragmentList.size() > 0) {
                if (fragmentList.get(0) instanceof AnnouncementDetailFragment) {
                    goToAnnouncements();
                    return;
                }
            }

            super.onBackPressed();
        }
    }

    private boolean selectNavigationItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_announcements:
                item.setChecked(true);
                goToAnnouncements();
                break;
            case R.id.menu_logout:
                onLogoutSelected();
                break;
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    private void onLogoutSelected() {
        mPresenter.logout();
    }

    private void goToAnnouncements() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content_layout, mAnnouncementListFragment)
                .commit();
    }

    @Override
    public void showError(String errMsg) {

    }

    @Override
    public void onLogoutSuccess() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onLogoutFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
