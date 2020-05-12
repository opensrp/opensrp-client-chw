package org.smartregister.chw.model;

public class FamilyDetailsModel {

    private String baseEntityId;
    private String familyHead;
    private String primaryCareGiver;
    private String familyName;
    private String villageTown;

    public FamilyDetailsModel(String baseEntityId, String familyHead, String primaryCareGiver, String familyName) {
        this.baseEntityId = baseEntityId;
        this.familyHead = familyHead;
        this.primaryCareGiver = primaryCareGiver;
        this.familyName = familyName;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getFamilyHead() {
        return familyHead;
    }

    public void setFamilyHead(String familyHead) {
        this.familyHead = familyHead;
    }

    public String getPrimaryCareGiver() {
        return primaryCareGiver;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getVillageTown() {
        return villageTown;
    }

    public void setVillageTown(String villageTown) {
        this.villageTown = villageTown;
    }
}
