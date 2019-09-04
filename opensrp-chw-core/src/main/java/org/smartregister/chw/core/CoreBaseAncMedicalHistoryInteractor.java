package org.smartregister.chw.core;

import android.content.Context;

import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.interactor.BaseAncMedicalHistoryInteractor;
import org.smartregister.chw.anc.util.VisitUtils;

import java.util.ArrayList;
import java.util.List;

public class CoreBaseAncMedicalHistoryInteractor extends BaseAncMedicalHistoryInteractor {

    @Override
    public void getMemberHistory(final String memberID, final Context context, final BaseAncMedicalHistoryContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {

            List<Visit> visits = VisitUtils.getVisits(memberID);
            final List<Visit> all_visits = new ArrayList<>(visits);

            for(Visit visit : visits){
                List<Visit> child_visits = getChildVisits(visit.getVisitId());
                all_visits.addAll(child_visits);
            }
            appExecutors.mainThread().execute(() -> callBack.onDataFetched(all_visits));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private List<Visit> getChildVisits(String parentVisitID){
        List<Visit> res = new ArrayList<>();

        List<Visit> visit_kids = AncLibrary.getInstance().visitRepository().getChildEvents(parentVisitID);

        if (visit_kids != null && !visit_kids.isEmpty()) {
            int x = 0;
            while (x < visit_kids.size()) {
                Visit v = visit_kids.get(x);
                List<VisitDetail> visitDetails = AncLibrary.getInstance().visitDetailsRepository().getVisits(v.getVisitId());
                visit_kids.get(x).setVisitDetails(VisitUtils.getVisitGroups(visitDetails));
                x++;
            }

            res.addAll(visit_kids);
        }

        return res;
    }
}
