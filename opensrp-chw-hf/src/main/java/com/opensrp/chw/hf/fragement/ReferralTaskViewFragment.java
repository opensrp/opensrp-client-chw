package com.opensrp.chw.hf.fragement;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opensrp.hf.R;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;

public class ReferralTaskViewFragment extends DialogFragment {

    public static String DIALOG_TAG = "ReferralTaskViewFragment";

    private CommonPersonObjectClient personObjectClient;
    private Task task;

    public CommonPersonObjectClient getPersonObjectClient() {
        return personObjectClient;
    }

    public void setPersonObjectClient(CommonPersonObjectClient personObjectClient) {
        this.personObjectClient = personObjectClient;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public static ReferralTaskViewFragment newInstance() {
        return new ReferralTaskViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.referrals_tasks_view_layout, container, false);
    }
}
