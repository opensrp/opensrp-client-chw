package org.smartregister.chw.intent;

public abstract class DefaultChwPncCloseDateIntentFlv implements ChwPncCloseDateIntent.Flavor {
    @Override
    public int getNumberOfDays() {
        return 60;
    }
}
