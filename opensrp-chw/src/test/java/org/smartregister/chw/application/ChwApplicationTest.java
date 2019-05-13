package org.smartregister.chw.application;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.immunization.ImmunizationLibrary;

public class ChwApplicationTest extends BaseUnitTest {

    @Test
    public void immunizationLibraryIsInitialisedOnStart() {
        Assert.assertNotNull(ImmunizationLibrary.getInstance());
    }
}
