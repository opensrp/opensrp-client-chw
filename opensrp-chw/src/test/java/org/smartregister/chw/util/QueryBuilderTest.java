package org.smartregister.chw.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.chw.core.utils.QueryBuilder;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

public class QueryBuilderTest {

    @Test
    public void testGetQueryWithJoinTable() {

        String expected_result = "SELECT object_id FROM ec_family_search WHERE object_id IN ( SELECT object_id FROM ec_family_search WHERE date_removed is null AND PHRASE MATCH 'gu*' UNION SELECT object_relational_id FROM ec_family_member_search WHERE date_removed is null AND PHRASE MATCH 'gu*' ) AND date_removed is null ORDER BY last_interacted_with DESC  LIMIT 0 , 20";

        String[] joinTables = new String[]{"ec_family_member"};
        String mainCondition = "date_removed is null";
        String tablename = "ec_family";
        String filters = "gu";
        RecyclerViewPaginatedAdapter clientAdapter = Mockito.mock(RecyclerViewPaginatedAdapter.class);
        String Sortqueries = "last_interacted_with DESC";

        // adapter should return default value
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();
        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();

        String query = QueryBuilder.getQuery(joinTables, mainCondition, tablename, filters, clientAdapter, Sortqueries).trim().replace("  ", " ");

        Assert.assertEquals("testGetQueryWithJoinTable", expected_result.replace("  ", " "), query.replace("  ", " "));
    }

    @Test
    public void testGetQueryWithoutJoinTable() {

        String expected_result = "SELECT object_id FROM ec_family_search WHERE date_removed is null AND PHRASE MATCH 'gu*' ORDER BY last_interacted_with DESC  LIMIT 0 , 20";

        String[] joinTables = new String[]{};
        String mainCondition = "date_removed is null";
        String tablename = "ec_family";
        String filters = "gu";
        RecyclerViewPaginatedAdapter clientAdapter = Mockito.mock(RecyclerViewPaginatedAdapter.class);
        String Sortqueries = "last_interacted_with DESC";

        // adapter should return default value
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();
        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();

        String query = QueryBuilder.getQuery(joinTables, mainCondition, tablename, filters, clientAdapter, Sortqueries).trim().replace("  ", " ");

        Assert.assertEquals("testGetQueryWithoutJoinTable", expected_result.replace("  ", " "), query.replace("  ", " "));
    }

    @Test
    public void testGetQueryNoConditionWithJoin() {

        String expected_result = "SELECT object_id FROM ec_family_search WHERE object_id IN ( SELECT object_id FROM ec_family_search WHERE PHRASE MATCH 'gu*' UNION SELECT object_relational_id FROM ec_family_member_search WHERE PHRASE MATCH 'gu*' ) ORDER BY last_interacted_with DESC  LIMIT 0 , 20";

        String[] joinTables = new String[]{"ec_family_member"};
        String mainCondition = " ";
        String tablename = "ec_family";
        String filters = "gu";
        RecyclerViewPaginatedAdapter clientAdapter = Mockito.mock(RecyclerViewPaginatedAdapter.class);
        String Sortqueries = "last_interacted_with DESC";

        // adapter should return default value
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();
        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();

        String query = QueryBuilder.getQuery(joinTables, mainCondition, tablename, filters, clientAdapter, Sortqueries).trim().replace("  ", " ");

        Assert.assertEquals("testGetQueryNoConditionWithJoin", expected_result.replace("  ", " "), query.replace("  ", " "));
    }

    @Test
    public void testGetQueryNoCondition() {

        String expected_result = "SELECT object_id FROM ec_child_search WHERE PHRASE MATCH 'gu*' ORDER BY last_interacted_with DESC  LIMIT 0 , 20";

        String[] joinTables = new String[]{};
        String mainCondition = "";
        String tablename = "ec_child";
        String filters = "gu";
        RecyclerViewPaginatedAdapter clientAdapter = Mockito.mock(RecyclerViewPaginatedAdapter.class);
        String Sortqueries = "last_interacted_with DESC";

        // adapter should return default value
        Mockito.doReturn(0).when(clientAdapter).getCurrentoffset();
        Mockito.doReturn(20).when(clientAdapter).getCurrentlimit();

        String query = QueryBuilder.getQuery(joinTables, mainCondition, tablename, filters, clientAdapter, Sortqueries).trim().replace("  ", " ");

        Assert.assertEquals("testGetQueryNoCondition", expected_result.replace("  ", " "), query.replace("  ", " "));
    }
}
