package org.smartregister.chw.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.repository.Repository;

/**
 * @author rkodev
 */

@Implements(ImmunizationLibrary.class)
public class ImmunizationLibraryShadow {

    private static ImmunizationLibrary immunizationLibrary = Mockito.mock(ImmunizationLibrary.class);

    @Implementation
    public static ImmunizationLibrary getInstance() {
        return immunizationLibrary;
    }

    @Implementation
    public static void init(Context context, Repository repository, CommonFtsObject commonFtsObject, int applicationVersion, int databaseVersion) {
        // mute initialization
    }

    public static void setImmunizationLibrary(ImmunizationLibrary immunizationLibrary) {
        ImmunizationLibraryShadow.immunizationLibrary = immunizationLibrary;
    }
}
