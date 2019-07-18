package org.smartregister.chw.util;

import org.junit.Test;

public class JsonFormUtilsTest {

    @Test
    public void getTimeZone() {
        String timeZone = JsonFormUtils.getTimeZone();
        boolean matches = timeZone.matches("^\\+\\d\\d:\\d0$");
        assert (matches);
    }
}
