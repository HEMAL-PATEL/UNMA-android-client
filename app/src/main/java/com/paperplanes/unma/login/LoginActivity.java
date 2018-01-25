package com.paperplanes.unma.login;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.AppUtil;
import com.paperplanes.unma.help.HelpActivity;
import com.paperplanes.unma.main.MainActivity;
import com.paperplanes.unma.model.Profile;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_username)
    EditText mUsername;
    @BindView(R.id.text_layout_username)
    TextInputLayout mTextInputLayoutUsername;
    @BindView(R.id.edit_text_password)
    EditText mPassword;
    @BindView(R.id.text_view_error)
    TextView mTextViewError;
    @BindView(R.id.user_type_checkbox)
    CheckBox mUserType;

    ProgressDialog dialog;

    @Inject
    SessionManager mSessionManager;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    LoginViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUtil.checkGooglePlayServicesAvailability(this);

        ((App) getApplication()).getAppComponent().inject(this);
        if (mSessionManager.isSessionSet()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViewModel();

        mUserType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mTextInputLayoutUsername.setHint(getString(R.string.text_nidn));
            } else {
                mTextInputLayoutUsername.setHint(getString(R.string.text_npm));
            }

            mUsername.setError(null);
        });
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        mViewModel.getLoading().observe(this, this::showLoading);
        mViewModel.getUsernameErr().observe(this, this::showUsernameError);
        mViewModel.getPasswordErr().observe(this, this::showPasswordError);
        mViewModel.getGeneralErr().observe(this, this::showError);
        mViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult != null) {
                if (loginResult.isSuccess()) {
                    onLoginSuccess();
                }
                else {
                    onLoginFailed(loginResult.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked(View view) {
        String npm = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        int userType = Profile.USER_TYPE_STUDENT;
        if (mUserType.isChecked())
            userType = Profile.USER_TYPE_LECTURER;

        mViewModel.login(npm, password, userType);
    }

    @OnClick(R.id.btn_help)
    public void onHelpClicked(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void showLoading(boolean active) {
        if (active) {
            mTextViewError.setVisibility(View.GONE);
            dialog = ProgressDialog.show(this, null, getResources().getString(R.string.msg_logging_in));
        } else if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showError(String message) {
        mTextViewError.setText(message);
        mTextViewError.setVisibility(View.VISIBLE);
    }

    public void showUsernameError(String errMessage) {
        mUsername.setError(errMessage);
        mUsername.requestFocus();
    }

    public void showPasswordError(String errMessage) {
        mPassword.setError(errMessage);
        mPassword.requestFocus();
    }

    public void onLoginSuccess() {
        Toast.makeText(this, getResources().getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show();
        goToMainActivity();
    }

    public void onLoginFailed(String message) {
        mTextViewError.setText(message);
        mTextViewError.setVisibility(View.VISIBLE);
    }
}
