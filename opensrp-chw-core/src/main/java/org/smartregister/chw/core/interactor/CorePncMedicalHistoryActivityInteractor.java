package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.pnc.interactor.BasePncMedicalHistoryInteractor;

import java.util.ArrayList;
import java.util.List;

public abstract class CorePncMedicalHistoryActivityInteractor extends BasePncMedicalHistoryInteractor {

    @Override
    public void getMemberHistory(final String memberID, final Context context, final BaseAncMedicalHistoryContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {

            List<Visit> visits = new ArrayList<>();
            Visit visit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
            if (visit != null) {
                List<VisitDetail> detailList = VisitDao.getPNCMedicalHistory(memberID);
                visit.setVisitDetails(VisitUtils.getVisitGroups(detailList));
                visits.add(visit);
            }
            appExecutors.mainThread().execute(() -> callBack.onDataFetched(visits));
        };

        appExecutors.diskIO().execute(runnable);
    }
}
