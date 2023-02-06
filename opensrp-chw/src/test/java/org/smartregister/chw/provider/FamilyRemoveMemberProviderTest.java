package org.smartregister.chw.provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FamilyRemoveMemberProviderTest {

    private FamilyRemoveMemberProvider familyRemoveMemberProvider;

    @Before
    public void setUp() {
        familyRemoveMemberProvider = Mockito.mock(FamilyRemoveMemberProvider.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getFamilyRemoveMemberInteractor() {
        Assert.assertNotNull(familyRemoveMemberProvider.getFamilyRemoveMemberInteractor());
    }
}