package org.smartregister.brac.hnpp.interactor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.interactor.FamilyProfileInteractor;

public class HnppFamilyProfileInteractor extends CoreFamilyProfileInteractor {
    @Override
    public void getNextUniqueId(Triple<String, String, String> triple, FamilyProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                final String entityId = "21a0cc6d-0ea0-4c80-bc29-9ae79981b248";
                appExecutors.mainThread().execute(new Runnable() {
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }

                    }
                });
            }
        };
        this.appExecutors.diskIO().execute(runnable);
    }
}
