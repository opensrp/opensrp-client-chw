package org.smartregister.chw.application;

public class DefaultChwApplicationFlv implements ChwApplication.Flavor {
    @Override
    public boolean hasP2P() {
        return true;
    }

    @Override
    public boolean hasReferrals() {
        return false;
    }
}
