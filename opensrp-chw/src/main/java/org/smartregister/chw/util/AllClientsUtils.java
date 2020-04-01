package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.activity.MalariaProfileActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.MalariaDao;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CLIENT;

public class AllClientsUtils {

    public static void goToAdultMemberProfile(Activity activity, CommonPersonObjectClient commonPersonObjectClient, Bundle bundle) {
        if (AncDao.isANCMember(commonPersonObjectClient.entityId())) {
            goToAncProfile(activity, commonPersonObjectClient, bundle);
        } else if (PNCDao.isPNCMember(commonPersonObjectClient.entityId())) {
            gotToPncProfile(activity, commonPersonObjectClient, bundle);
        } else if (MalariaDao.isRegisteredForMalaria(commonPersonObjectClient.entityId())) {
            gotToMalariaProfile(activity, commonPersonObjectClient);
        } else if (FpDao.isRegisteredForFp(commonPersonObjectClient.entityId())) {
            goToFamilyPlanningProfile(activity, commonPersonObjectClient);
        } else {
            goToOtherMemberProfile(activity, commonPersonObjectClient, bundle,
                    bundle.getString(Constants.INTENT_KEY.FAMILY_HEAD),
                    bundle.getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER));
        }
    }

    public static void goToChildProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = CoreChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(activity, AboveFiveChildProfileActivity.class);
        } else {
            intent = new Intent(activity, ChildProfileActivity.class);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        activity.startActivity(intent);
    }

    public static void goToOtherMemberProfile(Activity activity, CommonPersonObjectClient patient,
                                              Bundle bundle, String familyHead, String primaryCaregiver) {
        Intent intent = new Intent(activity, FamilyOtherMemberProfileActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
        activity.startActivity(intent);
    }

    private static void gotToPncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.pncRegisterRepository().getPncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, PncMemberProfileActivity.class));
    }

    private static void goToAncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.ancRegisterRepository().getAncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, AncMemberProfileActivity.class));
    }

    private static void gotToMalariaProfile(Activity activity, CommonPersonObjectClient patient) {
        MalariaProfileActivity.startMalariaActivity(activity, new org.smartregister.chw.malaria.domain.MemberObject(patient), patient);
    }

    private static void goToFamilyPlanningProfile(Activity activity, CommonPersonObjectClient patient) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(activity, FpDao.getMember(patient.getCaseId()));
    }

    private static Intent initProfileActivityIntent(Activity activity, CommonPersonObjectClient patient, Bundle bundle, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, patient.entityId());
        intent.putExtra(CLIENT, patient);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.TITLE_VIEW_TEXT, R.string.return_to_all_client);
        return intent;
    }
}
