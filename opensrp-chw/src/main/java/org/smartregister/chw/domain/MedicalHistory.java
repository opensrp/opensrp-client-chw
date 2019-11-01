package org.smartregister.chw.domain;

import android.support.annotation.DrawableRes;

import org.smartregister.chw.R;

import java.util.List;

public class MedicalHistory {

    private String title;
    private List<String> text;
    @DrawableRes
    private int bulletType = R.drawable.circle_drawable;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public int getBulletType() {
        return bulletType;
    }

    public void setBulletType(int bulletType) {
        this.bulletType = bulletType;
    }
}
