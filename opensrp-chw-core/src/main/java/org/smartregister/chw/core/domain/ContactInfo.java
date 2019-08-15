package org.smartregister.chw.core.domain;

public class ContactInfo {

    private String baseEntityId;
    private String key;
    private String value;
    private String eventDate;

    public ContactInfo() {
    }

    public ContactInfo(String baseEntityId, String key, String value) {
        this.baseEntityId = baseEntityId;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }
}
