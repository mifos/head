package org.mifos.test.acceptance.framework.client;

public class ClientEditFamilyParameters {

    // gender
    public static final int MALE = 49;
    public static final int FEMALE = 50;
    
    //Living Status
    public static final int TOGETHER= 622;
    public static final int NOT_TOGETHER= 623;
    
    //relationship
    public static final int SPOUSE = 1;
    public static final int FATHER = 2;
    public static final int OTHER_RELATIVE= 4;
    
    Integer relationship;
    Integer livingStatus;
    String firstName;
    String lastName;
    String middleName;
    String dateOfBirthDD;
    String dateOfBirthMM;
    String dateOfBirthYY;
    Integer gender;
    public Integer getRelationship() {
        return this.relationship;
    }
    public void setRelationship(Integer relationship) {
        this.relationship = relationship;
    }
    public Integer getLivingStatus() {
        return this.livingStatus;
    }
    public void setLivingStatus(Integer livingStatus) {
        this.livingStatus = livingStatus;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getMiddleName() {
        return this.middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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
    public Integer getGender() {
        return this.gender;
    }
    public void setGender(Integer gender) {
        this.gender = gender;
    }
}
