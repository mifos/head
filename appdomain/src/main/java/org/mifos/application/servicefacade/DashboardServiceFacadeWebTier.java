package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.config.Localization;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.DashboardDetailDto;
import org.mifos.dto.domain.DashboardDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class DashboardServiceFacadeWebTier implements DashboardServiceFacade {

    private final CustomerDao customerDao;
    private final LoanDao loanDao;
    private final CenterServiceFacade centerServiceFacade;
    
    @Autowired
    private DashboardServiceFacadeWebTier(CustomerDao customerDao,LoanDao loanDao,CenterServiceFacade centerServiceFacade) {
        this.customerDao=customerDao;
        this.loanDao=loanDao;
        this.centerServiceFacade=centerServiceFacade;
    }
    
    @Override
    public String[] getHeaders(){
        String[] headers = new String[5];
        Locale locale = Locale.UK;
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Locale prefferedLocale = Localization.getInstance().getLocaleById(user.getPreferredLocaleId());
        if (prefferedLocale!=null){
            locale = prefferedLocale;
        }
        ApplicationContext context 
        = ApplicationContextHolder.getApplicationContext();
        headers[0] = context.getMessage("DashboardDetail.GlobalNumber", null, locale);
        headers[1] = context.getMessage("DashboardDetail.State", null, locale);
        headers[2] = context.getMessage("DashboardDetail.LoanOfficer", null, locale);
        headers[3] = context.getMessage("DashboardDetail.Balance", null, locale);
        headers[4] = context.getMessage("DashboardDetail.ClientName", null, locale);
        return headers;
    }
    
    @Override
    public List<DashboardDetailDto> getBorrowers(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findAllBorrowers());
        }
        else {
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findBorrowersUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getBorrowersGroup(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findAllBorrowersGroup());
        }
        else {
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findBorrowersGroupUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveClients(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findAllActiveClients());
        }
        else {
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findActiveClientsUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveGroups(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findAllActiveGroups());
        }
        else {
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findActiveGroupsUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveCenters(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = centerBOtoDashboardDetailDtos(customerDao.findAllActiveCenters());
        }
        else {
            detailDtoList = centerBOtoDashboardDetailDtos(customerDao.findActiveCentersUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getWaitingForApprovalLoans() {
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findAllLoansWaitingForApproval());
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansWaitingForApprovalUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }

    @Override
    public List<DashboardDetailDto> getLoansInArrears() {
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findAllBadStandingLoans());
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findBadStandingLoansUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getLoansToBePaidCurrentWeek(){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansToBePaidCurrentWeek());
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansToBePaidCurrentWeekUnderLoanOfficer(loanOfficerId));
        }
        return detailDtoList;
    }

    
    @Override
    public DashboardDto getDashboardDto() {
        DashboardDto dashboardDto = new DashboardDto();
        Short loanOfficerID = getLoanOfficerId();
        if (loanOfficerID == null){
            dashboardDto.setBorrowersCount(customerDao.countAllBorrowers());
            dashboardDto.setBorrowersGroupCount(customerDao.countAllBorrowersGroup());

            dashboardDto.setActiveClientsCount(customerDao.countOfActiveClients());
            dashboardDto.setActiveGroupsCount(customerDao.countOfActiveGroups());
            dashboardDto.setActiveCentersCount(customerDao.countOfActiveCenters());

            dashboardDto.setWaitingForApprovalLoansCount(loanDao.countAllLoansWaitingForApproval());
            dashboardDto.setLoansInArrearsCount(loanDao.countAllBadStandingLoans());
            dashboardDto.setLoansToBePaidCurrentWeek(loanDao.countLoansToBePaidCurrentWeek());
            
        }
        else {
            dashboardDto.setBorrowersCount(customerDao.countBorrowersUnderLoanOfficer(loanOfficerID));
            dashboardDto.setBorrowersGroupCount(customerDao.countBorrowersGroupUnderLoanOfficer(loanOfficerID));
            
            dashboardDto.setActiveClientsCount(customerDao.countOfActiveClientsUnderLoanOfficer(loanOfficerID));
            dashboardDto.setActiveGroupsCount(customerDao.countOfActiveGroupsUnderLoanOfficer(loanOfficerID));
            dashboardDto.setActiveCentersCount(customerDao.countOfActiveCentersUnderLoanOfficer(loanOfficerID));
            
            dashboardDto.setWaitingForApprovalLoansCount(loanDao.countLoansWaitingForApprovalUnderLoanOfficer(loanOfficerID));
            dashboardDto.setLoansInArrearsCount(loanDao.countBadStandingLoansUnderLoanOfficer(loanOfficerID));
            dashboardDto.setLoansToBePaidCurrentWeek(loanDao.countLoansToBePaidCurrentWeekUnderLoanOfficer(loanOfficerID));
        }
        return dashboardDto;
    }

    private List<DashboardDetailDto> clientBOtoDashboardDetailDtos(List<ClientBO> clientBOList){
        List<DashboardDetailDto> clientDtoList = new ArrayList<DashboardDetailDto>();
        DashboardDetailDto dto;
        for (ClientBO clientBO : clientBOList){
            dto = new DashboardDetailDto();
            dto.setGlobalNumber(clientBO.getGlobalCustNum());
            dto.setState(clientBO.getCustomerStatus().getDescription());
            dto.setLoanOfficer(clientBO.getPersonnel().getDisplayName());
            dto.setUrl("viewClientDetails.ftl?globalCustNum="+dto.getGlobalNumber());
            dto.setBalance(clientBO.getLoanBalance(Money.getDefaultCurrency()).toString());
            dto.setDisplayName(clientBO.getDisplayName());
            clientDtoList.add(dto);
        }
        return clientDtoList;
    }
    
    private List<DashboardDetailDto> groupBOtoDashboardDetailDtos(List<GroupBO> groupBOList){
        List<DashboardDetailDto> groupDtoList = new ArrayList<DashboardDetailDto>();
        DashboardDetailDto dto;
        for (GroupBO groupBO : groupBOList){
            dto = new DashboardDetailDto();
            dto.setGlobalNumber(groupBO.getGlobalCustNum());
            dto.setState(groupBO.getCustomerStatus().getDescription());
            dto.setUrl("viewGroupDetails.ftl?globalCustNum="+dto.getGlobalNumber());
            dto.setLoanOfficer(groupBO.getPersonnel().getDisplayName());
            dto.setBalance(groupBO.getLoanBalance(Money.getDefaultCurrency()).toString());
            dto.setDisplayName(groupBO.getDisplayName());
            groupDtoList.add(dto);
        }
        return groupDtoList;
    }
    
    private List<DashboardDetailDto> centerBOtoDashboardDetailDtos(List<CenterBO> centerBOList){
        List<DashboardDetailDto> groupDtoList = new ArrayList<DashboardDetailDto>();
        DashboardDetailDto dto;
        for (CenterBO centerBO : centerBOList){
            dto = new DashboardDetailDto();
            dto.setGlobalNumber(centerBO.getGlobalCustNum());
            dto.setState(centerBO.getCustomerStatus().getDescription());
            dto.setUrl("viewCenterDetails.ftl?globalCustNum="+dto.getGlobalNumber());
            dto.setLoanOfficer(centerBO.getPersonnel().getDisplayName());
            dto.setBalance(centerBO.getLoanBalance(Money.getDefaultCurrency()).toString());
            dto.setDisplayName(centerBO.getDisplayName());
            groupDtoList.add(dto);
        }
        return groupDtoList;
    }
    
    private List<DashboardDetailDto> loanBOtoDashboardDetailDtos(List<LoanBO> loanBOList){
        List<DashboardDetailDto> loanDtoList = new ArrayList<DashboardDetailDto>();
        DashboardDetailDto dto;
        for (LoanBO loanBO : loanBOList){
            dto = new DashboardDetailDto();
            dto.setGlobalNumber(loanBO.getGlobalAccountNum());
            dto.setUrl("viewLoanAccountDetails.ftl?globalAccountNum="+dto.getGlobalNumber());
            dto.setState(loanBO.getAccountState().getDescription());
            dto.setLoanOfficer(loanBO.getPersonnel().getDisplayName());
            dto.setBalance(loanBO.getLoanBalance().toString());
            dto.setDisplayName(loanBO.getCustomer().getDisplayName());
            loanDtoList.add(dto);
        }
        return loanDtoList;
    }
    
    private Short getLoanOfficerId(){
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Short uId = new Short((short) user.getUserId());
        UserDetailDto uDetails = centerServiceFacade.retrieveUsersDetails(uId);
        Short loanOfficerId = null;
        if (uDetails.isLoanOfficer()){
            loanOfficerId = uId;
        }
        return loanOfficerId;
    }
}
