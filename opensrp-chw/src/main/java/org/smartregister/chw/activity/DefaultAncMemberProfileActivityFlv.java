package org.smartregister.chw.activity;

public class DefaultAncMemberProfileActivityFlv implements AncMemberProfileActivity.Flavor {
    @Override
    public Boolean hasFamilyLocationRow() {
        return false;
    }

    @Override
    public Boolean hasEmergencyTransport() {
        return false;
    }
}
