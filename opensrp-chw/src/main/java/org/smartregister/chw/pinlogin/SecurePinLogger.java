package org.smartregister.chw.pinlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.Utils;

import timber.log.Timber;

/**
 * provides additional login credentials services
 * allows a user to save the username and
 */
public class SecurePinLogger implements PinLogger {

    interface SecureConstants {
        String PIN_LOGIN = "chw-PinLogin";
        String SECURE_PIN = "chw-SecuredPin";
        String PREFERENCES_CONFIGURED = "chw-ConfigDone";
        String PASSWORD = "chw-Password";
    }

    private Context ctx = ChwApplication.getInstance().getApplicationContext();
    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

    @Override
    public boolean isPinSet() {
        String pin = preferences.getString(SecureConstants.SECURE_PIN, "");
        return StringUtils.isNotBlank(pin);
    }

    @Override
    public void setPinStatus(boolean status) {
        preferences.edit().putBoolean(SecureConstants.PREFERENCES_CONFIGURED, true).apply();
        preferences.edit().putBoolean(SecureConstants.PIN_LOGIN, status).apply();
    }

    @Override
    public boolean enabledPin() {
        return preferences.getBoolean(SecureConstants.PIN_LOGIN, false);
    }

    @Override
    public boolean isFirstAuthentication() {
        return !preferences.getBoolean(SecureConstants.PREFERENCES_CONFIGURED, false);
    }

    @Override
    public void setPin(String newPin, @Nullable EventListener eventListener) {
        preferences.edit().putBoolean(SecureConstants.PREFERENCES_CONFIGURED, true).apply();

        if (StringUtils.isBlank(newPin)) {
            if (eventListener != null)
                eventListener.OnError(new Exception("Invalid pin"));

            return;
        }

        if (newPin.length() < 4) {
            if (eventListener != null)
                eventListener.OnError(new Exception("Pin to short"));
        }

        preferences.edit().putString(SecureConstants.SECURE_PIN, newPin).apply();
        if (eventListener != null)
            eventListener.OnSuccess();
    }

    public void attemptPinVerification(@NonNull String pin, @Nullable EventListener eventListener) {
        if (StringUtils.isBlank(pin)) {
            if (eventListener != null)
                eventListener.OnError(new Exception("Invalid pin"));

            return;
        }

        String currentPin = preferences.getString(SecureConstants.PIN_LOGIN, "");
        if (pin.equals(currentPin)) {
            if (eventListener != null)
                eventListener.OnSuccess();
        } else {
            if (eventListener != null)
                eventListener.OnError(new Exception("Login failed"));
        }
    }

    @Nullable
    @Override
    public String loggedInUser() {
        try {
            String values = Utils.getPrefferedName();
            if (StringUtils.isNotBlank(values))
                return values.split(" ")[0];

        } catch (Exception e) {
            Timber.v(e);
        }
        return null;
    }

    @Nullable
    @Override
    public String getLoggedInUserName() {
        return Utils.getAllSharedPreferences().fetchRegisteredANM();
    }

    @Override
    public void resetPinLogin() {
        preferences.edit().remove(SecureConstants.PIN_LOGIN).apply();
        preferences.edit().remove(SecureConstants.PREFERENCES_CONFIGURED).apply();
        preferences.edit().remove(SecureConstants.SECURE_PIN).apply();
        preferences.edit().remove(SecureConstants.PASSWORD).apply();
    }

    @Override
    public void savePassword(String passWord) {
        preferences.edit().putString(SecureConstants.PASSWORD, passWord).apply();
    }

    @Override
    public String getPassword(String pin) {
        String currentPin = preferences.getString(SecureConstants.SECURE_PIN, "");
        if (pin.equals(currentPin))
            return preferences.getString(SecureConstants.PASSWORD, null);

        return null;
    }
}
