package org.smartgresiter.wcaro.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartgresiter.wcaro.domain.FamilyMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FamilyChangeContractModelTest {

    List<FamilyMember> members = new ArrayList<>();
    String[] skipped = new String[]{"2", "3", "5"};
    int max = 20;
    int min = 1;
    int range = max - min + 1;

    String primaryCareID;
    String headOfHouseID;

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
