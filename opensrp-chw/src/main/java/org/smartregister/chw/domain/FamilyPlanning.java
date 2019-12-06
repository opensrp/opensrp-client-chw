package org.smartregister.chw.domain;

import org.mvel2.ast.IfNode;

import java.util.Date;

public class FamilyPlanning {
    private String fpMethod;
    private Integer fpPillCycles;
    private Date fpStartDate;

    public FamilyPlanning(String fpMethod,Integer fpPillCycles, Date fpStartDate){
        this.fpMethod = fpMethod;
        this.fpPillCycles = fpPillCycles;
        this.fpStartDate = fpStartDate;
    }

    public String getFpMethod() {
        return fpMethod;
    }

    public Integer getFpPillCycles() {
        return fpPillCycles;
    }

    public Date getFpStartDate() {
        return fpStartDate;
    }
    public void setFpMethod(String fpMethod) {
        this.fpMethod = fpMethod;
    }

    public void setFpPillCycles(Integer fpPillCycles) {
        this.fpPillCycles = fpPillCycles;
    }

    public void setFpStartDate(Date fpStartDate) {
        this.fpStartDate = fpStartDate;
    }
}
