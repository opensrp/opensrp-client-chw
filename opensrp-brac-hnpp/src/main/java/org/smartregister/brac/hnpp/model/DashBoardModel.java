package org.smartregister.brac.hnpp.model;

import android.content.Context;
import android.database.Cursor;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.contract.DashBoardContract;
import org.smartregister.brac.hnpp.utils.DashBoardData;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.application.CoreChwApplication;

import java.util.ArrayList;

import timber.log.Timber;

public class DashBoardModel implements DashBoardContract.Model {
    private Context context;

    public DashBoardModel(Context context){
        this.context = context;
    }

    private ArrayList<DashBoardData> dashBoardDataArrayList = new ArrayList<>();

    public ArrayList<DashBoardData> getDashBoardDataArrayList() {
        return dashBoardDataArrayList;
    }

    /**
     * Date formate should be Y-m-d (2019-10-24)
     * @param todate
     * @param fromDate
     * @return
     */
    public ArrayList<DashBoardData> getDashData(String todate, String fromDate){
        String query = "select eventType,count(*),eventDate as count from event where strftime('%Y-%m-%d', eventDate) BETWEEN '"+fromDate+"' AND '"+todate+"' group by eventType";
        Cursor cursor = null;
        dashBoardDataArrayList.clear();
        try {
            cursor = CoreChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, new String[]{});
            if(cursor !=null && cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    DashBoardData dashBoardData1 = new DashBoardData();
                    dashBoardData1.setCount(cursor.getInt(1));
                    dashBoardData1.setEventType(cursor.getString(0));
                    dashBoardData1.setTitle(HnppConstants.eventTypeMapping.get(dashBoardData1.getEventType()));
                    dashBoardData1.setImageSource((int)HnppConstants.iconMapping.get(dashBoardData1.getEventType()));
                    dashBoardDataArrayList.add(dashBoardData1);
                    cursor.moveToNext();
                }

            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return dashBoardDataArrayList;
    }


    @Override
    public DashBoardContract.Model getDashBoardModel() {
        return this;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
