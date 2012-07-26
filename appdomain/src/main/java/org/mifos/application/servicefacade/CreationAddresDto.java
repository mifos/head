package org.mifos.application.servicefacade;

import org.mifos.dto.domain.AddressDto;

public class CreationAddresDto {

    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String phoneNumber;

    public CreationAddresDto(String address1, String address2, String address3, String city, String state, String country,
            String zip, String phoneNumber) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
    }

    public String getLine1() {
        return address1;
    }

    public void setLine1(String line1) {
        this.address1 = line1;
    }

    public String getLine2() {
        return address2;
    }

    public void setLine2(String line2) {
        this.address2 = line2;
    }

    public String getLine3() {
        return address3;
    }

    public void setLine3(String line3) {
        this.address3 = line3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AddressDto toDto() {
        return new AddressDto(getLine1(), getLine2(), getLine3(), getCity(), getState(), getCountry(), getZip(),
                getPhoneNumber());

    }
}
