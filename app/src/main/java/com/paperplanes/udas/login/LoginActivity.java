package com.paperplanes.udas.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paperplanes.udas.App;
import com.paperplanes.udas.R;
import com.paperplanes.udas.data.FirebaseTokenRefreshService;
import com.paperplanes.udas.auth.SessionManager;
import com.paperplanes.udas.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.edit_text_npm)
    EditText mNpm;
    @BindView(R.id.edit_text_password)
    EditText mPassword;
    @BindView(R.id.text_view_error)
    TextView mTextViewError;

    ProgressDialog dialog;

    @Inject
    LoginPresenter mPresenter;

    @Inject
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ((App) getApplication()).getAppComponent().inject(this);

        Intent i = new Intent(this, FirebaseTokenRefreshService.class);
        startService(i);

        mPresenter.setView(this);
        if (mSessionManager.isSessionSet()) {
            goToMainActivity();
        }
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked(View view) {
        String npm = mNpm.getText().toString();
        String password = mPassword.getText().toString();

        mPresenter.login(npm, password);
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void showLoading(boolean active) {
        if (active) {
            mTextViewError.setVisibility(View.GONE);
            dialog = ProgressDialog.show(this, null, "Logging in...");
        } else if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        mTextViewError.setText(message);
        mTextViewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUsernameError(String errMessage) {
        mNpm.setError(errMessage);
    }

    @Override
    public void showPasswordError(String errMessage) {
        mPassword.setError(errMessage);
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        goToMainActivity();
    }

    @Override
    public void onLoginFailed(String message) {
        mTextViewError.setText(message);
        mTextViewError.setVisibility(View.VISIBLE);
    }
}
