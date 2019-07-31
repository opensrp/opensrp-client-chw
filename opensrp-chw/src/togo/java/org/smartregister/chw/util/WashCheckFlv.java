package org.smartregister.chw.util;

import org.smartregister.chw.fragment.FamilyProfileDueFragment;

public class WashCheckFlv implements FamilyProfileDueFragment.Flavor {
    @Override
    public boolean isWashCheckVisible() {
        return true;
    }
}
