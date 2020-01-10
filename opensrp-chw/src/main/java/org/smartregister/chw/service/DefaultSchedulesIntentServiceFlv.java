package org.smartregister.chw.service;

public class DefaultSchedulesIntentServiceFlv implements SchedulesIntentService.Flavor {
    @Override
    public boolean hasFamilyPlanning() {
        return false;
    }
}
