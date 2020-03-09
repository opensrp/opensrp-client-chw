package org.smartregister.chw.pinlogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author rkodev
 */
public interface PinLogger {

    /**
     * check if user has completed pin setup
     *
     * @return
     */
    boolean isPinSet();


    /**
     * disable or enable pin login status
     *
     * @param status
     */
    void setPinStatus(boolean status);

    /**
     * check if the user has enabled pin login
     *
     * @return
     */
    boolean enabledPin();

    /**
     * check if the authentication done is the very first authentication
     *
     * @return
     */
    boolean isFirstAuthentication();

    /**
     * attempts to set / update the pin for the user
     *
     * @param newPin
     * @param eventListener
     */
    void setPin(String newPin, @Nullable EventListener eventListener);

    /**
     * logs in the authenticated user
     *
     * @param pin
     */
    boolean attemptPinVerification(@NonNull String pin, @Nullable EventListener eventListener);

    /**
     * returns actual logged in user name and null if a user is not logged in
     *
     * @return
     */
    @Nullable
    String loggedInUser();

    @Nullable
    String getLoggedInUserName();

    /**
     * Nuke everything related to pins
     */
    void resetPinLogin();

    /**
     * saves login password to the pin logger
     */
    void savePassword(String passWord);

    /**
     * returns password in the pin logger
     *
     * @return
     */
    String getPassword(String pin);

    interface EventListener {

        void onError(Exception ex);

        void onSuccess();

        void onEvent(String event);
    }
}
