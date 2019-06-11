package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
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

        String hf_visit = MessageFormat.format(context.getString(R.string.anc_home_visit_facility_visit), 1);
        actionList.put(hf_visit, new BaseAncHomeVisitAction(hf_visit, "", false, null,
                ANC_HOME_VISIT.HEALTH_FACILITY_VISIT));

        actionList.put(context.getString(R.string.anc_home_visit_family_planning), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_family_planning), "", false, null,
                ANC_HOME_VISIT.FAMILY_PLANNING));

        actionList.put(context.getString(R.string.anc_home_visit_nutrition_status), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_nutrition_status), "", false, null,
                ANC_HOME_VISIT.NUTRITION_STATUS));

        actionList.put(context.getString(R.string.anc_home_visit_counselling_task), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_counselling_task), "", false, null,
                ANC_HOME_VISIT.COUNSELLING));

        actionList.put(context.getString(R.string.anc_home_visit_malaria_prevention), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_malaria_prevention), "", false, null,
                ANC_HOME_VISIT.MALARIA));

        actionList.put(context.getString(R.string.anc_home_visit_observations_n_illnes), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_observations_n_illnes), "", true, null,
                ANC_HOME_VISIT.OBSERVATION_AND_ILLNESS));

        actionList.put(context.getString(R.string.anc_home_visit_remarks_and_comments), new BaseAncHomeVisitAction(context.getString(R.string.anc_home_visit_remarks_and_comments), "", true, null,
                ANC_HOME_VISIT.REMARKS_AND_COMMENTS));

        return actionList;
    }
}
