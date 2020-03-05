package org.smartregister.chw.pinlogin;

public interface PinLogger {

    boolean isPinSet();

    boolean isFirstAuthentication();

    void setPin(String newPin);

    void attemptPinVerification(String pin);

}
