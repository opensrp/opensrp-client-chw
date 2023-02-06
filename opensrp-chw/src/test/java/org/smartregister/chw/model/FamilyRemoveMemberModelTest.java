package org.smartregister.chw.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.List;

public class FamilyRemoveMemberModelTest extends BaseUnitTest {

    @Mock
    private CommonPersonObjectClient personObjectClient;

    private FamilyRemoveMemberModel familyRemoveMemberModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        familyRemoveMemberModel = new FamilyRemoveMemberModel();
    }

    @Test
    public void testGetValidForm(){
        String form = familyRemoveMemberModel.getForm(personObjectClient);
        List<String> validForms = new ArrayList<String>(2);
        validForms.add(CoreConstants.JSON_FORM.getFamilyDetailsRemoveMember());
        validForms.add(CoreConstants.JSON_FORM.getFamilyDetailsRemoveChild());
        Assert.assertTrue(validForms.contains(form));
    }
}