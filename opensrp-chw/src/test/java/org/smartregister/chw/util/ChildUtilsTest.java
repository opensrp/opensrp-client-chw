package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseUnitTest;
import java.util.Arrays;
import java.util.List;

public class ChildUtilsTest extends BaseUnitTest {

    @Test
    public void isFullyImmunizedForTwoYears(){
      String[] list = {"OPV0".toLowerCase(),"BCG".toLowerCase(),"OPV1".toLowerCase(),"OPV2".toLowerCase(),"OPV3".toLowerCase()
              ,"Penta1".toLowerCase(),"Penta2".toLowerCase(),"Penta3".toLowerCase(),"PCV1".toLowerCase(),"PCV2".toLowerCase()
              ,"PCV3".toLowerCase(),"Rota1".toLowerCase(),"Rota2".toLowerCase(),"IPV".toLowerCase(),"MCV1".toLowerCase()
              ,"MCV2".toLowerCase(),"yellowfever".toLowerCase()};
        List<String> receivedVaccine = Arrays.asList(list);
        Assert.assertEquals("2",ChildUtils.isFullyImmunized(receivedVaccine));
    }

    @Test
    public void isFullyImmunizedForOneYears(){
        String[] list = {"OPV0".toLowerCase(),"BCG".toLowerCase(),"OPV1".toLowerCase(),"OPV2".toLowerCase(),"OPV3".toLowerCase()
                ,"Penta1".toLowerCase(),"Penta2".toLowerCase(),"Penta3".toLowerCase(),"PCV1".toLowerCase(),"PCV2".toLowerCase()
                ,"PCV3".toLowerCase(),"Rota1".toLowerCase(),"Rota2".toLowerCase(),"IPV".toLowerCase(),"MCV1".toLowerCase(),"yellowfever".toLowerCase()};
        List<String> receivedVaccine = Arrays.asList(list);
        Assert.assertEquals("1",ChildUtils.isFullyImmunized(receivedVaccine));
    }

    @Test
    public void isFullyImmunizedForEmpty(){
        String[] list = {"OPV0".toLowerCase(),"BCG".toLowerCase(),"OPV1".toLowerCase(),"OPV2".toLowerCase()};
        List<String> receivedVaccine = Arrays.asList(list);
        Assert.assertEquals("",ChildUtils.isFullyImmunized(receivedVaccine));
    }

}
