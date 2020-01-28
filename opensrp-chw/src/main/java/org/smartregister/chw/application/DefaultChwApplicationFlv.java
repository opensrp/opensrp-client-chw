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

    @Override
    public boolean hasANC() {
        return true;
    }

    @Override
    public boolean hasPNC() {
        return true;
    }

    @Override
    public boolean hasChildSickForm() {
        return false;
    }

    @Override
    public boolean hasFamilyPlanning() {
        return false;
    }

    @Override
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return true;
    }

    @Override
    public boolean hasRoutineVisit() {
        return false;
    }
}
