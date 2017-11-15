package com.paperplanes.udas.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by abdularis on 15/11/17.
 */

public class UnmaAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "UnmaAuthenticator";
    private Context mContext;

    public UnmaAuthenticator(Context context) {
        super(context);
        Log.v(TAG, "UnmaAuthenticator()");
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.v(TAG, "editProperties()");
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.v(TAG, "addAccount()");

        final Intent i = new Intent(mContext, UnmaAuthenticatorActivity.class);
        i.putExtra(UnmaAuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        i.putExtra(UnmaAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        i.putExtra(UnmaAuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, i);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        Log.v(TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        Log.v(TAG, "getAuthToken()");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.v(TAG, "getAuthTokenLabel()");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options) throws NetworkErrorException {
        Log.v(TAG, "updateCredentials()");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) throws NetworkErrorException {
        Log.v(TAG, "hasFeatures()");
        return null;
    }
}
