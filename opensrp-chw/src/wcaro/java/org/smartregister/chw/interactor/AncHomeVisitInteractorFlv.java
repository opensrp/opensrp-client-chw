package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;

import java.util.LinkedHashMap;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, String memberID, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        actionList.put(context.getString(R.string.anc_home_visit_danger_signs), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_danger_signs), "", false, null,
                ANC_HOME_VISIT.DANGER_SIGNS));

        actionList.put("ANC Counseling", new BaseAncHomeVisitAction("ANC Counseling", "", false, null, ANC_HOME_VISIT.ANC_COUNSELING));

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

        actionList.put("ANC Health Facility Visit 1", new BaseAncHomeVisitAction("ANC Health Facility Visit 1", "", false, null, ANC_HOME_VISIT.HEALTH_FACILITY_VISIT));
        actionList.put("TT Immunization 1", new BaseAncHomeVisitAction("TT Immunization 1", "", false, null, "anc"));
        actionList.put("IPTp-SP dose 1", new BaseAncHomeVisitAction("IPTp-SP dose 1", "", false, null, "anc"));
        actionList.put("Observation & Illness", new BaseAncHomeVisitAction("Observation & Illness", "", true, null, "anc"));

        return actionList;
    }

}
