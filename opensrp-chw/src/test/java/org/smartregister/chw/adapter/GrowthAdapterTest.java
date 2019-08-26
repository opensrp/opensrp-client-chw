package org.smartregister.chw.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.ServiceContent;
import org.smartregister.chw.util.ServiceHeader;

import java.util.ArrayList;

public class GrowthAdapterTest extends BaseUnitTest {

    private Context context = RuntimeEnvironment.application;

    @Test
    public void testAddItemData() {
        GrowthAdapter adapter = new GrowthAdapter();
        int count = 10;
        adapter.addItem(getSamples(count));

        Assert.assertEquals(adapter.getItemCount(), count);
    }

    private ArrayList<BaseService> getSamples(int count) {
        int x = 0;
        ArrayList<BaseService> contentList = new ArrayList<>();
        while (x < count) {
            contentList.add(Mockito.mock(BaseService.class));
            x++;
        }
        return contentList;
    }

    @Test
    public void testOnCreateViewHolder() {
        GrowthAdapter adapter = new GrowthAdapter();

        ViewGroup viewGroup = Mockito.mock(ViewGroup.class);
        Mockito.doReturn(context).when(viewGroup).getContext();

        Assert.assertTrue(adapter.onCreateViewHolder(viewGroup, BaseService.TYPE_HEADER) instanceof GrowthAdapter.HeaderViewHolder);
        Assert.assertTrue(adapter.onCreateViewHolder(viewGroup, BaseService.TYPE_CONTENT) instanceof GrowthAdapter.ContentViewHolder);
        Assert.assertTrue(adapter.onCreateViewHolder(viewGroup, BaseService.TYPE_LINE) instanceof GrowthAdapter.HeaderViewHolder);
    }

    @Test
    public void testOnBindViewHolder() {
        GrowthAdapter adapter = Mockito.spy(new GrowthAdapter());
        ArrayList<BaseService> baseServices = Mockito.mock(ArrayList.class);
        ServiceContent serviceContent = Mockito.mock(ServiceContent.class);
        Mockito.doReturn("sample").when(serviceContent).getServiceName();

        Mockito.doReturn(serviceContent).when(baseServices).get(Mockito.anyInt());
        Whitebox.setInternalState(adapter, "baseServices", baseServices);

        // content holder
        GrowthAdapter.ContentViewHolder contentViewHolder = Mockito.mock(GrowthAdapter.ContentViewHolder.class);
        Mockito.doReturn(BaseService.TYPE_CONTENT).when(contentViewHolder).getItemViewType();
        TextView textView = Mockito.mock(TextView.class);
        Whitebox.setInternalState(contentViewHolder, "vaccineName", textView);

        adapter.onBindViewHolder(contentViewHolder, 0);
        Mockito.verify(textView).setText(Mockito.anyString());
    }

    @Test
    public void testOnBindViewHolderHeader() {
        GrowthAdapter adapter = Mockito.spy(new GrowthAdapter());
        ArrayList<BaseService> baseServices = Mockito.mock(ArrayList.class);
        ServiceHeader serviceHeader = Mockito.mock(ServiceHeader.class);
        Mockito.doReturn("sample").when(serviceHeader).getServiceHeaderName();

        Mockito.doReturn(serviceHeader).when(baseServices).get(Mockito.anyInt());
        Whitebox.setInternalState(adapter, "baseServices", baseServices);

        // content holder
        GrowthAdapter.HeaderViewHolder headerViewHolder = Mockito.mock(GrowthAdapter.HeaderViewHolder.class);
        Mockito.doReturn(BaseService.TYPE_HEADER).when(headerViewHolder).getItemViewType();
        TextView textView = Mockito.mock(TextView.class);
        Whitebox.setInternalState(headerViewHolder, "headerTitle", textView);

        adapter.onBindViewHolder(headerViewHolder, 0);
        Mockito.verify(textView).setText(Mockito.anyString());
    }

}
