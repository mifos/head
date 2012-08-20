package org.mifos.application.servicefacade;

import org.mifos.customers.client.business.NameType;
import org.mifos.dto.screen.ClientNameDetailDto;

public class CreateClientNameDetailDto {
    private Integer salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;

    public Integer getSalutation() {
        return salutation;
    }

    public void setSalutation(Integer salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }
    
    public String getDisplayName() {
        StringBuilder dispalyName = new StringBuilder();
        if (!getFirstName().isEmpty()) {
            dispalyName.append(getFirstName());    
        }
        if (!getMiddleName().isEmpty()) {
            dispalyName.append(" ");
            dispalyName.append(getMiddleName());
        }
        if (!getLastName().isEmpty()) {
            dispalyName.append(" ");
            dispalyName.append(getLastName());
        }
        if (!getSecondLastName().isEmpty()) {
            dispalyName.append(" ");
            dispalyName.append(getSecondLastName());
        }
        
        return dispalyName.toString();
    }
    
    public ClientNameDetailDto toDto() {
        return new ClientNameDetailDto(NameType.CLIENT.getValue(), getSalutation(), getFirstName(), getMiddleName(), getLastName(), getSecondLastName());
    }

}
