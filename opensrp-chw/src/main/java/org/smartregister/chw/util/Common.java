package org.smartregister.chw.util;

import android.support.annotation.NonNull;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

public class Common {
    @NonNull
    public static CommonPersonObjectClient clientForEdit(@NonNull String baseEntityId) {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);
        CommonPersonObject personObject = commonRepository.findByBaseEntityId(baseEntityId);
        CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());
        return client;
    }
}
