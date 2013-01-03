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
    public String[] getLoanHeaders(){
        String[] headers = new String[5];
        Locale locale = getLocale();
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
        headers[3] = context.getMessage("DashboardDetail.LoanBalance", null, locale);
        headers[4] = context.getMessage("DashboardDetail.ClientName", null, locale);
        return headers;
    }
    
    @Override
    public String[] getCustomerHeaders(){
        String[] headers = new String[5];
        Locale locale = getLocale();
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Locale prefferedLocale = Localization.getInstance().getLocaleById(user.getPreferredLocaleId());
        if (prefferedLocale!=null){
            locale = prefferedLocale;
        }
        ApplicationContext context 
        = ApplicationContextHolder.getApplicationContext();
        headers[0] = context.getMessage("DashboardDetail.GlobalNumber", null, locale);
        headers[1] = context.getMessage("DashboardDetail.ClientName", null, locale);
        headers[2] = context.getMessage("DashboardDetail.State", null, locale);
        headers[3] = context.getMessage("DashboardDetail.LoanOfficer", null, locale);
        headers[4] = context.getMessage("DashboardDetail.LoanBalance", null, locale);
        return headers;
    }
    
    private Locale getLocale(){
        Locale locale = Locale.UK;
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Locale prefferedLocale = Localization.getInstance().getLocaleById(user.getPreferredLocaleId());
        if (prefferedLocale!=null){
            locale = prefferedLocale;
        }
        return locale;
    }
    
    @Override
    public List<DashboardDetailDto> getBorrowers(int position, int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findAllBorrowers(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findBorrowersUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getBorrowersGroup(int position,int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findAllBorrowersGroup(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findBorrowersGroupUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveClients(int position,int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findAllActiveClients(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = clientBOtoDashboardDetailDtos(customerDao.findActiveClientsUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveGroups(int position,int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findAllActiveGroups(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = groupBOtoDashboardDetailDtos(customerDao.findActiveGroupsUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getActiveCenters(int position,int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = centerBOtoDashboardDetailDtos(customerDao.findAllActiveCenters(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = centerBOtoDashboardDetailDtos(customerDao.findActiveCentersUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getWaitingForApprovalLoans(int position,int noOfObjects,String ordering) {
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findAllLoansWaitingForApproval(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansWaitingForApprovalUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }

    @Override
    public List<DashboardDetailDto> getLoansInArrears(int position,int noOfObjects,String ordering) {
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findAllBadStandingLoans(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findBadStandingLoansUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
        }
        return detailDtoList;
    }
    
    @Override
    public List<DashboardDetailDto> getLoansToBePaidCurrentWeek(int position,int noOfObjects,String ordering){
        List<DashboardDetailDto> detailDtoList;
        Short loanOfficerId = getLoanOfficerId();
        if (loanOfficerId == null){
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansToBePaidCurrentWeek(position,noOfObjects,ordering));
        }
        else {
            detailDtoList = loanBOtoDashboardDetailDtos(loanDao.findLoansToBePaidCurrentWeekUnderLoanOfficer(position,noOfObjects,loanOfficerId,ordering));
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
            dto.setState(clientBO.getCustomerStatus().getName());
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
            dto.setState(groupBO.getCustomerStatus().getName());
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
            dto.setState(centerBO.getCustomerStatus().getName());
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
            dto.setBalance(loanBO.getLoanSummary().getTotalAmntDue().toString());
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

    @Override
    public int countBorrowers() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=customerDao.countAllBorrowers();
        }
        else {
            val=customerDao.countBorrowersUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countBorrowersGroup() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=customerDao.countAllBorrowersGroup();
        }
        else {
            val=customerDao.countBorrowersGroupUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countOfActiveClients() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=customerDao.countOfActiveClients();
        }
        else {
            val=customerDao.countOfActiveClientsUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countOfActiveGroups() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=customerDao.countOfActiveGroups();
        }
        else {
            val=customerDao.countOfActiveGroupsUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countOfActiveCenters() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=customerDao.countOfActiveCenters();
        }
        else {
            val=customerDao.countOfActiveCentersUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countLoansWaitingForApproval() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=loanDao.countAllLoansWaitingForApproval();
        }
        else {
            val=loanDao.countLoansWaitingForApprovalUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countBadStandingLoans() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=loanDao.countAllBadStandingLoans();
        }
        else {
            val=loanDao.countBadStandingLoansUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }

    @Override
    public int countLoansToBePaidCurrentWeek() {
        Short loanOfficerId = getLoanOfficerId();
        int val=-1;
        if (loanOfficerId == null){
            val=loanDao.countLoansToBePaidCurrentWeek();
        }
        else {
            val=loanDao.countLoansToBePaidCurrentWeekUnderLoanOfficer(loanOfficerId);
        }
        return val;
    }
}
