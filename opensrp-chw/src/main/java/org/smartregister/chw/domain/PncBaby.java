package org.smartregister.chw.domain;

import org.smartregister.chw.core.domain.Person;

import java.util.Date;

public class PncBaby extends Person {
    private String lbw;

    public PncBaby(String baseEntityID, String firstName, String lastName, String middleName, Date dob, String lbw) {
        super(baseEntityID, firstName, lastName, middleName, dob);
        this.lbw = lbw;
    }

    public String getLbw() {
        return lbw;
    }
}
