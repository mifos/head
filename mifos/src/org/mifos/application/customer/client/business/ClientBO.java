package org.mifos.application.customer.client.business;

import java.io.InputStream;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerAddressDetailEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class ClientBO extends CustomerBO {

	private InputStream customerPicture;

	private Set<ClientNameDetailEntity> nameDetailSet;

	private Set<ClientAttendanceBO> clientAttendances;

	private Date dateOfBirth;

	private String governmentId;

	private ClientPerformanceHistoryEntity performanceHistory;

	private Short groupFlag;

	private ClientDetailEntity customerDetail;

	// TODO: removed searchId from parameter and generate internally
	protected ClientBO() {
		super();
		this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
		clientAttendances = new HashSet<ClientAttendanceBO>();
	}

	public ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus,
			CustomerAddressDetailEntity customerAddress,
			List<CustomerCustomFieldEntity> customFields, PersonnelBO formedBy, 
			OfficeBO office, CustomerBO parentCustomer, String searchId)
			throws CustomerException {
		this(userContext, displayName, customerStatus, customerAddress,
				customFields, formedBy, office, parentCustomer, null, null,
				searchId);
		clientAttendances = new HashSet<ClientAttendanceBO>();
	}

	public ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus,
			CustomerAddressDetailEntity customerAddress,
			List<CustomerCustomFieldEntity> customFields, PersonnelBO formedBy, 
			OfficeBO office, MeetingBO meeting, PersonnelBO personnel,
			String searchId) throws CustomerException {
		this(userContext, displayName, customerStatus, customerAddress,
				customFields, formedBy, office, null, meeting, personnel,
				searchId);
	}

	private ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus,
			CustomerAddressDetailEntity customerAddress,
			List<CustomerCustomFieldEntity> customFields, PersonnelBO formedBy,
			OfficeBO office, CustomerBO parentCustomer, MeetingBO meeting,
			PersonnelBO personnel, String searchId) throws CustomerException {
		super(userContext, displayName, CustomerLevel.CLIENT, customerStatus,
				customerAddress, customFields, formedBy, office,
				parentCustomer, meeting, personnel);
		this.setSearchId(searchId);
		if(customerStatus.equals(CustomerStatus.CLIENT_ACTIVE.getValue()))
			this.setCustomerActivationDate(this.getCreatedDate());
	}

	public Set<ClientNameDetailEntity> getNameDetailSet() {
		return nameDetailSet;
	}

	public void setNameDetailSet(
			Set<ClientNameDetailEntity> customerNameDetailSet) {
		if (customerNameDetailSet != null) {
			for (Object obj : customerNameDetailSet) {
				((ClientNameDetailEntity) obj).setClient(this);
			}
		}
		this.nameDetailSet = customerNameDetailSet;
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

	public ClientPerformanceHistoryEntity getPerformanceHistory() {
		return performanceHistory;
	}

	public ClientDetailEntity getCustomerDetail() {
		return customerDetail;
	}

	public void setCustomerDetail(ClientDetailEntity customerDetail) {
		this.customerDetail = customerDetail;
	}

	public void setPerformanceHistory(
			ClientPerformanceHistoryEntity performanceHistory) {
		if (performanceHistory != null)
			performanceHistory.setClient(this);
		this.performanceHistory = performanceHistory;
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

	public boolean isClientUnderGroup() {
		return groupFlag.equals(YesNoFlag.YES.getValue());
	}
}
