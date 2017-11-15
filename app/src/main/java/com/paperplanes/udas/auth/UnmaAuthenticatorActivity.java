package com.paperplanes.udas.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paperplanes.udas.App;
import com.paperplanes.udas.R;
import com.paperplanes.udas.login.LoginPresenter;
import com.paperplanes.udas.login.LoginView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abdularis on 15/11/17.
 */

public class UnmaAuthenticatorActivity extends AccountAuthenticatorActivity implements LoginView {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    @BindView(R.id.edit_text_npm)
    EditText mNpm;
    @BindView(R.id.edit_text_password)
    EditText mPassword;
    @BindView(R.id.text_view_error)
    TextView mTextViewError;

    ProgressDialog dialog;

    @Inject
    LoginPresenter mPresenter;

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_login_2);

        Log.v("UnmaAuthActivity", "onCreate()");
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ((App) getApplication()).getAppComponent().inject(this);
        mAccountManager = AccountManager.get(this);

        mPresenter.setView(this);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked(View view) {
        String npm = mNpm.getText().toString();
        String password = mPassword.getText().toString();

        mPresenter.login(npm, password);
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
    public void onLoginSuccess(String username, String password, String authToken) {
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();

        Account account = new Account(mNpm.getText().toString(), getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            mAccountManager.addAccountExplicitly(account, password, null);
            mAccountManager.setAuthToken(account, "unma.access", authToken);
        } else {
            mAccountManager.setPassword(account, password);
        }

        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        setAccountAuthenticatorResult(data);

        Intent i = new Intent();
        i.putExtras(data);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onLoginFailed(String message) {
        mTextViewError.setText(message);
        mTextViewError.setVisibility(View.VISIBLE);
    }
}
