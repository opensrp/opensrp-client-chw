package org.smartregister.chw.application;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.reporting.ReportingLibrary;

public class ChwApplicationTest extends BaseUnitTest {

    @Test
    public void reportingLibraryIsInitialisedOnStart() {
        Assert.assertNotNull(ReportingLibrary.getInstance());
    }
}
