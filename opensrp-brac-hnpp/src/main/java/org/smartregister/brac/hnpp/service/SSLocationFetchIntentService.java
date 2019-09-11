package org.smartregister.brac.hnpp.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.NoHttpResponseException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.location.SSModel;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;

public class SSLocationFetchIntentService extends IntentService {

    private static final String LOCATION_FETCH = "opensrp/provider/location-tree";
    private static final String TAG = "SSLocation";

    public SSLocationFetchIntentService() { super(TAG); }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SSLocationFetchIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        JSONObject jsonObjectLocation = getLocationList();
        if(jsonObjectLocation!=null){
           SSModel ssModel =  new Gson().fromJson(jsonObjectLocation.toString(), SSModel.class);
           if(ssModel != null){
               HnppApplication.getSSLocationRepository().addOrUpdate(ssModel);
           }
        }

    }

    private JSONObject getLocationList(){
        try{
            HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
            String baseUrl = CoreLibrary.getInstance().context().
                    configuration().dristhiBaseURL();
            String endString = "/";
            if (baseUrl.endsWith(endString)) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
            }
            String userName = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
            if(TextUtils.isEmpty(userName)){
                return null;
            }
            //testing
            String url = baseUrl + LOCATION_FETCH + "username=" + userName;
            Log.v("LOCATION_FETCH","getLocationList>>url:"+url);
            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                throw new NoHttpResponseException(LOCATION_FETCH + " not returned data");
            }

            return new JSONObject((String) resp.payload());
        }catch (Exception e){

        }
        return null;

    }
}
