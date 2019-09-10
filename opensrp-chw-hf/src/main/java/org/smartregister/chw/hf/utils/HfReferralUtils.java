package org.smartregister.chw.hf.utils;

import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.repository.HfTaskRepository;
import org.smartregister.domain.Task;

public class HfReferralUtils extends CoreReferralUtils {

    public static Task getLatestClientReferralTask(String baseEntityId, String referralType) {

        return ((HfTaskRepository) HealthFacilityApplication.getInstance()
                .getTaskRepository()).getLatestTaskByEntityId(baseEntityId, referralType);

    }

}
