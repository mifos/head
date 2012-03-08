package org.mifos.ui.core.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.mifos.ui.core.controller.util.helpers.UrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.servlet.IncludePage;

@Controller
public class ViewLoanAccountDetailsController {

    @Autowired
    private LoanAccountServiceFacade loanAccountServiceFacade;
    
    @Autowired
    private CenterServiceFacade centerServiceFacade;
    
    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;
    
    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    
    private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
    
    @RequestMapping(value = "/viewLoanAccountDetails", method=RequestMethod.GET)
    public ModelAndView showLoanAccountDetails(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewLoanAccountDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        LoanInformationDto loanInformationDto = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        modelAndView.addObject("loanInformationDto", loanInformationDto);
        
        boolean containsQGForCloseLoan = questionnaireServiceFacade.getQuestionGroupInstances(loanInformationDto.getAccountId(), "Close", "Loan").size() > 0;
        modelAndView.addObject("containsQGForCloseLoan", containsQGForCloseLoan);
        
        // for mifostabletag
        List<LoanActivityDto> activities = loanInformationDto.getRecentAccountActivity();
        for (LoanActivityDto activity : activities) {
            activity.setUserPrefferedDate(DateFormatUtils.format(activity.getActionDate(), "dd/MM/yyyy", personnelServiceFacade.getUserPreferredLocale()));
        }
        request.getSession().setAttribute("recentAccountActivities", loanInformationDto.getRecentAccountActivity());

        modelAndView.addObject("backPageUrl", UrlHelper.constructCurrentPageUrl(request));
        
        loanAccountServiceFacade.putLoanBusinessKeyInSession(globalAccountNum, request);
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewLoanAccountAllActivity", method=RequestMethod.GET)
    public ModelAndView showLoanAccountAllActivity(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewLoanAccountAllActivity", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        LoanInformationDto loanInformationDto = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        modelAndView.addObject("loanInformationDto", loanInformationDto);
        modelAndView.addObject("currentDate", new Date());
        
        List<LoanActivityDto> allLoanAccountActivities = this.loanAccountServiceFacade.retrieveAllLoanAccountActivities(globalAccountNum);
        request.setAttribute("loanAllActivityView", allLoanAccountActivities);
        
        this.loanAccountServiceFacade.putLoanBusinessKeyInSession(globalAccountNum, request);
        
        return modelAndView;
    }

    @RequestMapping(value = "/viewLoanAccountTransactionHistory", method=RequestMethod.GET)
    public ModelAndView showLoanAccountTransactionHistory(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewLoanAccountTransactionHistory", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
    
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        LoanInformationDto loanInformationDto = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        modelAndView.addObject("loanInformationDto", loanInformationDto);
        
        List<TransactionHistoryDto> transactionHistoryDto = this.centerServiceFacade.retrieveAccountTransactionHistory(globalAccountNum);
        
        request.setAttribute("trxnHistoryList", transactionHistoryDto);
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewLoanAccountNextInstallmentDetails", method=RequestMethod.GET)
    public ModelAndView showLoanAccountNextInstallmentDetails(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewLoanAccountNextInstallmentDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        LoanInformationDto loanInformationDto = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        LoanInstallmentDetailsDto loanInstallmentDetailsDto = this.loanAccountServiceFacade.retrieveInstallmentDetails(loanInformationDto.getAccountId());
        
        modelAndView.addObject("loanInformationDto", loanInformationDto);
        modelAndView.addObject("loanInstallmentDetailsDto", loanInstallmentDetailsDto);
        
        this.loanAccountServiceFacade.putLoanBusinessKeyInSession(globalAccountNum, request);
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewLoanAccountRepaymentSchedule", method=RequestMethod.GET)
    public ModelAndView showLoanAccountRepaymentSchedule(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewLoanAccountRepaymentSchedule", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        LoanInformationDto loanInformationDto = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        modelAndView.addObject("loanInformationDto", loanInformationDto);
        modelAndView.addObject("currentDate", new Date());
        
        this.loanAccountServiceFacade.putLoanBusinessKeyInSession(globalAccountNum, request);
        
        return modelAndView;
    }
    
}
