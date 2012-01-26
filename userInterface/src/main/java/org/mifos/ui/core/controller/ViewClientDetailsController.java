package org.mifos.ui.core.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewClientDetails")
public class ViewClientDetailsController {

	@Autowired
	ClientServiceFacade clientServiceFacade;
	
	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;
	@Autowired
	private QuestionnaireServiceFacade questionnaireServiceFacade;
	
	@RequestMapping(method=RequestMethod.GET)
    public ModelAndView showDetails(HttpServletRequest request) throws ApplicationException{
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		
        ModelAndView modelAndView = new ModelAndView("m_viewClientDetails");
        if (currentDevice.isMobile()) {
            modelAndView = new ModelAndView("m_viewClientDetails");
        }
        
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
        
        return modelAndView;
	}
	
	private String constructCurrentPageUrl(String globalCustNum) throws UnsupportedEncodingException{
	    String viewName = "viewClientDetails.ftl";
	    String url = String.format("%s?globalCustNum=%s", viewName, globalCustNum);
	    return URLEncoder.encode(url, "UTF-8");
	}
	
}
