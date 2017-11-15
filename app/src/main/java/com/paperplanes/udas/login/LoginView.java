package com.paperplanes.udas.login;

/**
 * Created by abdularis on 02/11/17.
 */

public interface LoginView {

    void showLoading(boolean active);

    void showErrorMessage(String message);

    void showUsernameError(String errMessage);

    void showPasswordError(String errMessage);

    void onLoginSuccess(String username, String password, String authToken);

    void onLoginFailed(String message);
}
