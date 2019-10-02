package org.smartregister.brac.hnpp.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SSModel {
    public int id;
    public String username;
    @SerializedName("simprints_enable")
    public boolean simprints_enable = false;
    public ArrayList<SSLocations> locations = new ArrayList<>() ;


}
