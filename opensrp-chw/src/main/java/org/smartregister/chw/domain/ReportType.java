package org.smartregister.chw.domain;

import org.smartregister.chw.contract.ListContract;

public class ReportType implements ListContract.Identifiable {

    public ReportType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private String id;
    private String name;

    @Override
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
