package org.smartregister.chw.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.job.BaseJob;

@Implements(BaseJob.class)
public class BaseJobShadow {

    @Implementation
    public static void scheduleJobImmediately(String jobTag) {
        // DO NOTHING
    }
}
