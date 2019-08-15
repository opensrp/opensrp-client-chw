package org.smartregister.chw.intent;

import com.opensrp.chw.core.intent.ChwPncCloseDateIntent;

public abstract class DefaultChwPncCloseDateIntentFlv implements ChwPncCloseDateIntent.Flavor {
    @Override
    public int getNumberOfDays() {
        return 60;
    }
}
