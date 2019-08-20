package org.smartregister.chw.interactor;

import android.content.Context;

import org.ei.drishti.dto.AlertStatus;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.interactor.BaseAncMemberProfileInteractor;
import org.smartregister.chw.anc.util.Constants;

import java.util.Date;

import static org.smartregister.chw.anc.AncLibrary.getInstance;

public class AncMemberProfileInteractor extends BaseAncMemberProfileInteractor {
    private Context context;

    public AncMemberProfileInteractor(Context context) {
        this.context = context;
    }

    /**
     * Compute and process the lower profile info
     *
     * @param memberObject
     * @param callback
     */
    @Override
    public void refreshProfileInfo(final MemberObject memberObject, final BaseAncMemberProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {

            Date lastVisitDate = getLastVisitDate(memberObject);

            @Override
            public void run() {
                appExecutors.mainThread().execute(() -> {
                    callback.refreshLastVisit(lastVisitDate);
                    callback.refreshFamilyStatus(AlertStatus.normal);
                    callback.refreshUpComingServicesStatus(context.getString(R.string.anc_visit), AlertStatus.normal, new Date());
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Date getLastVisitDate(MemberObject memberObject) {
        Date lastVisitDate = null;
        Visit lastVisit = getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.ANC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return lastVisitDate;
    }

}
