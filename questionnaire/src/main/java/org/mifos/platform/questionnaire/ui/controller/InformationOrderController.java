package org.mifos.platform.questionnaire.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.platform.questionnaire.service.InformationOrder;
import org.mifos.platform.questionnaire.service.InformationOrderServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@SuppressWarnings("PMD")
public class InformationOrderController {
	
	@Autowired
	private InformationOrderServiceFacade informationOrderServiceFacade;
	
	@RequestMapping(value = "/saveInformationOrder", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveInformationOrder(@RequestBody Map<String, Integer> order, HttpServletRequest request, HttpServletResponse response) {
		
		List<InformationOrder> informationOrderList = new ArrayList<InformationOrder>();
		InformationOrder informationOrder;
		for (Map.Entry<String, Integer> entry: order.entrySet()) {
			informationOrder = new InformationOrder(Integer.valueOf(entry.getKey()), 
					null, null, null, entry.getValue());
			informationOrderList.add(informationOrder);
		}
		informationOrderServiceFacade.updateInformationOrder(informationOrderList);
	}

}
