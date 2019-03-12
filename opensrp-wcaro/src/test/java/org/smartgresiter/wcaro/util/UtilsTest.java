package org.smartgresiter.wcaro.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartgresiter.wcaro.BaseUnitTest;

public class UtilsTest extends BaseUnitTest {

    @Test
    public void firstCharacterUppercase_empty(){
        Assert.assertEquals("",Utils.firstCharacterUppercase(""));
    }
    @Test
    public void firstCharacterUppercase_with_one_character(){
        Assert.assertEquals("A",Utils.firstCharacterUppercase("a"));
    }
    @Test
    public void firstCharacterUppercase_with_two_word(){
        Assert.assertEquals("A b",Utils.firstCharacterUppercase("a b"));
    }
}
