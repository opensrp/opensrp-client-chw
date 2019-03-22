package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;

public class ConstantsTest {

    @Test
    public void testEducationLevelMap(){
        Assert.assertTrue(org.smartregister.chw.util.Constants.FORM_CONSTANTS.EDUCATION_LEVELS.size() > 0);
        Assert.assertEquals("1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", org.smartregister.chw.util.Constants.FORM_CONSTANTS.EDUCATION_LEVELS.get("Secondary"));
    }
}
