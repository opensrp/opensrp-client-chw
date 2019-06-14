package org.smartregister.chw.interactor;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;

import java.text.MessageFormat;
import java.util.LinkedHashMap;

import timber.log.Timber;

import static org.smartregister.chw.util.JsonFormUtils.getValue;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {
    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        evaluateDangerSigns(actionList, context);

        evaluateHealthFacilityVisit(actionList, context);

        evaluateFamilyPlanning(actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_nutrition_status), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_nutrition_status), "", false, null,
                ANC_HOME_VISIT.NUTRITION_STATUS));

        actionList.put(context.getString(R.string.anc_home_visit_counselling_task), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counselling_task), "", false, null,
                ANC_HOME_VISIT.COUNSELLING));

        evaluateMalaria(actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));

        actionList.put(context.getString(R.string.anc_home_visit_remarks_and_comments), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_remarks_and_comments), "", true, null,
                ANC_HOME_VISIT.REMARKS_AND_COMMENTS));

        return actionList;
    }

    private void evaluateDangerSigns(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_danger_signs), "", false, null,
                ANC_HOME_VISIT.DANGER_SIGNS);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "danger_signs_counseling");

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PENDING;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_danger_signs), ba);
    }

    private void evaluateHealthFacilityVisit(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(visit, "", false, null, ANC_HOME_VISIT.HEALTH_FACILITY_VISIT);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_hf_visit");

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else if (value.equalsIgnoreCase("No")) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PENDING;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(visit, ba);
    }

    private void evaluateFamilyPlanning(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_family_planning), "", false, null,
                ANC_HOME_VISIT.FAMILY_PLANNING);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "fam_planning");

                        if (value.equalsIgnoreCase("Yes")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_family_planning), ba);
    }

    private void evaluateMalaria(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_malaria_prevention), "", false, null,
                ANC_HOME_VISIT.MALARIA);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value1 = getValue(jsonObject, "fam_llin");
                        String value2 = getValue(jsonObject, "llin_2days");
                        String value3 = getValue(jsonObject, "llin_condition");

                        if (value1.equalsIgnoreCase("Yes") && value2.equalsIgnoreCase("Yes") && value3.equalsIgnoreCase("Okay")) {
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } else {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(context.getString(R.string.anc_home_visit_malaria_prevention), ba);
    }
}
