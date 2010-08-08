package org.mifos.dto.domain;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("PMD")
public class AddressDto {
    private final String line1;
    private final String line2;
    private final String line3;
    private final String city;
    private final String state;
    private final String country;
    private final String zip;
    private final String phoneNumber;

    public AddressDto(String line1, String line2, String line3, String city, String state, String country, String zip,
            String phoneNumber) {
        super();
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
    }

    public String getLine1() {
        return this.line1;
    }


    public String getLine2() {
        return this.line2;
    }


    public String getLine3() {
        return this.line3;
    }


    public String getCity() {
        return this.city;
    }


    public String getState() {
        return this.state;
    }


    public String getCountry() {
        return this.country;
    }


    public String getZip() {
        return this.zip;
    }


    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getDisplayAddress() {
        String displayAddress = "";
        if (StringUtils.isNotBlank(getLine1())) {
            displayAddress = getLine1();
        }
        if (StringUtils.isNotBlank(getLine2()) && StringUtils.isNotBlank(getLine1())) {
            displayAddress += ", " + getLine2();
        } else if (StringUtils.isNotBlank(getLine2())) {
            displayAddress = getLine2();
        }
        if (StringUtils.isNotBlank(getLine3()) && StringUtils.isNotBlank(getLine2())
                || (StringUtils.isNotBlank(getLine3()) && StringUtils.isNotBlank(getLine1()))) {
            displayAddress += ", " + getLine3();
        } else if (StringUtils.isNotBlank(getLine3())) {
            displayAddress += getLine3();
        }
        return displayAddress;
    }
}
