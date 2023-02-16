package org.smartregister.chw.fragment;

import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.core.fragment.CoreCdpReceiveMsdRegisterFragment;
import org.smartregister.family.util.JsonFormUtils;

public class CdpReceiveFromOrganizationsRegisterFragment extends CoreCdpReceiveMsdRegisterFragment {
    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(requireActivity(), form.toString());
        requireActivity().startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
