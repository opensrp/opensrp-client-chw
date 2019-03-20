package org.smartgresiter.wcaro.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;

/**
 * Created by keyman on 11/03/2019.
 */
public class FamilyOtherMemberProfileFragmentPresenterTest extends BaseUnitTest {

    @Mock
    private FamilyOtherMemberProfileFragmentContract.View view;

    @Mock
    private FamilyOtherMemberProfileFragmentContract.Model model;

    private FamilyOtherMemberProfileFragmentContract.Presenter presenter;

    private String viewConfigurationIdentifier;
    private String familyBaseEntityId;
    private String baseEntityId;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        viewConfigurationIdentifier = "viewConfigurationIdentifier";
        familyBaseEntityId = "familyBaseEntityId";
        baseEntityId = "baseEntityId";

        presenter = new FamilyOtherMemberProfileFragmentPresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId);
    }

    @Test
    public void testMainCondition() {
        FamilyOtherMemberProfileFragmentPresenter familyOtherMemberProfileFragmentPresenter = (FamilyOtherMemberProfileFragmentPresenter) presenter;
        Assert.assertEquals(" object_id = 'baseEntityId' and date_removed is null ", familyOtherMemberProfileFragmentPresenter.getMainCondition());
    }

}
