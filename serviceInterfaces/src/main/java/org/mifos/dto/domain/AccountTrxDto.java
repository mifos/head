package org.mifos.dto.domain;

public class AccountTrxDto {
    /** 
     * Dto for handling undo full payments import 
     * 
     * id from account_trxn table
     * */
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccountTrxDto(Integer id) {
        this.id = id;
    }
    
}
