package org.smartregister.chw.application;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.reporting.ReportingLibrary;

public class CoreChwApplicationTest extends BaseUnitTest {

    @Test
    public void immunizationLibraryIsInitialisedOnStart() {
        Assert.assertNotNull(ImmunizationLibrary.getInstance());
    }

    @Test
    public void reportingLibraryIsInitialisedOnStart() {
        Assert.assertNotNull(ReportingLibrary.getInstance());
    }

}
