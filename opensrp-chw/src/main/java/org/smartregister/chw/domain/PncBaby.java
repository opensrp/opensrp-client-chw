package org.smartregister.chw.domain;

import org.smartregister.chw.core.domain.Person;

import java.util.Date;

public class PncBaby extends Person {
    private String lbw;
    private String gender;

    public PncBaby(String baseEntityID, String firstName, String lastName, String middleName, Date dob, String gender, String lbw) {
        super(baseEntityID, firstName, lastName, middleName, dob);
        this.lbw = lbw;
        this.gender = gender;
    }

    public String getLbw() {
        return lbw;
    }

    public String getGender() {
        return gender;
    }
}
