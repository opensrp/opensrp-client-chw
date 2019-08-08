package org.smartregister.chw.intent;

import android.app.IntentService;
import android.content.Intent;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.repository.PncCloseDateRepository;
import org.smartregister.chw.pnc.util.PncUtil;

import timber.log.Timber;

public class ChwPncCloseDateIntent extends IntentService {

    private Flavor flavor = new ChwPncCloseDateIntentFlv();

    public ChwPncCloseDateIntent() {
        super("ChwPncCloseDateIntent");
    }

    private PncCloseDateRepository repository;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        repository = PncLibrary.getInstance().getPncCloseDateRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            PncUtil.updatePregancyOutcome(flavor.getNumberOfDays(), repository);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    interface Flavor {
        int getNumberOfDays();
    }
}
