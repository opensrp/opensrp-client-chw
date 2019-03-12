package org.smartgresiter.wcaro.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartregister.family.contract.FamilyOtherMemberContract;

/**
 * Created by keyman on 11/03/2019.
 */
public class FamilyOtherMemberActivityPresenterTest extends BaseUnitTest {

    @Mock
    private FamilyOtherMemberContract.View view;

    @Mock
    private FamilyOtherMemberContract.Model model;

    private FamilyOtherMemberContract.Presenter presenter;

    private String viewConfigurationIdentifier;
    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        viewConfigurationIdentifier = "viewConfigurationIdentifier";
        familyBaseEntityId = "familyBaseEntityId";
        baseEntityId = "baseEntityId";
        familyHead = "familyHead";
        primaryCaregiver = "primaryCaregiver";
        villageTown = "villageTown";
        familyName = "familyName";

        presenter = new FamilyOtherMemberActivityPresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Test
    public void testFields() {
        FamilyOtherMemberActivityPresenter familyOtherMemberActivityPresenter = (FamilyOtherMemberActivityPresenter) presenter;
        Assert.assertEquals("familyBaseEntityId", familyOtherMemberActivityPresenter.getFamilyBaseEntityId());
        Assert.assertEquals("familyName", familyOtherMemberActivityPresenter.getFamilyName());
    }

}
