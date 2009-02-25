package org.mifos.application.customer.client.business.service;

import java.util.HashMap;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;

public class InMemoryClientAttendanceDao implements ClientAttendanceDao {

    private HashMap<String, AttendanceType> attendance = new HashMap<String, AttendanceType>();
    private HashMap<String, LocalDate> meetingDate = new HashMap<String, LocalDate>();

    @Override
    public AttendanceType getAttendance(Integer clientId, LocalDate meetingDate) {
        return attendance.get(getKey(clientId, meetingDate));
    }

    @Override
    public void setAttendance(Integer clientId, LocalDate meetingDate, AttendanceType attendance) {
        String key = getKey(clientId, meetingDate);
        this.attendance.put(key, attendance);
        this.meetingDate.put(key, meetingDate);
    }

    private String getKey(Integer clientId, LocalDate meetingDate) {
        return clientId.toString() + "-" + meetingDate.toString();
    }
    
    public int getNumberOfRecords() {
        return attendance.size();
    }

}
