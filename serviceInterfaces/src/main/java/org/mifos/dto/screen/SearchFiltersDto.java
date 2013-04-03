package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Map;

public class SearchFiltersDto implements Serializable {
    
    private static final long serialVersionUID = 7010074665904863870L;
    
    private Map<String, Boolean> customerLevels;
    private Map<String, Integer> customerStates;
    private String creationDateRangeStart;
    private String creationDateRangeEnd;
    private String ethnicity;
    private String businessActivity;
    private Integer gender;
    private String citizenship;

    public SearchFiltersDto() {
        // Default empty constructor for hibernate
    }

    public SearchFiltersDto(Map<String, Boolean> customerLevels, Map<String, Integer> customerStates,
            String creationDateRangeStart, String creationDateRangeEnd, String ethnicity, String businessActivity,
            Integer gender, String citizenship) {
        super();
        this.customerLevels = customerLevels;
        this.customerStates = customerStates;
        this.creationDateRangeStart = creationDateRangeStart;
        this.creationDateRangeEnd = creationDateRangeEnd;
        this.ethnicity = ethnicity;
        this.businessActivity = businessActivity;
        this.gender = gender;
        this.citizenship = citizenship;
    }

    public Map<String, Boolean> getCustomerLevels() {
        return customerLevels;
    }

    public void setCustomerLevels(Map<String, Boolean> customerLevels) {
        this.customerLevels = customerLevels;
    }

    public Map<String, Integer> getCustomerStates() {
        return customerStates;
    }

    public void setCustomerStates(Map<String, Integer> customerStates) {
        this.customerStates = customerStates;
    }

    public String getCreationDateRangeStart() {
        return creationDateRangeStart;
    }

    public void setCreationDateRangeStart(String creationDateRangeStart) {
        this.creationDateRangeStart = creationDateRangeStart;
    }

    public String getCreationDateRangeEnd() {
        return creationDateRangeEnd;
    }

    public void setCreationDateRangeEnd(String creationDateRangeEnd) {
        this.creationDateRangeEnd = creationDateRangeEnd;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }
    
    
}
