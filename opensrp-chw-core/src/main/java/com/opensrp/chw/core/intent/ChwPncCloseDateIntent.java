package com.opensrp.chw.core.intent;

import android.app.IntentService;
import android.content.Intent;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.repository.PncCloseDateRepository;
import org.smartregister.chw.pnc.util.PncUtil;

import timber.log.Timber;

public class ChwPncCloseDateIntent extends IntentService {
    private PncCloseDateRepository repository;

    private Flavor flavor;


    public ChwPncCloseDateIntent() {
        super("ChwPncCloseDateIntent");
    }

    public Flavor getFlavor() {
        return flavor;
    }

    public void setFlavor(Flavor flavor) {
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


    interface Flavor {
        int getNumberOfDays();
    }
}
