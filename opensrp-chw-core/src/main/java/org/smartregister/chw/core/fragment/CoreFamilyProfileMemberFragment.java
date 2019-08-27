package org.smartregister.chw.core.fragment;

import android.content.Intent;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.presenter.BaseFamilyProfileMemberPresenter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;


public abstract class CoreFamilyProfileMemberFragment extends BaseFamilyProfileMemberFragment {

    @Override
    public abstract void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver);

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        int i = view.getId();
        if (i == R.id.patient_column) {
            if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                goToProfileActivity(view);
            }
        } else if (i == R.id.next_arrow && view.getTag() != null &&
                view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
            goToProfileActivity(view);
        }
    }

    public void goToProfileActivity(android.view.View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (CoreConstants.TABLE_NAME.FAMILY_MEMBER.equals(entityType)) {
                goToOtherMemberProfileActivity(commonPersonObjectClient);
            } else {
                goToChildProfileActivity(commonPersonObjectClient);
            }
        }
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient) {
        Intent intent = new Intent(getActivity(), getFamilyOtherMemberProfileActivityClass());
        intent.putExtras(getArguments());
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, ((BaseFamilyProfileMemberPresenter) presenter).getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, ((BaseFamilyProfileMemberPresenter) presenter).getPrimaryCaregiver());
        startActivity(intent);
    }

    public void goToChildProfileActivity(CommonPersonObjectClient patient) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = CoreChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(getActivity(), getAboveFiveChildProfileActivityClass());
        } else {
            intent = new Intent(getActivity(), getChildProfileActivityClass());
        }
        if (getArguments() != null) {
            intent.putExtras(getArguments());
        }
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, true);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        startActivity(intent);
    }

    protected abstract Class<?> getFamilyOtherMemberProfileActivityClass();

    protected abstract Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass();

    protected abstract Class<? extends CoreChildProfileActivity> getChildProfileActivityClass();

    @Override
    protected abstract void initializePresenter();

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.v("setAdvancedSearchFormData");
    }

}
