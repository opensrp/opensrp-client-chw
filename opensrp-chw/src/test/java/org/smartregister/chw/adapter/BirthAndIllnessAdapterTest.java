package org.smartregister.chw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.BaseUnitTest;

import java.util.ArrayList;

public class BirthAndIllnessAdapterTest extends BaseUnitTest {

    private Context context = RuntimeEnvironment.application;

    @Test
    public void testSetData() {
        BirthAndIllnessAdapter adapter = new BirthAndIllnessAdapter();
        int count = 10;
        adapter.setData(getSamples(count));

        Assert.assertEquals(adapter.getItemCount(), count);
    }

    private ArrayList<String> getSamples(int count) {
        int x = 0;
        ArrayList<String> contentList = new ArrayList<>();
        while (x < count) {
            contentList.add("sample");
            x++;
        }
        return contentList;
    }

    @Test
    public void testOnCreateViewHolder() {
        BirthAndIllnessAdapter adapter = new BirthAndIllnessAdapter();

        ViewGroup viewGroup = Mockito.mock(ViewGroup.class);
        Mockito.doReturn(context).when(viewGroup).getContext();

        RecyclerView.ViewHolder sample = adapter.onCreateViewHolder(viewGroup, 0);

        boolean valid = sample instanceof BirthAndIllnessAdapter.ContentViewHolder;

        Assert.assertTrue(valid);
    }

    @Test
    public void testOnBindViewHolder() {
        BirthAndIllnessAdapter adapter = new BirthAndIllnessAdapter();
        int count = 10;
        adapter.setData(getSamples(count));

        BirthAndIllnessAdapter.ContentViewHolder contentViewHolder = Mockito.mock(BirthAndIllnessAdapter.ContentViewHolder.class);
        TextView textView = Mockito.mock(TextView.class);
        Whitebox.setInternalState(contentViewHolder, "vaccineName", textView);

        adapter.onBindViewHolder(contentViewHolder, 1);

        Mockito.verify(textView).setText(Mockito.anyString());
    }
}
