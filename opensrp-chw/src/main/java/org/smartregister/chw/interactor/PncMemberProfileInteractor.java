package org.smartregister.chw.interactor;

import android.content.Context;
import android.util.Pair;

import org.ei.drishti.dto.AlertStatus;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.interactor.BasePncMemberProfileInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class PncMemberProfileInteractor extends BasePncMemberProfileInteractor {
    private Context context;
    private Date lastVisitDate;
    private Date deliveryDate;

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
                appExecutors.mainThread().execute(() -> {
                    callback.refreshLastVisit(lastVisitDate);
                    callback.refreshFamilyStatus(AlertStatus.normal);
                    callback.refreshUpComingServicesStatus(context.getString(R.string.pnc_visit), AlertStatus.normal, new Date());
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private Date getLastVisitDate(MemberObject memberObject) {
        Date lastVisitDate = null;
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return lastVisitDate;
    }

    public void updateChild(final Pair<Client, Event> pair, final String jsonString, final CoreChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            try {
                new ChildProfileInteractor().saveRegistration(pair, jsonString, true, callBack);
            } catch (Exception e) {
                Timber.e(e);
            }
        });
        appExecutors.diskIO().execute(runnable);
    }

    public PncVisitAlertRule getVisitSummary(String motherBaseID) {
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(org.smartregister.chw.util.Constants.RULE_FILE.PNC_HOME_VISIT);
        Date lastVisitDate = getLastDateVisit(motherBaseID);
        Date deliveryDate = getDeliveryDate(motherBaseID);
        return HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, deliveryDate);
    }

    private Date getLastDateVisit(String motherBaseID) {
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(motherBaseID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
            return lastVisitDate;
        } else {
            return lastVisitDate = getDeliveryDate(motherBaseID);
        }

    }

    private Date getDeliveryDate(String motherBaseID) {
        String deliveryDateString = PncLibrary.getInstance().profileRepository().getDeliveryDate(motherBaseID);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            deliveryDate = sdf.parse(deliveryDateString);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return deliveryDate;
    }

    public PncVisitAlertRule visitSummary(CommonPersonObjectClient pc) {
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.PNC_HOME_VISIT);
        String dayPnc = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DELIVERY_DATE, true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String baseEntityID = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
        Date deliveryDate = null;
        Date lastVisitDate = null;
        try {
            deliveryDate = sdf.parse(dayPnc);
        } catch (ParseException e) {
            Timber.e(e);
        }

        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, deliveryDate);
    }

}
