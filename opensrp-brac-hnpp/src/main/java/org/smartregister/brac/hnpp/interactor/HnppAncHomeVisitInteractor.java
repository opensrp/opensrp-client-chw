package org.smartregister.brac.hnpp.interactor;

import android.content.Context;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.HnppHomeVisitActionHelper;
import org.smartregister.chw.anc.actionhelper.DangerSignsHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncHomeVisitInteractor;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import java.util.LinkedHashMap;
import java.util.Map;

public class HnppAncHomeVisitInteractor extends BaseAncHomeVisitInteractor {

    @Override
    public void calculateActions(final BaseAncHomeVisitContract.View view, final MemberObject memberObject, final BaseAncHomeVisitContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            final LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

            try {

                Context context = view.getContext();
                BaseAncHomeVisitAction danger_signs = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.anc_home_visit_danger_signs))
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.DANGER_SIGNS)
                        .withHelper(new DangerSignsHelper())
                        .build();

                BaseAncHomeVisitAction ANC1_FORM = new BaseAncHomeVisitAction.Builder(context, "গর্ভবতী পরিচর্যা")
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.ANC1_FORM)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                BaseAncHomeVisitAction GENERAL_DISEASE = new BaseAncHomeVisitAction.Builder(context, "শারীরিক সমস্যা")
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.GENERAL_DISEASE)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                BaseAncHomeVisitAction PREGNANCY_HISTORY = new BaseAncHomeVisitAction.Builder(context, "পূর্বের গর্ভের ইতিহাসঃ")
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.PREGNANCY_HISTORY)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                actionList.put("পূর্বের গর্ভের ইতিহাসঃ", PREGNANCY_HISTORY);
                actionList.put("শারীরিক সমস্যা", GENERAL_DISEASE);
//                actionList.put("গর্ভবতী পরিচর্যা", ANC1_FORM);

            } catch (BaseAncHomeVisitAction.ValidationException e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }


}
