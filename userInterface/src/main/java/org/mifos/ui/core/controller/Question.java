package org.mifos.ui.core.controller;

import java.io.Serializable;

public class Question implements Serializable{
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
