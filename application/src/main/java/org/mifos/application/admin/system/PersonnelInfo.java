package org.mifos.application.admin.system;

import java.io.Serializable;

public class PersonnelInfo implements Serializable, Comparable<PersonnelInfo> {
    private String offices;
    private String names;
    private String activityTime;
    private String activityContext;

    public PersonnelInfo(String offices, String names, String activityTime, String activityContext) {
        this.offices = offices;
        this.names = names;
        this.activityTime = activityTime;
        this.activityContext = activityContext;
    }

    @Override
    public int compareTo(PersonnelInfo personnelInfo) {
        if (offices.compareTo(personnelInfo.getOffices()) != 0) {
            return offices.compareTo(personnelInfo.getOffices());
        }
        return names.compareTo(personnelInfo.getNames());
    }

    @Override
    public String toString() {
        return offices + names;
    }

    public String getOffices() {
        return offices;
    }

    public String getNames() {
        return names;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public String getActivityContext() {
        return activityContext;
    }
}
