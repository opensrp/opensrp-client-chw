package org.smartregister.chw.util;


public class ServiceContent implements BaseService {
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public int getType() {
        return TYPE_CONTENT;
    }
}
