package org.smartregister.chw.fragment;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.junit.Assert;

public class FamilyOtherMemberProfileFragmentTest extends BaseUnitTest {

    @Test
    public void testNewInstance() {
        BaseFamilyOtherMemberProfileFragment fragment = FamilyOtherMemberProfileFragment.newInstance(null);
        Assert.assertNotNull(fragment.getArguments());

        Bundle bundle = Mockito.mock(Bundle.class);
        BaseFamilyOtherMemberProfileFragment fragment1 = FamilyOtherMemberProfileFragment.newInstance(bundle);
        Assert.assertEquals(bundle, fragment1.getArguments());
    }

    @Test
    public void getFamilyOtherMemberProfileFragmentPresenter() {
        FamilyOtherMemberProfileFragment fragment = new FamilyOtherMemberProfileFragment();
        Assert.assertNotNull(fragment.getFamilyOtherMemberProfileFragmentPresenter("4a150309-bff0-4870-a3d7-0f042bb160e4", "00ebf410-0fb2-4610-9f0b-39e3b7736688"));
    }
}