package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;

import static com.opensrp.chw.core.utils.Utils.firstCharacterUppercase;

public class UtilsTest extends BaseUnitTest {

    @Test
    public void firstCharacterUppercase_empty() {
<<<<<<< HEAD
        Assert.assertEquals("", firstCharacterUppercase(""));
=======
        Assert.assertEquals("", Utils.firstCharacterUppercase(""));
>>>>>>> 30e1bbcc39be8135be5c2e78791194db661bea33
    }

    @Test
    public void firstCharacterUppercase_with_one_character() {
<<<<<<< HEAD
        Assert.assertEquals("A", firstCharacterUppercase("a"));
=======
        Assert.assertEquals("A", Utils.firstCharacterUppercase("a"));
>>>>>>> 30e1bbcc39be8135be5c2e78791194db661bea33
    }

    @Test
    public void firstCharacterUppercase_with_two_word() {
<<<<<<< HEAD
        Assert.assertEquals("A b", firstCharacterUppercase("a b"));
=======
        Assert.assertEquals("A b", Utils.firstCharacterUppercase("a b"));
>>>>>>> 30e1bbcc39be8135be5c2e78791194db661bea33
    }
}
