package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.ArrayList;
import java.util.List;

public class MalariaUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        return new ArrayList<>();
    }
}
