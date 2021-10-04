package org.smartregister.chw.domain;

import org.smartregister.chw.contract.ListContract;

public class ReportType implements ListContract.Identifiable {

    private final String id;
    private final String name;
    private String description;

    public ReportType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ReportType(String id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
