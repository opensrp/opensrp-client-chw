package org.smartgresiter.wcaro.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartgresiter.wcaro.R;

public class ChildUtilsTest extends BaseUnitTest {

    @Test
    public void daysAway_awayTest(){
        //2019-04-08
        //20 days away
        SpannableString spannableString = ChildUtils.daysAway("2019-04-08");
        ForegroundColorSpan[] colorSpans =spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class);
        Assert.assertTrue(colorSpans[0].getForegroundColor() == Color.GRAY);
    }
    @Test
    public void daysAway_overdueTest(){
        //2015-03-20
        //1460 days overdue color code= -1030586
        SpannableString spannableString = ChildUtils.daysAway("2015-03-20");
        ForegroundColorSpan[] colorSpans =spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class);
        Assert.assertTrue(colorSpans[0].getForegroundColor() == -1030586);
    }

}
