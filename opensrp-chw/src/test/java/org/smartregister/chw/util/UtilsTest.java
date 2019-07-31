package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;

import static com.opensrp.chw.core.utils.Utils.firstCharacterUppercase;

public class UtilsTest extends BaseUnitTest {

    @Test
    public void firstCharacterUppercase_empty(){
        Assert.assertEquals("", firstCharacterUppercase(""));
    }
    @Test
    public void firstCharacterUppercase_with_one_character(){
        Assert.assertEquals("A",firstCharacterUppercase("a"));
    }
    @Test
    public void firstCharacterUppercase_with_two_word(){
        Assert.assertEquals("A b",firstCharacterUppercase("a b"));
    }
}
