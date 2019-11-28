package org.smartregister.brac.hnpp.interactor;

import android.content.Context;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.contract.MemberHistoryContract;
import org.smartregister.brac.hnpp.utils.MemberHistoryData;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class MemberHistoryInteractor implements MemberHistoryContract.Interactor {

    private AppExecutors appExecutors;

    public MemberHistoryInteractor(AppExecutors appExecutors){
        this.appExecutors = appExecutors;
    }

    @Override
    public void fetchData(Context context, String baseEntityId, MemberHistoryContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            ArrayList<MemberHistoryData> memberHistoryData = getHistory(baseEntityId);
            appExecutors.mainThread().execute(() -> callBack.onUpdateList(memberHistoryData));
        };
        appExecutors.diskIO().execute(runnable);

    }

    private ArrayList<MemberHistoryData> getHistory(String baseEntityId) {
        ArrayList<MemberHistoryData> historyDataArrayList  = new ArrayList<>();
        MemberHistoryData memberHistoryData = new MemberHistoryData();
        memberHistoryData.setImageSource(R.drawable.childrow_family);
        memberHistoryData.setTitle("গর্ভবতী পরিচর্যা-১ম ত্রিমাসিক");
        memberHistoryData.setVisitDate(System.currentTimeMillis());
        historyDataArrayList.add(memberHistoryData);

        MemberHistoryData memberHistoryData2 = new MemberHistoryData();
        memberHistoryData2.setImageSource(R.drawable.rowavatar_member);
        memberHistoryData2.setTitle("গর্ভবতী পরিচর্যা-১ম ত্রিমাসিক");
        memberHistoryData2.setVisitDate(System.currentTimeMillis()+2000);

        historyDataArrayList.add(memberHistoryData2);
        return historyDataArrayList;

    }

}
