package org.smartregister.chw.intent;

<<<<<<< HEAD
=======
import org.smartregister.chw.core.intent.ChwPncCloseDateIntent;
>>>>>>> 6e7397a241ca09e14aa29b28b6d41020877e5d1f

public abstract class DefaultChwPncCloseDateIntentFlv implements ChwPncCloseDateIntent.Flavor {
    @Override
    public int getNumberOfDays() {
        return 60;
    }
}
