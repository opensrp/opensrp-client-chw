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

                String title1 = HnppConstants.visitEventTypeMapping.get(HnppConstants.HOME_VISIT_FORMS.ANC1_FORM);
                String title2 = HnppConstants.visitEventTypeMapping.get(HnppConstants.HOME_VISIT_FORMS.GENERAL_DISEASE);
                String title3 = HnppConstants.visitEventTypeMapping.get(HnppConstants.HOME_VISIT_FORMS.PREGNANCY_HISTORY);

                BaseAncHomeVisitAction ANC1_FORM = new BaseAncHomeVisitAction.Builder(context,title1 )
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.ANC1_FORM)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                BaseAncHomeVisitAction GENERAL_DISEASE = new BaseAncHomeVisitAction.Builder(context,title2 )
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.GENERAL_DISEASE)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                BaseAncHomeVisitAction PREGNANCY_HISTORY = new BaseAncHomeVisitAction.Builder(context, title3)
                        .withOptional(false)
                        .withFormName(HnppConstants.HOME_VISIT_FORMS.PREGNANCY_HISTORY)
                        .withHelper(new HnppHomeVisitActionHelper())
                        .build();

                actionList.put(title3, PREGNANCY_HISTORY);
                actionList.put(title2, GENERAL_DISEASE);
                actionList.put(title1, ANC1_FORM);




            } catch (BaseAncHomeVisitAction.ValidationException e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }


}
