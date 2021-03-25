package org.smartregister.chw.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.chw.dao.EventDao;

public class CloseFamilyService extends IntentService {

    public CloseFamilyService() {
        super("CloseFamilyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        EventDao.closeReopenedClients();
    }
}
