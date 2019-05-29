package org.smartregister.chw.interactor;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.interactor.BaseAncHomeVisitInteractor;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants;

import java.util.LinkedHashMap;

public class AncHomeVisitInteractor extends BaseAncHomeVisitInteractor {

    @Override
    public void calculateActions(final BaseAncHomeVisitContract.View view, String memberID, final BaseAncHomeVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

                try {
                    actionList.put("Danger Signs", new BaseAncHomeVisitAction("Danger Signs", "", false, null, Constants.JSON_FORM.ANC_HOME_VISIT.DANGER_SIGNS));
                    actionList.put("ANC Counseling", new BaseAncHomeVisitAction("ANC Counseling", "", false, null, Constants.JSON_FORM.ANC_HOME_VISIT.ANC_COUNSELING));

                    BaseAncHomeVisitFragment fragmentLLTIN = BaseAncHomeVisitFragment.getInstance(view, "Sleeping under a LLITN",
                            "Is the woman sleeping under a Long Lasting Insecticide-Treated Net (LLITN)?",
                            R.drawable.form_llitn,
                            BaseAncHomeVisitFragment.QuestionType.BOOLEAN
                    );
                    actionList.put("Sleeping under a LLITN", new BaseAncHomeVisitAction("Sleeping under a LLITN", "", false, fragmentLLTIN, null));

                    BaseAncHomeVisitFragment ancCardFragment = BaseAncHomeVisitFragment.getInstance(view, "ANC Card Received",
                            "ANC card received?",
                            org.smartregister.chw.opensrp_chw_anc.R.drawable.avatar_woman,
                            BaseAncHomeVisitFragment.QuestionType.BOOLEAN
                    );
                    actionList.put("ANC Card Received", new BaseAncHomeVisitAction("ANC Card Received", "", false, ancCardFragment, null));

                    actionList.put("ANC Health Facility Visit 1", new BaseAncHomeVisitAction("ANC Health Facility Visit 1", "", false, null, Constants.JSON_FORM.ANC_HOME_VISIT.HEALTH_FACILITY_VISIT));
                    actionList.put("TT Immunization 1", new BaseAncHomeVisitAction("TT Immunization 1", "", false, null, "anc"));
                    actionList.put("IPTp-SP dose 1", new BaseAncHomeVisitAction("IPTp-SP dose 1", "", false, null, "anc"));
                    actionList.put("Observation & Illness", new BaseAncHomeVisitAction("Observation & Illness", "", true, null, "anc"));
                } catch (BaseAncHomeVisitAction.ValidationException e) {
                    e.printStackTrace();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.preloadActions(actionList);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }
}
