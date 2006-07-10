package org.mifos.application.customer.client.business;

import java.io.InputStream;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerNameDetailEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

/**
 * This class acts as valueObject for clients module
 * 
 * @author ashishsm
 * 
 */
public class ClientBO extends CustomerBO {

	private InputStream customerPicture;

	private Set customerNameDetailSet;

	private Set<ClientAttendanceBO> clientAttendances;

	private Date dateOfBirth;

	private String governmentId;

	private ClientPerformanceHistoryEntity clientPerformanceHistory;

	public ClientBO() {
		this.customerNameDetailSet = new HashSet();
		clientAttendances = new HashSet<ClientAttendanceBO>();
	}

	public ClientBO(UserContext userContext) {
		super(userContext);
	}

	public Set getCustomerNameDetailSet() {
		return customerNameDetailSet;
	}

	public void setCustomerNameDetailSet(Set customerNameDetailSet) {
		if (customerNameDetailSet != null) {
			for (Object obj : customerNameDetailSet) {
				((CustomerNameDetailEntity) obj).setCustomer(this);
			}
		}
		this.customerNameDetailSet = customerNameDetailSet;
	}

	public InputStream getCustomerPicture() {
		return customerPicture;
	}

	public void setCustomerPicture(InputStream customerPicture) {
		this.customerPicture = customerPicture;
	}

	public Set<ClientAttendanceBO> getClientAttendances() {
		return clientAttendances;
	}

	private void setClientAttendances(Set<ClientAttendanceBO> clientAttendances) {
		this.clientAttendances = clientAttendances;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}

	public ClientPerformanceHistoryEntity getClientPerformanceHistory() {
		return clientPerformanceHistory;
	}

	public void setClientPerformanceHistory(
			ClientPerformanceHistoryEntity clientPerformanceHistory) {
		this.clientPerformanceHistory = clientPerformanceHistory;
	}

	public void addClientAttendance(ClientAttendanceBO clientAttendance) {
		clientAttendance.setCustomer(this);
		clientAttendances.add(clientAttendance);
	}

	public ClientAttendanceBO getClientAttendanceForMeeting(Date meetingDate) {
		for (ClientAttendanceBO clientAttendance : getClientAttendances()) {
			if (clientAttendance.getMeetingDate().compareTo(meetingDate) == 0)
				return clientAttendance;
		}
		return null;
	}

	public void handleAttendance(Date meetingDate, Short attendance)
			throws ServiceException {
		ClientAttendanceBO clientAttendance = getClientAttendanceForMeeting(meetingDate);
		if (clientAttendance == null) {
			clientAttendance = new ClientAttendanceBO();
			clientAttendance.setMeetingDate(meetingDate);
			addClientAttendance(clientAttendance);
		}
		clientAttendance.setAttendance(attendance);
		getDBService().update(this);
	}

	public boolean isCustomerActive() {
		if (getCustomerStatus().getStatusId().equals(
				ClientConstants.STATUS_ACTIVE))
			return true;
		return false;
	}
}
