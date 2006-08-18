package org.mifos.application.customer.client.business;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.Status;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class ClientBO extends CustomerBO {

	private CustomerPictureEntity customerPicture;

	private Set<ClientNameDetailEntity> nameDetailSet;

	private Set<ClientAttendanceBO> clientAttendances;

	private Date dateOfBirth;

	private String governmentId;

	private final ClientPerformanceHistoryEntity performanceHistory;

	private Short groupFlag;

	private ClientDetailEntity customerDetail;

	protected ClientBO() {
		super();
		this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
		clientAttendances = new HashSet<ClientAttendanceBO>();
		this.performanceHistory = null;
	}

	public ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId,
			Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, CustomerBO parentCustomer,
			Date dateOfBirth, String governmentId, Short trained,
			Date trainedDate, Short groupFlag,
			ClientNameDetailView clientNameDetailView,
			ClientNameDetailView spouseNameDetailView,
			ClientDetailView clientDetailView, InputStream picture)
			throws CustomerException {
		this(userContext, displayName, customerStatus, externalId,
				mfiJoiningDate, address, customFields, fees, formedById,
				officeId, parentCustomer, null, null, dateOfBirth,
				governmentId, trained, trainedDate, groupFlag,
				clientNameDetailView, spouseNameDetailView, clientDetailView,
				picture);
		clientAttendances = new HashSet<ClientAttendanceBO>();
	}

	public ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId,
			Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, MeetingBO meeting,
			Short loanOfficerId, Date dateOfBirth, String governmentId,
			Short trained, Date trainedDate, Short groupFlag,
			ClientNameDetailView clientNameDetailView,
			ClientNameDetailView spouseNameDetailView,
			ClientDetailView clientDetailView, InputStream picture)
			throws CustomerException {
		this(userContext, displayName, customerStatus, externalId,
				mfiJoiningDate, address, customFields, fees, formedById,
				officeId, null, meeting, loanOfficerId, dateOfBirth,
				governmentId, trained, trainedDate, groupFlag,
				clientNameDetailView, spouseNameDetailView, clientDetailView,
				picture);
	}

	private ClientBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId,
			Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, CustomerBO parentCustomer,
			MeetingBO meeting, Short loanOfficerId, Date dateOfBirth,
			String governmentId, Short trained, Date trainedDate,
			Short groupFlag, ClientNameDetailView clientNameDetailView,
			ClientNameDetailView spouseNameDetailView,
			ClientDetailView clientDetailView, InputStream picture)
			throws CustomerException {
		super(userContext, displayName, CustomerLevel.CLIENT, customerStatus,
				externalId, mfiJoiningDate, address, customFields, fees,
				formedById, officeId, parentCustomer, meeting, loanOfficerId);
		nameDetailSet = new HashSet<ClientNameDetailEntity>();
		if (customerStatus.equals(CustomerStatus.CLIENT_ACTIVE.getValue()))
			this.setCustomerActivationDate(this.getCreatedDate());
		this.performanceHistory = new ClientPerformanceHistoryEntity(this);
		this.dateOfBirth = dateOfBirth;
		this.governmentId = governmentId;
		this.trained = trained;
		this.trainedDate = trainedDate;
		this.groupFlag = groupFlag;
		this.addNameDetailSet(new ClientNameDetailEntity(this, null,
				clientNameDetailView));
		this.addNameDetailSet(new ClientNameDetailEntity(this, null,
				spouseNameDetailView));
		this.customerDetail = new ClientDetailEntity(this, clientDetailView);
		try {
			if (picture != null)
				this.customerPicture = new CustomerPictureEntity(this,
						Hibernate.createBlob(picture));
		} catch (IOException e) {
			throw new CustomerException(e);
		}

		if (parentCustomer != null) {
			CustomerHierarchyEntity customerHierarchyEntity = new CustomerHierarchyEntity(
					this, parentCustomer, Status.ACTIVE);
			this.addCustomerHierarchy(customerHierarchyEntity);
		}
	}

	public Set<ClientNameDetailEntity> getNameDetailSet() {
		return nameDetailSet;
	}

	private void setNameDetailSet(
			Set<ClientNameDetailEntity> customerNameDetailSet) {
		this.nameDetailSet = customerNameDetailSet;
	}

	public void addNameDetailSet(ClientNameDetailEntity customerNameDetail) {
		this.nameDetailSet.add(customerNameDetail);
	}

	public CustomerPictureEntity getCustomerPicture() {
		return customerPicture;
	}

	public void setCustomerPicture(CustomerPictureEntity customerPicture) {
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
		new CustomerPersistence().createOrUpdate(this);
	}

	public boolean isCustomerActive() {
		if (getCustomerStatus().getId().equals(ClientConstants.STATUS_ACTIVE))
			return true;
		return false;
	}

	public boolean isClientUnderGroup() {
		return groupFlag.equals(YesNoFlag.YES.getValue());
	}

	@Override
	protected void validateStatusChange(Short newStatusId)
			throws ApplicationException, SystemException {

	}
}
