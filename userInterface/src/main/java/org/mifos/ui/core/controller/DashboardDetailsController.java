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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardDetailsController {

    @Autowired
    private DashboardServiceFacade dashboardServiceFacade;
    
    @RequestMapping(value = "/viewWaitingForApprovalLoansDBDetails", method=RequestMethod.GET)
    public ModelAndView showWaitingForApprovalLoansDBDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getWaitingForApprovalLoans();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewBadStandingLoansDBDetails", method=RequestMethod.GET)
    public ModelAndView showBadStandingLoansDBDetails(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getLoansInArrears();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewLoansToBePaidCurrWeekDBDetails", method=RequestMethod.GET)
    public ModelAndView showLoansToBePaidCurrWeek(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getLoanModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getLoansToBePaidCurrentWeek();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewTotalBorrowersDBDetails", method=RequestMethod.GET)
    public ModelAndView showTotalBorowers(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getBorrowers();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewTotalBorrowersGroupDBDetails", method=RequestMethod.GET)
    public ModelAndView showTotalBorowersGroup(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getBorrowersGroup();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveClientsDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveClients(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveClients();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveGroupsDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveGroups(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveGroups();
        modelAndView.addObject("dashboardDetails", loans);
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewActiveCentersDBDetails", method=RequestMethod.GET)
    public ModelAndView showActiveCenters(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getCustomerModelAndView();
        List<DashboardDetailDto> loans = (List<DashboardDetailDto>) dashboardServiceFacade.getActiveCenters();
        modelAndView.addObject("dashboardDetails", loans);
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
