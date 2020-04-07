package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean hasP2P() {
        return false;
    }

    @Override
    public boolean hasReferrals() {
        return true;
    }

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
    public boolean hasFamilyPlanning() {
        return true;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasServiceReport() {
        return false;
    }

    public boolean hasQR() {
        return true;
    }

    @Override
    public boolean hasJobAids() {
        return false;
    }

    @Override
    public boolean hasTasks() {
        return true;
    }

    @Override
    public boolean hasStockUsageReport() {
        return false;
    }
}
