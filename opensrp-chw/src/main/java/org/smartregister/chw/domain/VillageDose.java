package org.smartregister.chw.domain;

import org.smartregister.chw.contract.ListContract;

import java.util.HashMap;
import java.util.Map;

public class VillageDose implements ListContract.Identifiable {
    private String ID;
    private String villageName;
    private Map<String, Number> recurringServices = new HashMap<>();

    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public Map<String, Number> getRecurringServices() {
        return recurringServices;
    }

    public void setRecurringServices(Map<String, Number> recurringServices) {
        this.recurringServices = recurringServices;
    }
}
