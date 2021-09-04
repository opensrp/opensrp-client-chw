package org.smartregister.chw.fragment;

import com.vijay.jsonwizard.utils.FormUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.smartregister.chw.activity.HivIndexContactProfileActivity;
import org.smartregister.chw.activity.HivIndexContactsContactsRegisterActivity;
import org.smartregister.chw.core.fragment.CoreHivIndexContactsRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hiv.dao.HivIndexDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.chw.model.HivIndexContactsRegisterFragmentModel;
import org.smartregister.chw.presenter.HivIndexContactsContactsRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Objects;

import timber.log.Timber;

public class HivIndexContactsRegisterFragment extends CoreHivIndexContactsRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((HivIndexContactsContactsRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new HivIndexContactsContactsRegisterFragmentPresenter(this, new HivIndexContactsRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null) {
            HivIndexContactProfileActivity.startHivIndexContactProfileActivity(getActivity(), Objects.requireNonNull(HivIndexDao.getMember(client.getCaseId())));
        }
    }


    @Override
    protected void openFollowUpVisit(@Nullable HivMemberObject hivMemberObject) {
        if (getActivity() != null) {
            try {
                HivIndexContactsContactsRegisterActivity.startHIVFormActivity(getActivity(), hivMemberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getHivRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(getActivity(), CoreConstants.JSON_FORM.getHivFollowupVisit()).toString());
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }
}


