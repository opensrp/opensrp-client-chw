package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;

import java.text.MessageFormat;
import java.util.LinkedHashMap;

public class AncHomeVisitInteractorFlv implements AncHomeVisitInteractor.Flavor {

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, String memberID, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

        Context context = view.getContext();

        actionList.put(context.getString(R.string.anc_home_visit_danger_signs), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_danger_signs), "", false, null,
                ANC_HOME_VISIT.DANGER_SIGNS));

        actionList.put(context.getString(R.string.anc_home_visit_counseling), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counseling), "", false, null,
                ANC_HOME_VISIT.ANC_COUNSELING));


        actionList.put(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_sleeping_under_llitn_net), "", false, BaseAncHomeVisitFragment.getInstance(view, ANC_HOME_VISIT.SLEEPING_UNDER_LLITN, null),
                null));

        /*
        BaseAncHomeVisitFragment ancCardFragment = BaseAncHomeVisitFragment.getInstance(view, context.getString(R.string.anc_home_visit_anc_card_received),
                "ANC card received?",
                org.smartregister.chw.opensrp_chw_anc.R.drawable.avatar_woman,
                BaseAncHomeVisitFragment.QuestionType.BOOLEAN
        );
        actionList.put(context.getString(R.string.anc_home_visit_anc_card_received), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_anc_card_received), "", false, ancCardFragment, null));
*/
        String visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), 1);
        actionList.put(visit, new BaseAncHomeVisitAction(visit, "", false, null, ANC_HOME_VISIT.HEALTH_FACILITY_VISIT));

        actionList.put("TT 1 Immunization", new BaseAncHomeVisitAction("TT 1 Immunization", "", false, null, "anc"));
        actionList.put("IPTp-SP dose 1", new BaseAncHomeVisitAction("IPTp-SP dose 1", "", false, null, "anc"));
        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));

        return actionList;
    }

}
