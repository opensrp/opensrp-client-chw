package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean showChildrenUnderFiveAndGirlsAgeNineToEleven() {
        return true;
    }

    @Override
    public boolean hasChildSickForm() {
        return true;
    }
}
