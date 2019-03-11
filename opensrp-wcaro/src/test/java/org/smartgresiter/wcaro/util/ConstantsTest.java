package org.smartgresiter.wcaro.util;

import org.junit.Assert;
import org.junit.Test;

public class ConstantsTest {

    @Test
    public void testEducationLevelMap(){
        Assert.assertTrue(org.smartgresiter.wcaro.util.Constants.FORM_CONSTANTS.EDUCATION_LEVELS.size() > 0);
        Assert.assertEquals("1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", org.smartgresiter.wcaro.util.Constants.FORM_CONSTANTS.EDUCATION_LEVELS.get("Secondary"));
    }
}
