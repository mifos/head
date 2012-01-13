package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.servicefacade.ClientServiceFacade;
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
	
	@RequestMapping(method=RequestMethod.GET)
    public ModelAndView showDetails(HttpServletRequest request) throws Exception{
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
        /*
         * In ClientCustAction.get there is SessionUtils.setAttribute(ClientConstants.IS_PHOTO_FIELD_HIDDEN, FieldConfig.getInstance().isFieldHidden("Client.Photo"), request);
         * but mifos-userinterface can not access FieldConfig
         */
        modelAndView.addObject("isPhotoFieldHidden", false);
        
        
        
        return modelAndView;
	}
}
