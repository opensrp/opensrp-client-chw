package org.smartregister.chw.fragment;

import androidx.annotation.Nullable;

import org.smartregister.chw.activity.TbProfileActivity;
import org.smartregister.chw.activity.TbRegisterActivity;
import org.smartregister.chw.core.fragment.CoreTbRegisterFragment;
import org.smartregister.chw.model.TbRegisterFragmentModel;
import org.smartregister.chw.presenter.TbRegisterFragmentPresenter;
import org.smartregister.chw.tb.dao.TbDao;
import org.smartregister.chw.tb.domain.TbMemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Objects;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class TbRegisterFragment extends CoreTbRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((TbRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new TbRegisterFragmentPresenter(this, new TbRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null)
            TbProfileActivity.startTbProfileActivity(getActivity(), Objects.requireNonNull(TbDao.getMember(client.getCaseId())));
    }

    @Override
    protected void openFollowUpVisit(@Nullable TbMemberObject tbMemberObject) {
        if (getActivity() != null)
            TbRegisterActivity.startTbFormActivity(getActivity(), tbMemberObject.getBaseEntityId(),org.smartregister.chw.util.Constants.JSON_FORM.getTbFollowupVisit(),getFormUtils().getFormJsonFromRepositoryOrAssets(org.smartregister.chw.util.Constants.JSON_FORM.getTbFollowupVisit()).toString());
    }

}

