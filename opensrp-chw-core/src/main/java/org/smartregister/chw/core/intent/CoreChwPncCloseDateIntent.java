package org.smartregister.chw.core.intent;

import android.app.IntentService;
import android.content.Intent;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.repository.PncCloseDateRepository;
import org.smartregister.chw.pnc.util.PncUtil;

import timber.log.Timber;

public class CoreChwPncCloseDateIntent extends IntentService {
    private PncCloseDateRepository repository;

    private Flavor flavor;


    public CoreChwPncCloseDateIntent(Flavor flavor) {
        super("CoreChwPncCloseDateIntent");
        this.flavor = flavor;
    }

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

    public interface Flavor {
        int getNumberOfDays();
    }
}
