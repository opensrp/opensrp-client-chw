package org.smartregister.chw.util;

import android.util.Log;

public class ServiceContent implements BaseService {
    private String serviceName;

    //TODO update actual implementation from child code
    public void setType(String type) {
        //String serviceDate;
        Log.v("ServiceContent",type);
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
