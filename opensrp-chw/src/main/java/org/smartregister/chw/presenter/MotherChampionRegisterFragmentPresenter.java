package org.smartregister.chw.presenter;

import org.smartregister.chw.pmtct.contract.PmtctRegisterFragmentContract;
import org.smartregister.chw.pmtct.presenter.BasePmtctRegisterFragmentPresenter;

import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME;

public class MotherChampionRegisterFragmentPresenter extends BasePmtctRegisterFragmentPresenter {
    public MotherChampionRegisterFragmentPresenter(PmtctRegisterFragmentContract.View view, PmtctRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " ec_family_member.date_removed is null AND ec_pmtct_community_followup.base_entity_id NOT IN (SELECT community_referral_form_id FROM ec_pmtct_community_feedback)";
    }

    @Override
    public String getMainTable() {
        return TABLE_NAME.PMTCT_COMMUNITY_FOLLOWUP;
    }
}
