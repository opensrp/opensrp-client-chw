package org.smartregister.chw.presenter;

import org.smartregister.chw.pmtct.contract.PmtctRegisterFragmentContract;
import org.smartregister.chw.pmtct.presenter.BasePmtctRegisterFragmentPresenter;
import org.smartregister.chw.util.Constants;

public class MotherChampionSbccRegisterFragmentPresenter extends BasePmtctRegisterFragmentPresenter {
    public MotherChampionSbccRegisterFragmentPresenter(PmtctRegisterFragmentContract.View view, PmtctRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TableName.SBCC;
    }

}
