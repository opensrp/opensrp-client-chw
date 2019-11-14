package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.interactor.CoreChildUpcomingServiceInteractor;

import java.util.List;

public class ChildUpcomingServicesInteractor extends CoreChildUpcomingServiceInteractor {

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        return super.getMemberServices(context, memberObject);
    }
}
