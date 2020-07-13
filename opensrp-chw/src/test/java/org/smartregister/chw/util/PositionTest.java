package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    @Test
    public void testParent() {
        // root
        Position root = new Position("Kenya");

        Position nax = new Position(root, "Nax");
        Position max_a = new Position(nax, "Rongai");
        Position max_b = new Position(nax, "Town");


        Position meru = new Position(root, "Meru");
        Position chuka = new Position(meru, "Chuka");
        Position nkubu = new Position(meru, "Nkubu");
        Position maua = new Position(meru, "Maua");

        Assert.assertTrue(root.isParentOf(meru));
        Assert.assertTrue(root.isParentOf(nax));
        Assert.assertTrue(root.isParentOf(chuka));
        Assert.assertFalse(meru.isParentOf(meru));

        Assert.assertTrue(meru.isParentOf(maua));
        Assert.assertFalse(meru.isParentOf(nax));
        Assert.assertFalse(nax.isParentOf(chuka));
        Assert.assertFalse(nax.isParentOf(nkubu));


        Assert.assertTrue(nax.isParentOf(max_b));
    }
}
