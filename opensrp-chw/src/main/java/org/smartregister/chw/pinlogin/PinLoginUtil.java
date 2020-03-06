package org.smartregister.chw.pinlogin;

public class PinLoginUtil {

    private static PinLogger pinLogger;

    public static PinLogger getPinLogger(){

        if(pinLogger == null){
            pinLogger = new PinLogger() {
                @Override
                public boolean isPinSet() {
                    return true;
                }

                @Override
                public boolean isFirstAuthentication() {
                    return false;
                }

                @Override
                public void setPin(String newPin) {

                }

                @Override
                public void attemptPinVerification(String pin) {

                }
            };
        }
        return pinLogger;
    }
}
