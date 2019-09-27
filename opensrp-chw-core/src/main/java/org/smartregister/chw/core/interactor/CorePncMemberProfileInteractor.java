package org.smartregister.chw.core.interactor;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.pnc.interactor.BasePncMemberProfileInteractor;

public abstract class CorePncMemberProfileInteractor extends BasePncMemberProfileInteractor {

    @Override
    public MemberObject getMemberClient(String memberID) {
        // read all the member details from the database
        return PNCDao.getMember(memberID);
    }

}
