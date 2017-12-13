package com.paperplanes.unma.profileupdate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.ViewModelFactory;
import com.paperplanes.unma.common.ErrorUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileUpdateActivity extends AppCompatActivity {

    @BindView(R.id.old_password) TextView mOldPassword;
    @BindView(R.id.new_password) TextView mNewPassword;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.text_view_error) TextView mTextError;

    @Inject
    ViewModelFactory mViewModelFactory;
    ProfileUpdateViewModel mViewModel;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        ButterKnife.bind(this);
        ((App) getApplication()).getAppComponent().inject(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Update Password");
        }

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProfileUpdateViewModel.class);
        mViewModel.getLoading().observe(this, this::showLoading);
        mViewModel.getSuccess().observe(this, this::onSuccess);
        mViewModel.getError().observe(this, this::showErrorThrowable);
    }

    private void showErrorThrowable(Throwable throwable) {
        mTextError.setVisibility(View.VISIBLE);
        mTextError.setText(ErrorUtil.getErrorStringForThrowable(this, throwable));
    }

    private void onSuccess(Void voidParam) {
        if (dialog != null)
            dialog.dismiss();
        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void showLoading(Boolean show) {
        if (show) {
            mTextError.setVisibility(View.GONE);
            dialog = ProgressDialog.show(this, null, "Updating...");
        } else if (dialog != null) {
            dialog.dismiss();
        }
    }

    @OnClick(R.id.update_btn)
    public void updatePassword(View view) {
        String oldPassword = mOldPassword.getText().toString();
        String newPassword = mNewPassword.getText().toString();
        mViewModel.changePassword(oldPassword, newPassword);
    }
}
