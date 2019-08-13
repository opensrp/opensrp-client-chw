package org.smartregister.chw.core.helper;

import java.util.HashSet;
import java.util.Set;

public class ContactRule {

    public static final String RULE_KEY = "contactRule";

    public boolean isFirst;

    public int initialVisit;

    public int currentVisit;

    public Set<Integer> set;

    public ContactRule(int wks, boolean isFirst) {

        this.initialVisit = wks;
        this.currentVisit = wks;

        this.isFirst = isFirst;

        this.set = new HashSet<>();
    }

}
