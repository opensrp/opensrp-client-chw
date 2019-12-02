package org.smartregister.brac.hnpp.sync;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.brac.hnpp.job.VisitLogServiceJob;
import org.smartregister.chw.core.sync.ChwClientProcessor;
import org.smartregister.domain.db.EventClient;

public class HnppClientProcessor extends ChwClientProcessor {
    public HnppClientProcessor(Context context) {
        super(context);
    }

    @Override
    protected void processAncHomeVisit(EventClient baseEvent, SQLiteDatabase database, String parentEventType) {
        super.processAncHomeVisit(baseEvent, database, parentEventType);
        //start log table job
        VisitLogServiceJob.scheduleJobImmediately(VisitLogServiceJob.TAG);
    }
}
