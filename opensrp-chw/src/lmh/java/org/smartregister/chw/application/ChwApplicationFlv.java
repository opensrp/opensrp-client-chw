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
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasRoutineVisit() {
        return false;
    }

    @Override
    public boolean hasPinLogin() {
        return true;
    }

    @Override
    public boolean hasReports() {
        return true;
    }

    @Override
    public boolean hasJobAids() {
        return false;
    }

}
