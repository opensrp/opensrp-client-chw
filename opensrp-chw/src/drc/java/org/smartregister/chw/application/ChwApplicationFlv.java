package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean checkP2PTeamId() {
        return false;
    }

    @Override
    public boolean hasFamilyKitCheck() {
        return true;
    }
}
