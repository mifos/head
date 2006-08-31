package org.mifos.application.customer.client.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class ClientBO extends CustomerBO {

	private CustomerPictureEntity customerPicture;

	private Set<ClientNameDetailEntity> nameDetailSet;

	private Set<ClientAttendanceBO> clientAttendances;

	private Date dateOfBirth;

	private String governmentId;

	private final ClientPerformanceHistoryEntity performanceHistory;

	private Short groupFlag;
	
	private String firstName;
	
	private String lastName;
	
	private String secondLastName;
	
	private ClientDetailEntity customerDetail;
	
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	
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
		validateOffice(officeId);
		nameDetailSet = new HashSet<ClientNameDetailEntity>();
		clientAttendances = new HashSet<ClientAttendanceBO>();
		this.performanceHistory = new ClientPerformanceHistoryEntity(this);
		this.dateOfBirth = dateOfBirth;
		this.governmentId = governmentId;
		if(trained!=null)
			setTrained(trained);
		else
			setTrained(YesNoFlag.NO.getValue());
		setTrainedDate(trainedDate);
		this.groupFlag = groupFlag;
		this.firstName = clientNameDetailView.getFirstName();
		this.lastName = clientNameDetailView.getLastName();
		this.secondLastName = clientNameDetailView.getSecondLastName();
		this.addNameDetailSet(new ClientNameDetailEntity(this, null,
				clientNameDetailView));
		this.addNameDetailSet(new ClientNameDetailEntity(this, null,
				spouseNameDetailView));
		this.customerDetail = new ClientDetailEntity(this, clientDetailView);
		createPicture(picture);		

		validateForDuplicateNameOrGovtId(displayName, dateOfBirth, governmentId);

		if(isActive()){
			validateFieldsForActiveClient(loanOfficerId, meeting);
			this.setCustomerActivationDate(this.getCreatedDate());
		}
		
		if(parentCustomer!=null)
			checkIfClientStatusIsLower(getStatus().getValue(), parentCustomer.getStatus().getValue());
		generateSearchId(parentCustomer , officeId);
	}
	
	public Set<ClientNameDetailEntity> getNameDetailSet() {
		return nameDetailSet;
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
	
	public ClientDetailEntity getCustomerDetail() {
		return customerDetail;
	}

	public void setCustomerDetail(ClientDetailEntity customerDetail) {
		this.customerDetail = customerDetail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	@Override
	public ClientPerformanceHistoryEntity getPerformanceHistory() {
		return performanceHistory;
	}
	
	public void addClientAttendance(ClientAttendanceBO clientAttendance) {
		clientAttendance.setCustomer(this);
		clientAttendances.add(clientAttendance);
	}

	public void addNameDetailSet(ClientNameDetailEntity customerNameDetail) {
		this.nameDetailSet.add(customerNameDetail);
	}
	
	@Override
	public boolean isActive() {
		return getCustomerStatus().getId().equals(CustomerStatus.CLIENT_ACTIVE.getValue());
	}

	public boolean isClientUnderGroup() {
		return groupFlag.equals(YesNoFlag.YES.getValue());
	}
	
	public ClientAttendanceBO getClientAttendanceForMeeting(Date meetingDate) {
		for (ClientAttendanceBO clientAttendance : getClientAttendances()) {
			if (DateUtils.getDateWithoutTimeStamp(clientAttendance.getMeetingDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(meetingDate.getTime())) == 0)
				return clientAttendance;
		}
		return null;
	}

	public void handleAttendance(Date meetingDate, Short attendance)
			throws ServiceException, CustomerException {
		ClientAttendanceBO clientAttendance = getClientAttendanceForMeeting(meetingDate);
		if (clientAttendance == null) {
			clientAttendance = new ClientAttendanceBO();
			clientAttendance.setMeetingDate(meetingDate);
			addClientAttendance(clientAttendance);
		}
		clientAttendance.setAttendance(attendance);
		try {
			new CustomerPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		}
	}

	@Override
	public void changeStatus(Short newStatusId, Short flagId, String comment) throws CustomerException, AccountException{
		super.changeStatus(newStatusId, flagId, comment);		
		if(isClientUnderGroup() && (newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue()) ||newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue()))){
			getParentCustomer().resetPositionsAssignedToClient(this.getCustomerId());
			getParentCustomer().setUserContext(getUserContext());
			getParentCustomer().update();
			CustomerBO center = getParentCustomer().getParentCustomer();
			if(center!=null ){
				center.resetPositionsAssignedToClient(this.getCustomerId());
				center.setUserContext(getUserContext());
				center.update();
				center = null;
			}
		}
	}

	@Override
	protected void validateStatusChange(Short newStatusId) throws CustomerException{
		logger.debug("In ClientBO::validateStatusChange(), customerId: " + getCustomerId());
		
		checkIfClientStatusIsLower(newStatusId, getParentCustomer().getCustomerStatus().getId());
		
		if (newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue()))
			checkIfClientCanBeClosed();
		
		if(newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue()))
			checkIfClientCanBeActive(newStatusId);
		
		logger.debug("In ClientBO::validateStatusChange(), successfully validated status, customerId: " + getCustomerId());
	}
	

	@Override
	protected boolean checkNewStatusIsFirstTimeActive(Short oldStatus,
			Short newStatusId) {
		if ((oldStatus.equals(CustomerStatus.CLIENT_PARTIAL.getValue()) || oldStatus
				.equals(CustomerStatus.CLIENT_PENDING.getValue()))
				&& newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue())) {
			this.setCustomerActivationDate(new Date());
			return true;
		}
		return false;
	}

	@Override
	public void save() throws CustomerException {
		super.save();
		try {
			if(this.getParentCustomer() !=null)
				new CustomerPersistence().createOrUpdate(this.getParentCustomer());
		} catch (PersistenceException pe) {
			throw new CustomerException(
					CustomerConstants.CREATE_FAILED_EXCEPTION, pe);
		}
	}

	public void updatePersonalInfo() throws CustomerException {
		validateForDuplicateNameOrGovtId(getDisplayName(), this.dateOfBirth ,this.governmentId);
		super.update();
	}

	/*public boolean isDuplicacyCheckNeeded()throws CustomerException{
		
		String oldDisplayName = (String)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_CLIENT_NAME).getValue();
		Date oldDOB = (Date)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_DOB).getValue();
		String oldGovernmentId = (String)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_GOVT_ID).getValue();
		String name= getDisplayName();
		Date dateOfBirth = getDateOfBirth();
		String govtId = getGovernmentId();
		boolean returnValue = false;
		//if both values of govt id is null then check if either name or DOB has changed. If so retrun true
		if(ValidateMethods.isNullOrBlank(oldGovernmentId) && ValidateMethods.isNullOrBlank(govtId)){
			if(!oldDisplayName.equals(name) || !oldDOB.equals(dateOfBirth)){
				returnValue = true;
			}
		}
		//if old value of govt id is null and a value is now entered for govt id or if the values of govt id are not the same return true
		else if(
				( ValidateMethods.isNullOrBlank(oldGovernmentId) && !ValidateMethods.isNullOrBlank(govtId))
				||(!ValidateMethods.isNullOrBlank(govtId) && !ValidateMethods.isNullOrBlank(oldGovernmentId)&& !oldGovernmentId.equals(govtId))	
				||(!ValidateMethods.isNullOrBlank(oldGovernmentId) && ValidateMethods.isNullOrBlank(govtId)))
		{
				returnValue =  true;
		}
		//if the values are the same return false
		else if( !ValidateMethods.isNullOrBlank(govtId) && !ValidateMethods.isNullOrBlank(oldGovernmentId)&& oldGovernmentId.equals(govtId))	
		{
			returnValue =  false;
		}
		return returnValue ;
	}*/


	public void transferToBranch(OfficeBO officeToTransfer)throws CustomerException{
		validateBranchTransfer(officeToTransfer);
		logger.debug("In ClientBO::transferToBranch(), transfering customerId: " + getCustomerId() +  "to branch : "+ officeToTransfer.getOfficeId());
		if(isActive())
			setCustomerStatus(new CustomerStatusEntity(CustomerStatus.CLIENT_HOLD));
		
		makeCustomerMovementEntries(officeToTransfer);		
		this.setPersonnel(null);
		generateSearchId(null,officeToTransfer.getOfficeId());
		super.update();
		logger.debug("In ClientBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());  
	}
	
	public void transferToGroup(GroupBO newParent)throws CustomerException{
		validateGroupTransfer(newParent);
		logger.debug("In ClientBO::transferToGroup(), transfering customerId: " + getCustomerId() +  "to Group Id : "+ newParent.getCustomerId());

		if(!isSameBranch(newParent.getOffice()))
			makeCustomerMovementEntries(newParent.getOffice());
		
		CustomerBO oldParent = getParentCustomer();
		changeGroup(newParent);
		
		CustomerHierarchyEntity currentHierarchy = getActiveCustomerHierarchy();
		currentHierarchy.makeInActive(userContext.getId());
		
		this.addCustomerHierarchy(new CustomerHierarchyEntity(this,newParent));
		oldParent.resetPositionsAssignedToClient(this.getCustomerId());
		
		if(oldParent.getParentCustomer()!=null){
			CustomerBO center = oldParent.getParentCustomer();
			center.resetPositionsAssignedToClient(this.getCustomerId());
			center.setUserContext(getUserContext());
			center.update();
		}
		oldParent.setUserContext(getUserContext());
		oldParent.update();
		newParent.update();
		this.update();		
		logger.debug("In ClientBO::transferToGroup(), successfully transfered, customerId :" + getCustomerId());
	}	
	
	public ClientNameDetailEntity getClientName(){
		for(ClientNameDetailEntity nameDetail : nameDetailSet){
			if(nameDetail.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)){
				return nameDetail;
			}
		}
		return null;
	}
	
	public ClientNameDetailEntity getSpouseName(){
		for(ClientNameDetailEntity nameDetail : nameDetailSet){
			if(!(nameDetail.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE))){
				return nameDetail;
			}
		}
		return null;
	}

	public void updateClientDetails(ClientDetailView clientDetailView) {
		customerDetail.updateClientDetails(clientDetailView);
		
	}

	public void updatePicture(InputStream picture) throws CustomerException{
		ClientPersistence clientPersistence = new ClientPersistence();
		if(customerPicture != null)
				customerPicture.setPicture(clientPersistence.createBlob(picture));	
		else
			this.customerPicture = new CustomerPictureEntity(this,clientPersistence.createBlob(picture));
			
	}
	
	private void createPicture(InputStream picture)throws CustomerException{
		try {
			if (picture !=null && picture.available() > 0)
				this.customerPicture = new CustomerPictureEntity(this,new ClientPersistence().createBlob(picture));
						
		} catch (IOException e) {
			throw new CustomerException(e);
		}
	}
	
	private void validateForActiveAccounts()throws CustomerException{
		if(isAnyLoanAccountOpen() || isAnySavingsAccountOpen()){
			ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
			try{
				Object[] args = new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP, userContext.getPereferedLocale())};
				throw new CustomerException(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT, args);
			}catch(ConfigurationException ce){
				new CustomerException(ce);
			}	
		}
	}
	
	private void validateBranchTransfer(OfficeBO officeToTransfer)throws CustomerException{
		if (officeToTransfer == null)
			throw new CustomerException(CustomerConstants.INVALID_OFFICE);
		
		if(isSameBranch(officeToTransfer))
			throw new CustomerException(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER);
	}
	
	private boolean isSameGroup(GroupBO group){
		return getParentCustomer().getCustomerId().equals(group.getCustomerId());
	}
	
	private void makeCustomerMovementEntries(OfficeBO officeToTransfer){
		CustomerMovementEntity currentCustomerMovement = getActiveCustomerMovement();
		if(currentCustomerMovement == null){
			currentCustomerMovement = new CustomerMovementEntity(this, getCreatedDate());
			this.addCustomerMovement(currentCustomerMovement);
		}
		
		currentCustomerMovement.makeInActive(userContext.getId());
		this.setOffice(officeToTransfer);
		CustomerMovementEntity newCustomerMovement = new CustomerMovementEntity(this, new Date());
		this.addCustomerMovement(newCustomerMovement);
	}
	
	private void validateGroupTransfer(GroupBO toGroup)throws CustomerException{
		if (toGroup == null)
			throw new CustomerException(CustomerConstants.INVALID_PARENT);
		
		if(isSameGroup(toGroup))
			throw new CustomerException(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER);
		
		validateForGroupStatus(toGroup.getStatus());
		validateForActiveAccounts();		
	}
	
	private void validateForGroupStatus(CustomerStatus groupStatus)throws CustomerException{
		if(isGroupStatusLower(getStatus(),groupStatus)){
			ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
			try{
				Object[] args = new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP, userContext.getPereferedLocale()),
						labelConfig.getLabel(ConfigurationConstants.CLIENT, userContext.getPereferedLocale())};
				throw new CustomerException(ClientConstants.ERRORS_LOWER_GROUP_STATUS, args);
			}catch(ConfigurationException ce){
				new CustomerException(ce);
			}	
		}
	}
	
	private void generateSearchId(CustomerBO parentCustomer , Short officeId) throws CustomerException{
		int count;
		if (parentCustomer != null) {
			this.setSearchId(parentCustomer.getSearchId()+ "."+ String.valueOf(parentCustomer.getMaxChildCount() + 1));
			parentCustomer.incrementMaxChildCount();
		}
		else{
			try { 
			count = new CustomerPersistence().getCustomerCountForOffice(CustomerLevel.CLIENT, officeId);
			} catch (PersistenceException pe) {
				throw new CustomerException(pe);
			} 
			String searchId=GroupConstants.PREFIX_SEARCH_STRING + ++count;
			this.setSearchId(searchId);
		  }
	}
	
	private void validateForDuplicateNameOrGovtId(String displayName, Date dateOfBirth, String governmentId)
	throws CustomerException {
		Integer custId = null;
		if(getCustomerId() == null)
			custId = Integer.valueOf("0");
		else
			custId = getCustomerId();
		
		checkForDuplicacy(displayName , dateOfBirth , governmentId , custId);
		
	}
	
	private void validateFieldsForActiveClient(Short loanOfficerId, MeetingBO meeting)throws CustomerException{
		if (isActive()){
			if(!isClientUnderGroup()){
				validateLO(loanOfficerId);
				validateMeeting(meeting);
			}			
		}
	}
	
	private void checkForDuplicacy(String name, Date dob, String governmentId , Integer customerId)throws CustomerException{
		ClientPersistence clientPersistence = new ClientPersistence();
		
			if(!StringUtils.isNullOrEmpty(governmentId)){
				try{
					if(clientPersistence.checkForDuplicacyOnGovtId(governmentId , customerId) == true){
						Object[] values = new Object[2];
						values[0] = governmentId;
						values[1]=MifosConfiguration.getInstance().getLabel(ConfigurationConstants.GOVERNMENT_ID,userContext.getPereferedLocale());
						throw new CustomerException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION,values);
					}
				}
				catch(ConfigurationException ce){
					throw new CustomerException();
				}
			}
			else{
				if(clientPersistence.checkForDuplicacyOnName(name,dob,customerId) == true){
					Object[] values = new Object[1];
					values[0] = name;
					throw new CustomerException(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,values);
				}
			}
		
	}
	
	private boolean isGroupStatusLower(CustomerStatus clientStatus, CustomerStatus groupStatus ){
		if(clientStatus.equals(CustomerStatus.CLIENT_PENDING)){
			if(groupStatus.equals(CustomerStatus.GROUP_PARTIAL))
				return true;
		}
		else if(clientStatus.equals(CustomerStatus.CLIENT_ACTIVE)){
			if(groupStatus.equals(CustomerStatus.GROUP_PARTIAL) || 
					groupStatus.equals(CustomerStatus.GROUP_PENDING)){
				return true;
			}
		}
		return false;
	}
	
	private void checkIfClientCanBeClosed()throws CustomerException{
		if (getActiveAndApprovedLoanAccounts(new java.util.Date()).size() > 0
				|| getActiveSavingsAccounts().size() > 0) {
			throw new CustomerException(
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
		}
	}
	
	private void checkIfClientStatusIsLower(Short clientStatusId, Short groupStatus)throws CustomerException{
		if ((clientStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue()) || clientStatusId
				.equals(CustomerStatus.CLIENT_PENDING.getValue()))
				&& this.isClientUnderGroup()) {
			if (isGroupStatusLower(clientStatusId, groupStatus)) {
				try {
					throw new CustomerException(
							ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,
							new Object[] {
									MifosConfiguration.getInstance().getLabel(
											ConfigurationConstants.GROUP,
											this.getUserContext().getPereferedLocale()),
											MifosConfiguration.getInstance().getLabel(
											ConfigurationConstants.CLIENT,
											this.getUserContext().getPereferedLocale()) });
				}catch (ConfigurationException ce) {
					throw new CustomerException(ce);
				}
			}
		}
	}
	
	private void checkIfClientCanBeActive(Short newStatus) throws CustomerException{
		boolean loanOfficerActive = false;
		boolean branchInactive = false;
		short officeId = getOffice().getOfficeId();
			if(getPersonnel()==null || getPersonnel().getPersonnelId()==null){
				throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
			}
			if(getCustomerMeeting()==null||getCustomerMeeting().getMeeting()==null){
				throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
			}
			if( getPersonnel() !=null){
				loanOfficerActive =new OfficePersistence().hasActivePeronnel(officeId);
			}
			 
			branchInactive = new OfficePersistence().isBranchInactive(officeId);
			if(loanOfficerActive ==false){
				try {
					throw new CustomerException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{MifosConfiguration.getInstance().getLabel(ConfigurationConstants.BRANCHOFFICE,getUserContext().getPereferedLocale())});
				}catch (ConfigurationException ce) {
					throw new CustomerException(ce);
				}
								
			}
			if(branchInactive ==true){
				try {
					throw new CustomerException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{MifosConfiguration.getInstance().getLabel(ConfigurationConstants.BRANCHOFFICE,getUserContext().getPereferedLocale())});
				} catch (ConfigurationException ce) {
					throw new CustomerException(ce);
				}
				
			}
	}
	
	private boolean isGroupStatusLower(Short clientStatusId, Short parentStatus){
		return isGroupStatusLower(CustomerStatus.getStatus(clientStatusId), CustomerStatus.getStatus(parentStatus));			
	}
	
	private void changeGroup(CustomerBO newParent){
		CustomerBO oldParent = getParentCustomer();
		setPersonnel(newParent.getPersonnel());
		setParentCustomer(newParent);
		if(newParent.getCustomerMeeting()!=null){
			if(getCustomerMeeting()!=null)
				getCustomerMeeting().setMeeting(newParent.getCustomerMeeting().getMeeting());
			else
				setCustomerMeeting(createCustomerMeeting(newParent.getCustomerMeeting().getMeeting()));
			getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		}
		else{
			if(getCustomerMeeting()!=null)
				new CustomerPersistence().deleteMeeting(this);
		}
		
		oldParent.decrementMaxChildCount();
		newParent.incrementMaxChildCount();
		setSearchId(newParent.getSearchId()+ "."+ String.valueOf(newParent.getMaxChildCount()));		
	}

}
