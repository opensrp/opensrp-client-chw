package org.smartregister.chw.domain;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistory {

    private String title;
    private List<String> text;

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

    public void setText(String text) {
        if (this.text == null)
            this.text = new ArrayList<>();

        this.text.add(text);
    }
}
