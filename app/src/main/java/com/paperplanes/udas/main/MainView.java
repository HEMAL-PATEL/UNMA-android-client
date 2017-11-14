package com.paperplanes.udas.main;

/**
 * Created by abdularis on 14/11/17.
 */

public interface MainView {

    void showError(String errMsg);

    void onLogoutSuccess();

    void onLogoutFailed(String msg);

}
