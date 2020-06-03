package org.smartregister.chw.activity;

public class AncMemberProfileActivityFlv extends DefaultAncMemberProfileActivityFlv {
    @Override
    public Boolean hasFamilyLocationRow() {
        return true;
    }

    @Override
    public Boolean hasEmergencyTransport() {
        return true;
    }
}
