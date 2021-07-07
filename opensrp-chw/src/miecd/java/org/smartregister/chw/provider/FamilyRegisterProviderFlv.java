package org.smartregister.chw.provider;

public class FamilyRegisterProviderFlv extends DefaultFamilyRegisterProviderFlv {
    @Override
    public boolean hasMalaria() {
        return true;
    }

    @Override
    public boolean hasFp(){return true;}
}
