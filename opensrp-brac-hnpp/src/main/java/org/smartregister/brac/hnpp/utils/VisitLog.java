package org.smartregister.brac.hnpp.utils;

public class VisitLog {

    public String VISIT_ID;
    public String VISIT_TYPE;
    public String BASE_ENTITY_ID;
    public Long VISIT_DATE;
    public String EVENT_TYPE;
    public String VISIT_JSON;

    public void setVisitId(String VISIT_ID) {
        this.VISIT_ID = VISIT_ID;
    }

    public void setVisitType(String VISIT_TYPE) {
        this.VISIT_TYPE = VISIT_TYPE;
    }

    public void setBaseEntityId(String BASE_ENTITY_ID) {
        this.BASE_ENTITY_ID = BASE_ENTITY_ID;
    }

    public void setVisitDate(Long VISIT_DATE) {
        this.VISIT_DATE = VISIT_DATE;
    }

    public void setEventType(String EVENT_TYPE) {
        this.EVENT_TYPE = EVENT_TYPE;
    }

    public void setVisitJson(String VISIT_JSON) {
        this.VISIT_JSON = VISIT_JSON;
    }

    public String getVisitId() {
        return VISIT_ID;
    }

    public String getVisitType() {
        return VISIT_TYPE;
    }

    public String getBaseEntityId() {
        return BASE_ENTITY_ID;
    }

    public Long getVisitDate() {
        return VISIT_DATE;
    }

    public String getEventType() {
        return EVENT_TYPE;
    }

    public String getVisitJson() {
        return VISIT_JSON;
    }
}
