package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.interactor.CoreChildUpcomingServiceInteractor;

import java.util.List;

public class ChildUpcomingServiceInteractor extends CoreChildUpcomingServiceInteractor {

    public final List<BaseUpcomingService> getUpcomingServices(final MemberObject memberObject, final Context ctx){
        return getMemberServices(ctx, memberObject);
    }

}
