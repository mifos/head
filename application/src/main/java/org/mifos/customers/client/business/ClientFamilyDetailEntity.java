package org.mifos.customers.client.business;

import java.sql.Date;

import org.mifos.framework.business.PersistentObject;

public class ClientFamilyDetailEntity extends PersistentObject {

    private final Integer customerFamilyId;

    private final ClientBO client;

    private final ClientNameDetailEntity clientName;

    private Short relationship;

    private Short gender;

    private Short livingStatus;

    private Date dateOfBirth;

    public ClientFamilyDetailEntity(ClientBO client,ClientNameDetailEntity clientName, ClientFamilyDetailView clientFamily){
        this.customerFamilyId=null;

        this.client=client;
        this.clientName=clientName;
        this.relationship=clientFamily.getRelationship();
        this.gender=clientFamily.getGender();
        this.livingStatus=clientFamily.getLivingStatus();
        this.dateOfBirth=clientFamily.getDateOfBirth();
    }

    protected ClientFamilyDetailEntity() {
        super();
        this.customerFamilyId=null;
        this.client=null;
        this.clientName=null;
        this.relationship= null;
        this.gender=null;
        this.livingStatus=null;
        this.dateOfBirth=null;

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

    public void setGender(short gender) {
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

    public Integer getCustomerFamilyId() {
        return this.customerFamilyId;
    }

    public ClientBO getClient() {
        return this.client;
    }

    public ClientNameDetailEntity getClientName() {
        return this.clientName;
    }

    public void updateClientFamilyDetails(ClientFamilyDetailView familyDetails){
        this.setRelationship(familyDetails.getRelationship());
        this.setGender(familyDetails.getGender());
        this.setLivingStatus(familyDetails.getLivingStatus());
        this.setDateOfBirth(familyDetails.getDateOfBirth());
    }



}
