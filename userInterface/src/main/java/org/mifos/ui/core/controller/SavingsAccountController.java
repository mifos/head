package org.mifos.ui.core.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
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
public class SavingsAccountController {

    @Autowired
    private SavingsServiceFacade savingsServiceFacade;
    
    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    
    private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
    
    @RequestMapping(value = "/viewSavingsAccountDetails", method=RequestMethod.GET)
    public ModelAndView showSavingsAccountDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewSavingsAccountDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        SavingsAccountDetailDto savingsAccountDetailDto = savingsServiceFacade.retrieveSavingsAccountDetails(globalAccountNum);
        modelAndView.addObject("savingsAccountDetailDto", savingsAccountDetailDto);
        
        boolean containsQGForCloseSavings = false;
        containsQGForCloseSavings = questionnaireServiceFacade.getQuestionGroupInstances(savingsAccountDetailDto.getAccountId(), "Close", "Savings").size() > 0;
        modelAndView.addObject("containsQGForCloseSavings", containsQGForCloseSavings);
        
        modelAndView.addObject("backPageUrl", UrlHelper.constructCurrentPageUrl(request));
        
        savingsServiceFacade.putSavingsBusinessKeyInSession(globalAccountNum, request);
        
        // for mifostabletag
        request.getSession().setAttribute("recentActivityForDetailPage", savingsAccountDetailDto.getRecentActivity());
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/viewSavingsAccountRecentActivity", method=RequestMethod.GET)
    public ModelAndView showSavingsAccountRecentActivity(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewSavingsAccountRecentActivity", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        SavingsAccountDetailDto savingsAccountDetailDto = savingsServiceFacade.retrieveSavingsAccountDetails(globalAccountNum);
        modelAndView.addObject("savingsAccountDetailDto", savingsAccountDetailDto);
        modelAndView.addObject("currentDate", new Date());
        
        List<SavingsRecentActivityDto> recentActivity = this.savingsServiceFacade.retrieveRecentSavingsActivities(savingsAccountDetailDto.getAccountId().longValue());
        request.getSession().removeAttribute("recentActivityList");
        request.getSession().setAttribute("recentActivityList", recentActivity);
        
        savingsServiceFacade.putSavingsBusinessKeyInSession(globalAccountNum, request);
        
        return modelAndView;
    }

}
