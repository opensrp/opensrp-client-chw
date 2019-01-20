package org.smartgresiter.wcaro.listener;

import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

public interface UpdateServiceListener {
    void onUpdateServiceList(Map<String, ServiceWrapper> serviceWrapperMap);
}
