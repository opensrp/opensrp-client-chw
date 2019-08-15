package org.smartregister.chw.core.model;

import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

public class ServiceTaskModel {
    private Map<String, ServiceWrapper> givenServiceMap;
    private Map<String, ServiceWrapper> notGivenServiceMap;

    public Map<String, ServiceWrapper> getGivenServiceMap() {
        return givenServiceMap;
    }

    public void setGivenServiceMap(Map<String, ServiceWrapper> givenServiceMap) {
        this.givenServiceMap = givenServiceMap;
    }

    public Map<String, ServiceWrapper> getNotGivenServiceMap() {
        return notGivenServiceMap;
    }

    public void setNotGivenServiceMap(Map<String, ServiceWrapper> notGivenServiceMap) {
        this.notGivenServiceMap = notGivenServiceMap;
    }
}
