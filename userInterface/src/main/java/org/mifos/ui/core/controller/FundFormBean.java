package org.mifos.ui.core.controller;

import org.mifos.accounts.fund.servicefacade.FundCodeDto;

public class FundFormBean {

    private String id;

    private FundCodeDto code;

    private String name;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FundCodeDto getCode() {
        return this.code;
    }

    public void setCode(FundCodeDto code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
