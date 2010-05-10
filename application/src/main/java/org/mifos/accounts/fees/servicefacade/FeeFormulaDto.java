package org.mifos.accounts.fees.servicefacade;

public class FeeFormulaDto {

    private Short id;
    private String name;

    public void setId(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
