package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.List;

public class AdminDocumentFormBean {

    private Integer id;            // unique ID associated with document

    //@NotEmpty @Size(min=1, max=100)     // not sure about max size
    private String name;           // document title

    //@NotEmpty
    private String accountType;// savings or loan

    // contains the options selected from multiselect listbox
    // TODO: does not bind correctly
    private List<String> showStatus = new ArrayList<String>();

    /*
     * TODO: Also need to add an object (file object?) to which
     * an uploaded file will be bound.
     */

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    public List<String> getshowStatus() {
        return showStatus;
    }
    public void setshowStatus(List<String> showStatus) {
        this.showStatus = showStatus;
    }
    // overloaded following method mostly for testing right now, trying to figure
    // out how to bind to this field!
    public void setshowStatus(String[] showStatus) {
        for (String showStatu : showStatus) {
            this.showStatus.add( showStatu );
        }
    }
}