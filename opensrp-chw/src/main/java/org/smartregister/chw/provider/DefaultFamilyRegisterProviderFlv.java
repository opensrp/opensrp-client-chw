package org.smartregister.chw.provider;

public abstract class DefaultFamilyRegisterProviderFlv implements FamilyRegisterProvider.Flavor {
    @Override
    public boolean hasMalaria() {
        return false;
    }
}
