package org.mifos.ui.core.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
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
    public ModelAndView showSavingsAccountDetails(HttpServletRequest request, HttpServletResponse response) throws ApplicationException{
        ModelAndView modelAndView =new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewSavingsAccountDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));
        
        String globalAccountNum = request.getParameter("globalAccountNum");
        
        SavingsAccountDetailDto savingsAccountDetailDto = savingsServiceFacade.retrieveSavingsAccountDetails(globalAccountNum);
        modelAndView.addObject("savingsAccountDetailDto", savingsAccountDetailDto);
        
        boolean containsQGForCloseSavings = false;
        containsQGForCloseSavings = questionnaireServiceFacade.getQuestionGroupInstances(savingsAccountDetailDto.getAccountId(), "Close", "Savings").size() > 0;
        modelAndView.addObject("containsQGForCloseSavings", containsQGForCloseSavings);
        
        try {
            modelAndView.addObject("currentPageUrl", constructCurrentPageUrl(request));
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationException(e);
        }
        
        savingsServiceFacade.putSavingsBusinessKeyInSession(globalAccountNum, request);
        
        // for mifostabletag
        request.getSession().setAttribute("recentActivityForDetailPage", savingsAccountDetailDto.getRecentActivity());
        
        return modelAndView;
    }
    
    @SuppressWarnings("unchecked")
    private String constructCurrentPageUrl(HttpServletRequest request) throws UnsupportedEncodingException{
        StringBuilder viewName =  new StringBuilder(request.getServletPath());
        StringBuilder parameters = new StringBuilder("?");
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()){
            parameters.append(String.format("%s=%s&", entry.getKey(), entry.getValue()[0]));
        }
        viewName = viewName.deleteCharAt(viewName.indexOf("/"));
        parameters = parameters.deleteCharAt(parameters.lastIndexOf("&"));
        return URLEncoder.encode(viewName.append(parameters).toString(), "UTF-8");
    }
}
