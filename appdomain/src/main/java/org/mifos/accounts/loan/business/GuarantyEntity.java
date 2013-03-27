package org.mifos.accounts.loan.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifos.framework.business.AbstractEntity;

@Entity
@Table(name = "guaranty")
public class GuarantyEntity  extends AbstractEntity{

    @Id
    @GeneratedValue
    @Column(name = "guaranty_id")
    private Integer guarantyId;

    @Column(name = "guarantor_id")
    private Integer guarantorId;
    
    @Column(name = "loan_id")
    private Integer loanId;

    @Column(name = "state")
    private Boolean state;

    public Integer getId() {
        return guarantyId;
    }

    public Integer getGuarantorId() {
        return guarantorId;
    }

    public void setGuarantorId(Integer guarantorId) {
        this.guarantorId = guarantorId;
    }

    public Integer getLoanId() {
        return this.loanId;
    }
    
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean guarantyState) {
        this.state = guarantyState;
    }
}