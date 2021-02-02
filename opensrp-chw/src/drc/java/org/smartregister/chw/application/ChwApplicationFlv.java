package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {

    @Override
    public boolean hasFamilyKitCheck() {
        return true;
    }

    @Override
    public boolean showsPhysicallyDisabledView() {
        return true;
    }
}
