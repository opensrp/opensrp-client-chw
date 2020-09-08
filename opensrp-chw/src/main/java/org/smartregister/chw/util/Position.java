package org.smartregister.chw.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Position {

    private List<String> indexPosition = new ArrayList<>();
    private String myIndex;

    public Position(String index) {
        indexPosition.add(index);
        this.myIndex = index;
    }

    public Position(Position parentPosition, String index) {
        indexPosition.addAll(parentPosition.getHierarchy());
        indexPosition.add(index);
        this.myIndex = index;
    }

    public List<String> getHierarchy() {
        return Collections.unmodifiableList(indexPosition);
    }

    public int getDepth() {
        return indexPosition.size();
    }

    public String getMyIndex() {
        return myIndex;
    }

    public boolean isParentOf(@NonNull Position child) {
        // if the child is a higher position than the current
        if (child.getDepth() < getDepth())
            return false;

        int x = 0;
        int end = child.getDepth();
        int currentMaxDepth = getDepth();

        while (x < end) {
            if (currentMaxDepth > x) {

                String childPos = child.getHierarchy().get(x);
                String myPos = getHierarchy().get(x);

                if (!childPos.equals(myPos)) {
                    System.out.println(child.getMyIndex() + " is not a child of " + getMyIndex());
                    return false;
                }

            } else {

                System.out.println(getMyIndex() + " is a parent of " + child.getMyIndex());
                return true;
            }

            x++;
        }

        System.out.println(child.getMyIndex() + " is not a child of " + getMyIndex());
        return false;
    }
}
