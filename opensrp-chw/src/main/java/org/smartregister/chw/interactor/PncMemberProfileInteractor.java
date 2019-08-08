package org.smartregister.chw.interactor;

import android.content.Context;
import android.util.Pair;

import org.ei.drishti.dto.AlertStatus;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.pnc.interactor.BasePncMemberProfileInteractor;
import org.smartregister.chw.util.HomeVisitUtil;
import org.smartregister.chw.util.VisitSummary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static org.smartregister.chw.anc.AncLibrary.getInstance;

public class PncMemberProfileInteractor extends BasePncMemberProfileInteractor {
    private Context context;

    public PncMemberProfileInteractor(Context context) {
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

    public void updateChild(final Pair<Client, Event> pair, final String jsonString, final ChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new ChildProfileInteractor().saveRegistration(pair, jsonString, true, callBack);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public VisitSummary visitSummary(CommonPersonObjectClient pc) {
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(org.smartregister.chw.util.Constants.RULE_FILE.ANC_HOME_VISIT);
        String dayPnc = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DELIVERY_DATE, true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String baseEntityID = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
        Date deliveryDate = null;
        Date lastVisitDate = null;
        Date lastNotVisitDate = null;
        try {
            deliveryDate = sdf.parse(dayPnc);
        } catch (ParseException e) {
            Timber.e(e);
        }

        Visit lastVisit = getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null)
            lastVisitDate = lastVisit.getDate();

        Visit lastNotVisit = getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT_NOT_DONE);
        if (lastNotVisit != null)
            lastNotVisitDate = lastNotVisit.getDate();

        return HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, lastNotVisitDate, deliveryDate);
    }
}
