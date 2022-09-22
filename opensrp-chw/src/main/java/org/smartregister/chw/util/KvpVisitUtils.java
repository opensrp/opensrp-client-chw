package org.smartregister.chw.util;

import android.content.Context;
import android.widget.Toast;

import org.smartregister.chw.kvp.KvpLibrary;
import org.smartregister.chw.kvp.domain.Visit;
import org.smartregister.chw.kvp.repository.VisitDetailsRepository;
import org.smartregister.chw.kvp.repository.VisitRepository;
import org.smartregister.chw.kvp.util.Constants;
import org.smartregister.chw.kvp.util.VisitUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class KvpVisitUtils extends VisitUtils {
    public static void processVisits(Context context) throws Exception {
        processVisits(KvpLibrary.getInstance().visitRepository(), KvpLibrary.getInstance().visitDetailsRepository(), context);
    }

    public static void processVisits(VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository, Context context) throws Exception {

        List<Visit> visits = visitRepository.getAllUnSynced();
        List<Visit> kvpFollowupVisits = new ArrayList<>();

        for (Visit v : visits) {

            if (v.getVisitType().equalsIgnoreCase(Constants.EVENT_TYPE.KVP_PrEP_FOLLOW_UP_VISIT)) {
                try {
                    kvpFollowupVisits.add(v);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }

        if (kvpFollowupVisits.size() > 0) {
            processVisits(kvpFollowupVisits, visitRepository, visitDetailsRepository);
            //TODO: Extract string resource and give a more descriptive text
            Toast.makeText(context, "VISIT SAVED AND PROCESSED", Toast.LENGTH_SHORT).show();
        }
    }
}
