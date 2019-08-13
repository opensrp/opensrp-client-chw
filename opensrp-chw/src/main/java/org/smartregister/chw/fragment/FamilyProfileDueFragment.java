package org.smartregister.chw.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.model.FamilyProfileDueModel;
import org.smartregister.chw.presenter.FamilyProfileDuePresenter;
import org.smartregister.chw.provider.ChwDueRegisterProvider;
import org.smartregister.chw.util.WashCheck;
import org.smartregister.chw.util.WashCheckFlv;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.util.FormUtils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.chw.util.Constants.INTENT_KEY.IS_COMES_FROM_FAMILY;
import static org.smartregister.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_WASH;
import static org.smartregister.family.util.Utils.metadata;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;

public class FamilyProfileDueFragment extends BaseFamilyProfileDueFragment {

    private int dueCount = 0;
    private View emptyView;
    private String familyName;
    private long dateFamilyCreated;
    private String familyBaseEntityId;
    private LinearLayout washCheckView;
    private Flavor flavorWashCheck = new WashCheckFlv();

    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new FamilyProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        familyName = getArguments().getString(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new FamilyProfileDuePresenter(this, new FamilyProfileDueModel(), null, familyBaseEntityId);
        //TODO need to pass this value as this value using at homevisit rule
        dateFamilyCreated = getArguments().getLong("");

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        ChwDueRegisterProvider chwDueRegisterProvider = new ChwDueRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler);
        this.clientAdapter = new FamilyRecyclerViewCustomAdapter(null, chwDueRegisterProvider, this.context().commonrepository(this.tablename), metadata().familyDueRegister.showPagination);
        this.clientAdapter.setCurrentlimit(metadata().familyDueRegister.currentLimit);
        this.clientsView.setAdapter(this.clientAdapter);
        //need some delay to ready the adapter
        new Handler().postDelayed(() -> {
            if (flavorWashCheck.isWashCheckVisible())
                ((FamilyProfileDuePresenter) presenter).fetchLastWashCheck(dateFamilyCreated);

        }, 500);

    }

    @Override
    public void countExecute() {
        super.countExecute();
        final int count = clientAdapter.getTotalcount();
        if (getActivity() != null && count != dueCount) {
            dueCount = count;
            ((FamilyProfileActivity) getActivity()).updateDueCount(dueCount);
        }
        if (getActivity() != null) getActivity().runOnUiThread(() -> onEmptyRegisterCount(count < 1));
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        emptyView = view.findViewById(R.id.empty_view);
        washCheckView = view.findViewById(R.id.wash_check_layout);

    }

    private void addWashCheckView() {
        View inflatLayout = getLayoutInflater().inflate(R.layout.view_wash_check, null);
        washCheckView.addView(inflatLayout);
        washCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonForm = FormUtils.getInstance(getActivity()).getFormJson(org.smartregister.chw.util.Constants.JSON_FORM.getWashCheck());
                    jsonForm.put(ENTITY_ID, familyBaseEntityId);
                    Intent intent = new Intent(getActivity(), metadata().familyMemberFormActivity);
                    intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

                    Form form = new Form();
                    form.setWizard(false);
                    form.setActionBarBackground(org.smartregister.family.R.color.customAppThemeBlue);

                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(org.smartregister.family.util.Constants.WizardFormActivity.EnableOnCloseDialog, true);
                    getActivity().startActivityForResult(intent, REQUEST_CODE_GET_JSON_WASH);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onEmptyRegisterCount(final boolean has_no_records) {
        if (emptyView != null) {
            emptyView.setVisibility(has_no_records ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.patient_column:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    goToChildProfileActivity(view);
                }
                break;
            case R.id.next_arrow:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
                    goToChildProfileActivity(view);
                }
                break;
            default:
                break;
        }
    }

    public void goToChildProfileActivity(View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient patient = (CommonPersonObjectClient) view.getTag();

            Intent intent = new Intent(getActivity(), ChildProfileActivity.class);
            intent.putExtras(getArguments());
            intent.putExtra(IS_COMES_FROM_FAMILY, true);
            intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
            startActivity(intent);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_JSON_WASH:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartregister.chw.util.Constants.EventType.WASH_CHECK)
                        ) {
                            boolean isSave = ((FamilyProfileDuePresenter) presenter).saveData(jsonString);
                            if (isSave) {
                                visibilityWashView(false);
                                if (getActivity() != null && getActivity() instanceof FamilyProfileActivity) {
                                    FamilyProfileActivity familyProfileActivity = (FamilyProfileActivity) getActivity();
                                    familyProfileActivity.updateWashCheckActivity();

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }

    public void updateWashCheckBar(WashCheck washCheck) {
        if(washCheckView.getVisibility() == View.VISIBLE) return;
        addWashCheckView();
        TextView name = washCheckView.findViewById(R.id.patient_name_age);
        TextView lastVisit = washCheckView.findViewById(R.id.last_visit);
        ImageView status = washCheckView.findViewById(R.id.status);
        if (washCheck == null || washCheck.getStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
            visibilityWashView(true);
            status.setImageResource(org.smartregister.chw.util.Utils.getDueProfileImageResourceIDentifier());
            if (washCheck == null) {
                lastVisit.setVisibility(View.GONE);
            } else {
                lastVisit.setText(String.format(getActivity().getString(R.string.last_visit_prefix), washCheck.getLastVisitDate()));
            }
            name.setText(getActivity().getString(R.string.family, familyName) + " " + getActivity().getString(R.string.wash_check_suffix));


        } else if (washCheck.getStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            visibilityWashView(true);
            status.setImageResource(org.smartregister.chw.util.Utils.getOverDueProfileImageResourceIDentifier());
            lastVisit.setText(String.format(getActivity().getString(R.string.last_visit_prefix), washCheck.getLastVisitDate()));
            name.setText(getActivity().getString(R.string.family, familyName) + " " + getActivity().getString(R.string.wash_check_suffix));

        } else {
            washCheckView.setVisibility(View.GONE);
        }

    }

    private void visibilityWashView(boolean isVisible) {
        if ((isVisible)) {
            washCheckView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            dueCount++;
        } else {
            dueCount--;
            washCheckView.setVisibility(View.GONE);
        }
        ((FamilyProfileActivity) getActivity()).updateDueCount(dueCount);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        Timber.d("setAdvancedSearchFormData");
    }

    public interface Flavor {
        boolean isWashCheckVisible();
    }

}