package org.smartregister.brac.hnpp.interactor;

import android.content.Context;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.contract.OtherServiceContract;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.OtherServiceData;
import org.smartregister.family.util.AppExecutors;

import java.util.ArrayList;

public class MemberOtherServiceInteractor implements OtherServiceContract.Interactor {

    private AppExecutors appExecutors;

    public MemberOtherServiceInteractor(AppExecutors appExecutors){
        this.appExecutors = appExecutors;
    }


    @Override
    public void fetchData(Context context, OtherServiceContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            ArrayList<OtherServiceData> otherServiceData = getOtherService();
            appExecutors.mainThread().execute(() -> callBack.onUpdateList(otherServiceData));
        };
        appExecutors.diskIO().execute(runnable);

    }
    private ArrayList<OtherServiceData> getOtherService(){

        ArrayList<OtherServiceData> otherServiceDataList = new ArrayList<>();
        OtherServiceData otherServiceData = new OtherServiceData();
        otherServiceData.setImageSource(R.drawable.woman_placeholder);
        otherServiceData.setTitle("নারী সেবা প্যাকেজ");
        otherServiceData.setType(HnppConstants.OTHER_SERVICE_TYPE.TYPE_WOMEN_PACKAGE);
        otherServiceDataList.add(otherServiceData);

        OtherServiceData otherServiceData2 = new OtherServiceData();
        otherServiceData2.setImageSource(R.drawable.woman_placeholder);
        otherServiceData2.setTitle("কিশোরী সেবা প্যাকেজ");
        otherServiceData2.setType(HnppConstants.OTHER_SERVICE_TYPE.TYPE_GIRL_PACKAGE);
        otherServiceDataList.add(otherServiceData2);

        OtherServiceData otherServiceData3 = new OtherServiceData();
        otherServiceData3.setImageSource(R.drawable.ic_muac);
        otherServiceData3.setTitle("ব্যাধি সেবা প্যাকেজ (এন সি ডি)");
        otherServiceData3.setType(HnppConstants.OTHER_SERVICE_TYPE.TYPE_NCD);
        otherServiceDataList.add(otherServiceData3);

        OtherServiceData otherServiceData4 = new OtherServiceData();
        otherServiceData4.setImageSource(R.drawable.child_girl_infant);
        otherServiceData4.setTitle("শিশু সেবা প্যাকেজ (আই.ওয়াই.সি.এফ)");
        otherServiceData4.setType(HnppConstants.OTHER_SERVICE_TYPE.TYPE_IYCF);
        otherServiceDataList.add(otherServiceData4);

        return otherServiceDataList;
    }
}
