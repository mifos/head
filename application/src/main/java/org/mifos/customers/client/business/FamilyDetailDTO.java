package org.mifos.customers.client.business;

public class FamilyDetailDTO {

    private short relationship=0;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirthDD;
    private String dateOfBirthMM;
    private String dateOfBirthYY;
    private short gender=0;
    private short livingStatus=0;

    public short getRelationship() {
        return this.relationship;
    }

    public void setRelationship(short relationship) {
        this.relationship = relationship;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirthDD() {
        return this.dateOfBirthDD;
    }

    public void setDateOfBirthDD(String dateOfBirthDD) {
        this.dateOfBirthDD = dateOfBirthDD;
    }

    public String getDateOfBirthMM() {
        return this.dateOfBirthMM;
    }

    public void setDateOfBirthMM(String dateOfBirthMM) {
        this.dateOfBirthMM = dateOfBirthMM;
    }

    public String getDateOfBirthYY() {
        return this.dateOfBirthYY;
    }

    public void setDateOfBirthYY(String dateOfBirthYY) {
        this.dateOfBirthYY = dateOfBirthYY;
    }

    public short getGender() {
        return this.gender;
    }

    public void setGender(short gender) {
        this.gender = gender;
    }

    public short getLivingStatus() {
        return this.livingStatus;
    }

    public void setLivingStatus(short livingStatus) {
        this.livingStatus = livingStatus;
    }


}
