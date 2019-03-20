package org.smartregister.chw.util;

public class ServiceContent implements BaseService {
    String serviceName;
    //String serviceDate;
    String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

//    public String getServiceDate() {
//        return serviceDate;
//    }
//
//    public void setServiceDate(String serviceDate) {
//        this.serviceDate = serviceDate;
//    }


    @Override
    public int getType() {
        return TYPE_CONTENT;
    }
}
