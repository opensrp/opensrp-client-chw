package org.smartregister.chw.interactor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpAlertObject;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.util.FormUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.fields;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class FpFollowUpVisitInteractorFlv extends DefaultFpFollowUpVisitInteractorFlv {
    protected LinkedHashMap<String, BaseAncHomeVisitAction> actionList;
    protected Context context;
    protected Map<String, List<VisitDetail>> details = null;
    protected List<PncBaby> children;
    protected MemberObject memberObject;
    protected BaseAncHomeVisitContract.View view;
    protected Boolean editMode = false;
    protected String familyPlanningMethod;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        editMode = view.getEditMode();
        this.view = view;
        List<FpAlertObject> memberDetails = FpDao.getFpDetails(memberObject.getBaseEntityId());
        if (memberDetails.size() > 0) {
            for (FpAlertObject detail : memberDetails) {
                familyPlanningMethod = detail.getFpMethod();
            }
        }
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EVENT_TYPE.FP_FOLLOW_UP_VISIT);
            if (lastVisit != null) {
                details = Collections.unmodifiableMap(VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId())));
            }
        }
        try {
            Constants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
            evaluateSideEffects();
            evaluateCounselling();
            evaluateResupply();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
        return actionList;
    }

    private void evaluateSideEffects() throws Exception {

        Map<String, List<VisitDetail>> details = null;
        if (editMode) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EVENT_TYPE.FP_FOLLOW_UP_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        JSONObject jsonObject = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupSideEffects());
        injectFamilyPlaningMethod(jsonObject);
        // jsonObject

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.side_effects))
                .withOptional(false)
                .withDetails(details)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withHelper(new SideEffectsHelper())
                .withFormName(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupSideEffects())
                .withJsonPayload(jsonObject.toString())
                .build();

        actionList.put( context.getString(R.string.side_effects), action);
    }

    private void evaluateCounselling() throws Exception {

        Map<String, List<VisitDetail>> details = null;
        if (editMode) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EVENT_TYPE.FP_FOLLOW_UP_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

       // JSONObject jsonObject = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupSideEffects());
      //  injectFamilyPlaningMethod(jsonObject);
        // jsonObject

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context,  context.getString(R.string.counseling))
                .withOptional(false)
                .withDetails(details)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withHelper(new SideEffectsHelper())
                .withFormName(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupCounsel())
               // .withJsonPayload(jsonObject.toString())
                .build();

        actionList.put(context.getString(R.string.counseling), action);
    }

    private void evaluateResupply() throws Exception {

        Map<String, List<VisitDetail>> details = null;
        if (editMode) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), FamilyPlanningConstants.EVENT_TYPE.FP_FOLLOW_UP_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        JSONObject jsonObject = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupResupply());
        injectFamilyPlaningMethod(jsonObject);
        // jsonObject

        BaseAncHomeVisitAction action = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.resupply,familyPlanningMethod))
                .withOptional(false)
                .withDetails(details)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withHelper(new SideEffectsHelper())
                .withFormName(Constants.JSON_FORM.FP_FOLLOW_UP_VISIT.getFamilyPlanningFollowupResupply())
               .withJsonPayload(jsonObject.toString())
                .build();

        actionList.put(context.getString(R.string.resupply,familyPlanningMethod), action);
    }

    public JSONObject injectFamilyPlaningMethod(JSONObject form) throws Exception {
        if (form == null) {
            return null;
        } else {
            JSONArray field = fields(form);
            JSONObject datePass = getFieldJSONObject(field, "fp_method");
            datePass.put("value", familyPlanningMethod);
            return form;
        }
    }

    private class SideEffectsHelper extends HomeVisitActionHelper {
        private String muac;

        @Override
        public void onPayloadReceived(String jsonPayload) {
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                if (familyPlanningMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_COC)) {
                    // key = JsonFormUtils.getValue(jsonObject, "muac");
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        @Override
        public String evaluateSubTitle() {
            if (StringUtils.isBlank(muac)) {
                return null;
            }

            String value = "";
            if ("chk_green".equalsIgnoreCase(muac)) {
                value = context.getString(R.string.muac_choice_1);
            } else if ("chk_yellow".equalsIgnoreCase(muac)) {
                value = context.getString(R.string.muac_choice_2);
            } else if ("chk_red".equalsIgnoreCase(muac)) {
                value = context.getString(R.string.muac_choice_3);
            }
            return value;
        }

        @Override
        public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
            if (StringUtils.isBlank(muac)) {
                return BaseAncHomeVisitAction.Status.PENDING;
            }

            if ("chk_green".equalsIgnoreCase(muac)) {
                return BaseAncHomeVisitAction.Status.COMPLETED;
            }

            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }


}
