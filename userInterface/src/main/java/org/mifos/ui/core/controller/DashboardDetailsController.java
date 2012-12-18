package org.mifos.ui.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.servicefacade.DashboardServiceFacade;
import org.mifos.dto.domain.DashboardDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardDetailsController {

    @Autowired
    private DashboardServiceFacade dashboardServiceFacade;
    
    @RequestMapping(value = "/viewWaitingForApprovalLoansDBDetails", method=RequestMethod.GET)
    public ModelAndView showWaitingForApprovalLoansDBDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getWaitingForApprovalLoans(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countLoansWaitingForApproval());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","waitingForApprovalLoansDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewBadStandingLoansDBDetails", method=RequestMethod.GET)
    public ModelAndView showBadStandingLoansDBDetails(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getLoansInArrears(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBadStandingLoans());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","badStandingLoansDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewLoansToBePaidCurrWeekDBDetails", method=RequestMethod.GET)
    public ModelAndView showLoansToBePaidCurrWeek(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getLoansToBePaidCurrentWeek(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countLoansToBePaidCurrentWeek());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","loansToBePaidCurrWeekDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewTotalBorrowersDBDetails", method=RequestMethod.GET)
    public ModelAndView showTotalBorowers(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getBorrowers(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBorrowers());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","totalBorrowersDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewTotalBorrowersGroupDBDetails", method=RequestMethod.GET)
    public ModelAndView showTotalBorowersGroup(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getBorrowersGroup(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBorrowersGroup());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","totalBorrowersGroupDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveClientsDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveClients(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveClients(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveClients());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","activeClientsDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveGroupsDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveGroups(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveGroups(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveGroups());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","activeGroupsDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveCentersDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveCenters(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveCenters(0,10);
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveCenters());
        modelAndView.addObject("dashboardDetails", loans);
        modelAndView.addObject("ajaxUrl","activeCentersDBDetailsAjax.ftl");
        return modelAndView;
    }
    
    @RequestMapping(value = "/badStandingLoansDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getBadStandingLoansDBDetails(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxLoanModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getLoansInArrears(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBadStandingLoans());
        return modelAndView;
    }
    
    @RequestMapping(value = "/loansToBePaidCurrWeekDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getLoansToBePaidCurrWeek(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxLoanModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getLoansToBePaidCurrentWeek(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countLoansToBePaidCurrentWeek());
        return modelAndView;
    }
    
    @RequestMapping(value = "/waitingForApprovalLoansDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getWaitingForApprovalLoansDBDetails(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxLoanModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getWaitingForApprovalLoans(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countLoansWaitingForApproval());
        return modelAndView;
    }
    
    @RequestMapping(value = "/activeCentersDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getActiveCentersAjax(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxCustomerModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getActiveCenters(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveCenters());
        return modelAndView;
    }
    
    @RequestMapping(value = "/activeClientsDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getActiveClientsAjax(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxCustomerModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getActiveClients(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveClients());
        return modelAndView;
    }
    
    @RequestMapping(value = "/activeGroupsDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getActiveGroupsAjax(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxCustomerModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getActiveGroups(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countOfActiveGroups());
        return modelAndView;
    }
    
    @RequestMapping(value = "/totalBorrowersGroupDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getBorrowersGroupAjax(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxCustomerModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getBorrowersGroup(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBorrowersGroup());
        return modelAndView;
    }
    
    @RequestMapping(value = "/totalBorrowersDBDetailsAjax", method = RequestMethod.GET )
    public ModelAndView getBorrowersAjax(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required=false) String sEcho, @RequestParam Integer iDisplayStart, @RequestParam Integer iDisplayLength){
        ModelAndView modelAndView = getAjaxCustomerModelAndView(sEcho,iDisplayStart,iDisplayLength);
        modelAndView.addObject("dashboardDetails", dashboardServiceFacade.getBorrowers(iDisplayStart,iDisplayLength));
        modelAndView.addObject("totalSize",dashboardServiceFacade.countBorrowers());
        return modelAndView;
    }
    
    private ModelAndView getAjaxLoanModelAndView(String sEcho,Integer iDisplayStart,Integer iDisplayLength){
        ModelAndView modelAndView = getLoanModelAndView();
        modelAndView.setViewName("viewDashboardDetailsAjax");
        if ( sEcho != null ){
            modelAndView.addObject("sEcho", sEcho);
        }
        modelAndView.addObject("iDisplayStart", iDisplayStart);
        modelAndView.addObject("iDisplayLength", iDisplayLength);
        modelAndView.addObject("iLength",iDisplayLength);
        return modelAndView;
    }
    
    private ModelAndView getAjaxCustomerModelAndView(String sEcho,Integer iDisplayStart,Integer iDisplayLength){
        ModelAndView modelAndView = getCustomerModelAndView();
        modelAndView.setViewName("viewDashboardDetailsAjax");
        modelAndView.setViewName("viewDashboardDetailsAjax");
        if ( sEcho != null ){
            modelAndView.addObject("sEcho", sEcho);
        }
        modelAndView.addObject("iDisplayStart", iDisplayStart);
        modelAndView.addObject("iDisplayLength", iDisplayLength);
        modelAndView.addObject("iLength",iDisplayLength);
        return modelAndView;
    }
    
    private ModelAndView getLoanModelAndView(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("viewDashboardDetails");
        String[] tableHeaders = dashboardServiceFacade.getLoanHeaders();
        modelAndView.addObject("tableHeaders",tableHeaders);
        modelAndView.addObject("type",'l');
        return modelAndView;
    }
    
    private ModelAndView getCustomerModelAndView(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("viewDashboardDetails");
        String[] tableHeaders = dashboardServiceFacade.getCustomerHeaders();
        modelAndView.addObject("tableHeaders",tableHeaders);
        modelAndView.addObject("type",'c');
        return modelAndView;
    }
}
