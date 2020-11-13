package org.smartregister.chw.application;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
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

    @Test
    public void testP2PClassifierIsInitialized() {
        ChwApplication application = new ChwApplication();
        ChwApplicationFlv flv = Mockito.spy(new ChwApplicationFlv());
        Mockito.doReturn(true).when(flv).hasForeignData();

        ReflectionHelpers.setField(application, "flavor", flv);
        Assert.assertNotNull(application.getP2PClassifier());
    }
}
