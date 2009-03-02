package org.mifos.application.customer.client.business.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.framework.exceptions.PersistenceException;

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

    @Override
    public List<ClientAttendanceDto> getClientAttendance(Date meetingDate, Short officeId) throws PersistenceException {
        return null;
    }

    private String getKey(Integer clientId, LocalDate meetingDate) {
        return clientId.toString() + "-" + meetingDate.toString();
    }
    
    public int getNumberOfRecords() {
        return attendance.size();
    }

}
