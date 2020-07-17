package org.smartregister.chw.contract;

public interface LoginJobScheduler {
    void scheduleJobsPeriodically();

    void scheduleJobsImmediately();

    long getFlexValue(int value);
}
