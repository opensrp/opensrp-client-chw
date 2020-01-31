package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean hasANC() {
        return false;
    }

    @Override
    public boolean hasPNC() {
        return false;
    }

    @Override
    public boolean hasChildSickForm() {
        return true;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasRoutineVisit() {
        return true;
    }

    @Override
    public boolean hasMalaria() {
        return true;
    }
}
