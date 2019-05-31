package org.smartregister.chw.interactor;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;

import timber.log.Timber;

import static org.smartregister.chw.util.JsonFormUtils.getValue;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, String memberID, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        evaluateDangerSigns(actionList, context);

        evaluateANCCounseling(actionList, context);

        evaluateSleepingUnderLLITN(view, actionList, context);

        evaluateANCCard(view, actionList, context);

        evaluateHealthFacilityVisit(actionList, context);

        evaluateImmunization(view, actionList, context);

        evaluateIPTP(view, actionList, context);

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));

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

    private void evaluateANCCounseling(LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counseling), "", false, null,
                ANC_HOME_VISIT.ANC_COUNSELING);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value1 = getValue(jsonObject, "anc_counseling");
                        String value2 = getValue(jsonObject, "birth_hf_counseling");
                        String value3 = getValue(jsonObject, "nutrition_counseling");

                        if (value1.equalsIgnoreCase("Yes") && value2.equalsIgnoreCase("Yes") && value3.equalsIgnoreCase("Yes")) {
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

        actionList.put(context.getString(R.string.anc_home_visit_counseling), ba);
    }

    private void evaluateSleepingUnderLLITN(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.SLEEPING_UNDER_LLITN, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "sleeping_llitn");

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

        actionList.put(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), ba);

    }

    private void evaluateANCCard(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_anc_card_received), "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.ANC_CARD_RECEIVED, null),
                null);

        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, "anc_card");

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

        actionList.put(context.getString(R.string.anc_home_visit_anc_card_received), ba);
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

    private void evaluateImmunization(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String immunization = MessageFormat.format(context.getString(R.string.anc_home_visit_tt_immunization), 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(immunization, "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.TT_IMMUNIZATION, null),
                null);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("tt{0}_date", 1));

                        try {
                            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(immunization, ba);
    }

    private void evaluateIPTP(BaseAncHomeVisitContract.View view, LinkedHashMap<String, BaseAncHomeVisitAction> actionList, Context context) throws BaseAncHomeVisitAction.ValidationException {

        String iptp = MessageFormat.format(context.getString(R.string.anc_home_visit_iptp_sp), 1);
        final BaseAncHomeVisitAction ba = new BaseAncHomeVisitAction(iptp, "", false,
                BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.IPTP_SP, null),
                null);
        ba.setAncHomeVisitActionHelper(new BaseAncHomeVisitAction.AncHomeVisitActionHelper() {
            @Override
            public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
                if (ba.getJsonPayload() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(ba.getJsonPayload());

                        String value = getValue(jsonObject, MessageFormat.format("iptp{0}_date", 1));

                        try {
                            new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(value);
                            return BaseAncHomeVisitAction.Status.COMPLETED;
                        } catch (Exception e) {
                            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
                        }

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return ba.computedStatus();
            }
        });

        actionList.put(iptp, ba);
    }

}
