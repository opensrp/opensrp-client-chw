package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.ANCCardAction;
import org.smartregister.chw.actionhelper.ImmunizationActionHelper;
import org.smartregister.chw.actionhelper.ObservationAction;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.fragment.BaseHomeVisitImmunizationFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.VaccineScheduleUtil;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public abstract class DefaultChwChildHomeVisitInteractor implements ChwChildHomeVisitInteractor.Flavor {
    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;
    protected Context context;
    protected Map<String, List<VisitDetail>> details = null;
    protected MemberObject memberObject;
    protected BaseAncHomeVisitContract.View view;
    protected Date dob;


    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        try {
            this.dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDob());
        } catch (ParseException e) {
            Timber.e(e);
        }
        this.view = view;
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.CHILD_HOME_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        try {
            Constants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
            evaluateChildVaccineCard();
            //evaluateImmunization();
            evaluateBirthCertForm();
            evaluateExclusiveBreastFeeding();
            evaluateVitaminA();
            evaluateDeworming();
            evaluateMNP();
            evaluateMUAC();
            evaluateLLITN();
            evaluateECD();
            evaluateObsAndIllness();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
        return actionList;
    }


    protected void evaluateChildVaccineCard() throws Exception{
        BaseAncHomeVisitAction vaccine_card = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.vaccine_card_title))
                .withOptional(false)
                .withDetails(details)
                .withHelper(new ANCCardAction())
                .withDestinationFragment(BaseAncHomeVisitFragment.getInstance(view, Constants.JSON_FORM.CHILD_HOME_VISIT.getVaccineCard(), null, details, null))
                .build();

        actionList.put(context.getString(R.string.vaccine_card_title), vaccine_card);
    }

    protected void evaluateImmunization() throws Exception {
        List<VaccineGroup> groups = VaccineScheduleUtil.getVaccineGroups(ChwApplication.getInstance().getApplicationContext(), "child");
        int x = 0;

        for (VaccineGroup group : groups) {

            List<VaccineWrapper> wrappers = VaccineScheduleUtil.getChildDueVaccines(memberObject.getBaseEntityId(), dob, x);

            BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, group.name)
                    .withOptional(false)
                    .withDetails(details)
                    .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.DETACHED)
                    .withVaccineWrapper(wrappers)
                    .withDestinationFragment(BaseHomeVisitImmunizationFragment.getInstance(view, memberObject.getBaseEntityId(), dob, details, wrappers))
                    .withHelper(new ImmunizationActionHelper(context, wrappers))
                    .build();
            actionList.put(group.name, action);

            x++;
        }
    }

    protected void evaluateBirthCertForm() throws Exception {
    }

    protected void evaluateExclusiveBreastFeeding() throws Exception {
    }

    protected void evaluateVitaminA() throws Exception {
    }

    protected void evaluateDeworming() throws Exception {
    }

    protected void evaluateMNP() throws Exception {
    }

    protected void evaluateMUAC() throws Exception {
    }

    protected void evaluateLLITN() throws Exception {
    }

    protected void evaluateECD() throws Exception {
    }

    protected void evaluateObsAndIllness() throws Exception {
        BaseAncHomeVisitAction observation = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_observations_n_illnes))
                .withOptional(true)
                .withDetails(details)
                .withHelper(new ObservationAction())
                .withFormName(Constants.JSON_FORM.ANC_HOME_VISIT.getObservationAndIllness())
                .build();

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), observation);
    }
}
