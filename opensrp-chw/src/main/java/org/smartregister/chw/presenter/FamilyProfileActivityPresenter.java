package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.FamilyProfileActivityFragment;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.List;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {
    private WashCheckModel washCheckModel;

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s in (%s) ", DBConstants.KEY.RELATIONAL_ID, familyBaseEntityId, ChildDBConstants.KEY.VISIT_TYPE, getEventsInCSV());
    }

    private String getEventsInCSV() {
        List<String> events = new ArrayList<>();
        events.add(CoreConstants.EventType.ANC_HOME_VISIT);
        events.add(CoreConstants.EventType.PNC_HOME_VISIT);
        events.add(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE);
        events.add(CoreConstants.EventType.CHILD_HOME_VISIT);
        events.add(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);

        StringBuilder res = new StringBuilder();
        for (String s : events) {
            if (res.length() > 0)
                res.append(",");

            res.append("'").append(s).append("'");
        }

        return res.toString();
    }

    @Override
    public String getDefaultSortQuery() {
        return VisitRepository.VISIT_TABLE + "." + ChildDBConstants.KEY.VISIT_DATE + " DESC";
    }

    public void fetchLastWashCheck() {
        if (getView() instanceof FamilyProfileActivityFragment) {
            FamilyProfileActivityFragment familyProfileActivityFragment = (FamilyProfileActivityFragment) getView();
            familyProfileActivityFragment.updateWashCheckBar(washCheckModel.getAllWashCheckList());
        }
    }
}
