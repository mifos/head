package org.mifos.ui.core.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.dto.screen.ClientInformationDto;
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
@RequestMapping("/viewClientDetails")
public class ViewClientDetailsController {

	@Autowired
	ClientServiceFacade clientServiceFacade;
	
	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;
	@Autowired
	private QuestionnaireServiceFacade questionnaireServiceFacade;
	
	private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
	
	@RequestMapping(method=RequestMethod.GET)
    public ModelAndView showDetails(HttpServletRequest request, HttpServletResponse response) throws ApplicationException{		
        ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewClientDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response)); 
        
        String clientSystemId = request.getParameter("globalCustNum");
        ClientInformationDto clientInformationDto;

        clientInformationDto = clientServiceFacade.getClientInformationDto(clientSystemId);
        
        modelAndView.addObject("clientInformationDto", clientInformationDto);

        boolean isPhotoFieldHidden = Boolean.parseBoolean(configurationServiceFacade.getConfig("Client.Photo"));
        modelAndView.addObject("isPhotoFieldHidden", isPhotoFieldHidden);
        
        try {
            modelAndView.addObject("currentPageUrl", constructCurrentPageUrl(clientSystemId));
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationException(e);
        }
        
        boolean containsQGForCloseClient = false;
        containsQGForCloseClient = questionnaireServiceFacade.getQuestionGroupInstances(clientInformationDto.getClientDisplay().getCustomerId(), "Close", "Client").size() > 0;
        modelAndView.addObject("containsQGForCloseClient", containsQGForCloseClient);
        
        clientServiceFacade.putClientBusinessKeyInSession(clientSystemId, request);
        
        return modelAndView;
	}
	
	private String constructCurrentPageUrl(String globalCustNum) throws UnsupportedEncodingException{
	    String viewName = "viewClientDetails.ftl";
	    String url = String.format("%s?globalCustNum=%s", viewName, globalCustNum);
	    return URLEncoder.encode(url, "UTF-8");
	}
	
}
