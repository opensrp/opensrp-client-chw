package org.smartregister.chw.interactor;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncMemberProfileInteractor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AncMemberProfileInteractor extends BaseAncMemberProfileInteractor {

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
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.refreshLastVisit(lastVisitDate);
                        callback.refreshFamilyStatus(AlertStatus.normal);
                        callback.refreshUpComingServicesStatus("ANC Visit", AlertStatus.normal, new Date());
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Date getLastVisitDate(MemberObject memberObject) {
        Date lastVisitDate = null;
        if (StringUtils.isNotBlank(memberObject.getLastContactVisit())) {
            try {
                lastVisitDate = new SimpleDateFormat("dd-MM-yyyy").parse(memberObject.getLastContactVisit());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return lastVisitDate;
    }

}
