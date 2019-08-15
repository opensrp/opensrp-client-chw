package org.smartregister.chw.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.model.FamilyChangeContractModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FamilyChangeContractModelTest {

    private List<FamilyMember> members = new ArrayList<>();
    private String[] skipped = new String[]{"2", "3", "5"};
    private int max = 20;
    private int min = 1;
    private int range = max - min + 1;

    private String primaryCareID;
    private String headOfHouseID;

    @Before
    public void setUp() {
        primaryCareID = String.valueOf((int) (Math.random() * range) + min);
        headOfHouseID = String.valueOf((int) (Math.random() * range) + min);

        int l = max;

        while (l > 0) {
            FamilyMember member = new FamilyMember();
            member.setMemberID(String.valueOf(l));

            members.add(member);
            l--;
        }

    }

    @Test
    public void testGetMembersExcluding() {
        FamilyChangeContractModel changeContractModel = new FamilyChangeContractModel();

        List<FamilyMember> res_member = changeContractModel.getMembersExcluding(members, primaryCareID, headOfHouseID, skipped);

        // verify that the number of returned objects is less the number skipped
        Assert.assertEquals(res_member.size(), (members.size() - skipped.length));

        // verify that the excluded strings are not involved in the party
        List<String> skipper = Arrays.asList(skipped);

        for (FamilyMember m : res_member) {
            Assert.assertTrue((!skipper.contains(m.getMemberID())));
        }

    }
}
