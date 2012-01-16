package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.framework.exceptions.ApplicationException;
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
	
	@RequestMapping(method=RequestMethod.GET)
    public ModelAndView showDetails(HttpServletRequest request) throws ApplicationException{
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		
        ModelAndView modelAndView = new ModelAndView("m_viewClientDetails");
        if (currentDevice.isMobile()) {
            modelAndView = new ModelAndView("m_viewClientDetails");
        }
        
        String clientSystemId = request.getParameter("globalCustNum");
        ClientInformationDto clientInformationDto;
        try {
            clientInformationDto = clientServiceFacade.getClientInformationDto(clientSystemId);
        }
        catch (MifosRuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            throw e;
        }
        
        modelAndView.addObject("clientInformationDto", clientInformationDto);

        boolean isPhotoFieldHidden = Boolean.parseBoolean(configurationServiceFacade.getConfig("Client.Photo"));
        modelAndView.addObject("isPhotoFieldHidden", isPhotoFieldHidden);
        
        return modelAndView;
	}
}
