package org.smartregister.chw.application;

public class ChwApplicationFlv implements ChwApplication.Flavor {
    @Override
    public boolean hasP2P() {
        return false;
    }

    @Override
    public boolean hasReferrals() {
        return true;
    }
}
