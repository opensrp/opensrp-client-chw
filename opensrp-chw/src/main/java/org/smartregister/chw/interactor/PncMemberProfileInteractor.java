package org.smartregister.chw.interactor;

import android.content.Context;
import android.util.Pair;

import org.ei.drishti.dto.AlertStatus;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.pnc.interactor.BasePncMemberProfileInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.util.Date;

import static org.smartregister.chw.anc.AncLibrary.getInstance;

public class PncMemberProfileInteractor extends BasePncMemberProfileInteractor {
    private Context context;

    public PncMemberProfileInteractor(Context context) {
        this.context = context;
    }

    public PncMemberProfileInteractor() {
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
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.refreshLastVisit(lastVisitDate);
                        callback.refreshFamilyStatus(AlertStatus.normal);
                        callback.refreshUpComingServicesStatus(context.getString(R.string.pnc_visit), AlertStatus.normal, new Date());
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Date getLastVisitDate(MemberObject memberObject) {
        Date lastVisitDate = null;
        Visit lastVisit = getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return lastVisitDate;
    }

    @Override
    public void updateChild(final Pair<Client, Event> pair, final String jsonString) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        new ChildProfileInteractor().saveRegistration(pair, jsonString, true, null);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
