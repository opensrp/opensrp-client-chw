package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.pnc.interactor.BasePncMedicalHistoryInteractor;
import org.smartregister.chw.util.Constants;

import java.util.List;

public class PncMedicalHistoryActivityInteractor extends BasePncMedicalHistoryInteractor {

    @Override
    public void getMemberHistory(final String memberID, final Context context, final BaseAncMedicalHistoryContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {

            List<Visit> visits = VisitUtils.getVisits(memberID, Constants.EventType.PNC_HOME_VISIT);

            List<PncBaby> children = PersonDao.getMothersPNCBabies(memberID);
            for (PncBaby pncBaby : children) {
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.DANGER_SIGNS_BABY));
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.PNC_HEALTH_FACILITY_VISIT));
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.VACCINE_CARD_RECEIVED));
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.EXCLUSIVE_BREASTFEEDING));
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.KANGAROO_CARE));
                visits.addAll(VisitUtils.getVisits(pncBaby.getBaseEntityID(), Constants.EventType.OBSERVATIONS_AND_ILLNESS));
            }

            appExecutors.mainThread().execute(() -> callBack.onDataFetched(visits));
        };

        appExecutors.diskIO().execute(runnable);
    }
}
