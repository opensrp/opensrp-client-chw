package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.utils.Utils;

import static org.smartregister.chw.util.Utils.formatDateForVisual;

public class UtilsTest extends BaseUnitTest {

    @Test
    public void firstCharacterUppercase_empty() {
        Assert.assertEquals("", Utils.firstCharacterUppercase(""));
    }

    @Test
    public void firstCharacterUppercase_with_one_character() {
        Assert.assertEquals("A", Utils.firstCharacterUppercase("a"));
    }

    @Test
    public void firstCharacterUppercase_with_two_word() {
        Assert.assertEquals("A b", Utils.firstCharacterUppercase("a b"));
    }


    @Test
    public void testFormatDateForVisual() {
        String date = "2020-06-23";
        String inputFormat = "yyyy-MM-dd";
        String formattedDate = formatDateForVisual(date, inputFormat);
        Assert.assertEquals(formattedDate, "23 Jun 2020");
    }

}
