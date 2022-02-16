package org.smartregister.chw.fragment;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.ACTION.START_BIRTH_CERTIFICATION_UPDATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_REGISTRATION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_REG_TYPE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.INFORMANT_REASON;
import static org.smartregister.chw.util.ChildDBConstants.KEY.SYSTEM_BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERT;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_CERT_NUM;
import static org.smartregister.chw.util.CrvsConstants.BIRTH_NOTIFICATION;
import static org.smartregister.chw.util.CrvsConstants.DOB;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.BirthCertificationRegisterActivity;
import org.smartregister.chw.core.fragment.CoreCertificationRegisterFragment;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.BirthCertificationRegisterFragmentModel;
import org.smartregister.chw.presenter.BirthCertificationRegisterFragmentPresenter;
import org.smartregister.chw.provider.BirthCertificationRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Set;

public class BirthCertificationRegisterFragment extends CoreCertificationRegisterFragment {


    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        BirthCertificationRegisterProvider provider = new BirthCertificationRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new BirthCertificationRegisterFragmentPresenter(this, new BirthCertificationRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.birth_certification;
    }

    @Override
    public Intent getUpdateIntent(CommonPersonObjectClient client) {
        if (getActivity() == null || client == null)
            return null;

        Intent intent = new Intent(getActivity(), BirthCertificationRegisterActivity.class);
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.ACTION, START_BIRTH_CERTIFICATION_UPDATE);
        intent.putExtra(BASE_ENTITY_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true));
        intent.putExtra(CLIENT_TYPE, Utils.getValue(client.getColumnmaps(), CLIENT_TYPE, true));
        intent.putExtra(BIRTH_CERT, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT, true));
        intent.putExtra(BIRTH_REGISTRATION, Utils.getValue(client.getColumnmaps(), BIRTH_REGISTRATION, true));
        intent.putExtra(BIRTH_NOTIFICATION, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION, true));
        intent.putExtra(BIRTH_CERTIFICATE_ISSUE_DATE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE, true));
        intent.putExtra(BIRTH_CERT_NUM, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_NUMBER, true));
        intent.putExtra(SYSTEM_BIRTH_NOTIFICATION, Utils.getValue(client.getColumnmaps(), SYSTEM_BIRTH_NOTIFICATION, true));
        intent.putExtra(BIRTH_REG_TYPE, Utils.getValue(client.getColumnmaps(), BIRTH_REG_TYPE, true));
        intent.putExtra(INFORMANT_REASON, Utils.getValue(client.getColumnmaps(), INFORMANT_REASON, true));
        intent.putExtra(DOB, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

        return intent;
    }
}
