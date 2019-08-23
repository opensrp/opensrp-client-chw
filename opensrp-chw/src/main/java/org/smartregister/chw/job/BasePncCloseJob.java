<<<<<<< HEAD:opensrp-chw/src/main/java/org/smartregister/chw/job/BasePncCloseJob.java
package org.smartregister.chw.job;
=======
package org.smartregister.chw.core.job;
>>>>>>> 6e7397a241ca09e14aa29b28b6d41020877e5d1f:opensrp-chw-core/src/main/java/org/smartregister/chw/core/job/BasePncCloseJob.java

import android.content.Intent;
import android.support.annotation.NonNull;

<<<<<<< HEAD:opensrp-chw/src/main/java/org/smartregister/chw/job/BasePncCloseJob.java
import org.smartregister.chw.core.job.CoreBasePncCloseJob;
import org.smartregister.chw.intent.ChwPncCloseDateIntent;
=======
import org.smartregister.chw.core.intent.ChwPncCloseDateIntent;
>>>>>>> 6e7397a241ca09e14aa29b28b6d41020877e5d1f:opensrp-chw-core/src/main/java/org/smartregister/chw/core/job/BasePncCloseJob.java
import org.smartregister.family.util.Constants;

import timber.log.Timber;

public class BasePncCloseJob extends CoreBasePncCloseJob {

    public static final String TAG = "BasePncCloseJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), ChwPncCloseDateIntent.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
