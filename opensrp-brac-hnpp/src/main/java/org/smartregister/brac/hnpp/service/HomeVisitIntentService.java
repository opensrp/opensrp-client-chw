package org.smartregister.brac.hnpp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.repository.AllSharedPreferences;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class HomeVisitIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private VisitRepository visitRepository;

    public HomeVisitIntentService() {
        super("HomeVisitService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        visitRepository = AncLibrary.getInstance().visitRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            processVisits();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Process all the visit older than 24 hours
     *
     * @throws Exception
     */
    protected void processVisits() throws Exception {
        processVisits(visitRepository, null);
    }
    public static void processVisits(VisitRepository visitRepository,String baseEntityID) throws Exception {
        Calendar calendar = Calendar.getInstance();

        List<Visit> visits = StringUtils.isNotBlank(baseEntityID) ?
                visitRepository.getAllUnSynced(calendar.getTime().getTime(), baseEntityID) :
                visitRepository.getAllUnSynced(calendar.getTime().getTime());
        for (Visit v : visits) {
            if (!v.getProcessed()) {

                // persist to db
                Event baseEvent = new Gson().fromJson(v.getPreProcessedJson(), Event.class);
                AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();
                NCUtils.addEvent(allSharedPreferences, baseEvent);

                // process details

                visitRepository.completeProcessing(v.getVisitId());
            }
        }

        // process after all events are saved
        NCUtils.startClientProcessing();

        // process vaccines and services
        Context context = AncLibrary.getInstance().context().applicationContext();
        context.startService(new Intent(context, VaccineIntentService.class));
        context.startService(new Intent(context, RecurringIntentService.class));
    }
}
