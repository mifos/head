package org.mifos.customers.client.business;

import java.sql.Date;

import org.mifos.framework.util.helpers.DateUtils;

public class ClientFamilyDetailView {

    private Short relationship;

    private Short gender;

    private Short livingStatus;

    private Date dateOfBirth;

    private String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ClientFamilyDetailView(Short relationship, Short gender, Short livingStatus, Date dateOfBirth) {
        super();
        this.relationship = relationship;
        this.gender = gender;
        this.livingStatus = livingStatus;
        this.dateOfBirth = dateOfBirth;
    }

    public Short getRelationship() {
        return this.relationship;
    }

    public void setRelationship(Short relationship) {
        this.relationship = relationship;
    }

    public Short getGender() {
        return this.gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Short getLivingStatus() {
        return this.livingStatus;
    }

    public void setLivingStatus(Short livingStatus) {
        this.livingStatus = livingStatus;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirthForBrowser() {
        if(getDateOfBirth()!=null) {
            return DateUtils.makeDateAsSentFromBrowser(getDateOfBirth());
        }
        return null;
    }


}
