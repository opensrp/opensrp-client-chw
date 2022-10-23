package org.smartregister.chw.domain;

import org.smartregister.chw.anc.domain.Visit;

public class SortableVisit extends Visit implements Comparable<SortableVisit> {
    @Override
    public int compareTo(SortableVisit sortableVisit) {
        long data = getDate().getTime() - sortableVisit.getDate().getTime();
        return (int) data;
    }
}
