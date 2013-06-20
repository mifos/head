package org.mifos.ui.core.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.CustomerSearchServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.core.MifosException;
import org.mifos.dto.domain.CustomerHierarchyDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.CustomerStatusDetailDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.MifosUser;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.servlet.IncludePage;

@Controller
@RequestMapping("/home")
@SuppressWarnings("PMD.AvoidUsingShortType")
public class HomePageController {
	
	@Autowired
	private CenterServiceFacade centerServiceFacade;
	
	@Autowired
	private PersonnelServiceFacade personnelServiceFacade;
	
	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;
	
	@Autowired
    private ClientServiceFacade clientServiceFacade;
	
	@Autowired
    private CustomerSearchServiceFacade customerSearchServiceFacade;
	
	private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
	
	@RequestMapping(method = RequestMethod.GET)
	@ModelAttribute("customerSearch")
	public ModelAndView showPopulatedForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("customerSearch") CustomerSearchFormBean customerSearchFormBean )
			throws MifosException, PersistenceException {

		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 
        ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "home", request);
        modelAndView.addObject("include_page", new IncludePage(request, response)); 
        
        Short userId = (short) user.getUserId();
        UserDetailDto userDetails = this.centerServiceFacade.retrieveUsersDetails(userId);
        modelAndView.addObject("customerSearch", customerSearchFormBean);
        boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
        modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );
        
        if (sitePreferenceHelper.isMobile(request)) {
	        List<ValueListElement> availibleClientGenders = clientServiceFacade.getClientGenders();
	        modelAndView.addObject("availibleClientGenders", availibleClientGenders);
	        
	        HashMap<String, ArrayList<CustomerStatusDetailDto>> customerStates = new HashMap<String, ArrayList<CustomerStatusDetailDto>>();
	        customerStates.putAll(customerSearchServiceFacade.getAvailibleCustomerStates());
	        modelAndView.addObject("availibleCustomerStates", customerStates);
        }
        
        if (userDetails.isLoanOfficer()) {
            loadLoanOfficerCustomersHierarchyForSelectedDay(userId, modelAndView, customerSearchFormBean);
            modelAndView.addObject("isLoanOfficer", true);
        } else {
        	modelAndView.addObject("isLoanOfficer", false);
        }
        
        return modelAndView;
	}

    private void loadLoanOfficerCustomersHierarchyForSelectedDay(Short userId, ModelAndView modelAndView, CustomerSearchFormBean customerSearchFormBean)
			throws MifosException {
        CustomerHierarchyDto hierarchy;
        List<String> nearestDates = new ArrayList<String>();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", personnelServiceFacade.getUserPreferredLocale());
        Date selectedDate = new LocalDate().toDateMidnight().toDate();
        
        DateTime nextDate = new DateTime();
        for (int i = 0; i < 7; i++){
            nearestDates.add(formatter.format(nextDate.toDate()));
            nextDate = nextDate.plusDays(1);
        }
        
        if ( customerSearchFormBean.getSelectedDateOption() != null ){
        	try {
        		selectedDate = formatter.parse(customerSearchFormBean.getSelectedDateOption());
        	} catch (ParseException e){
        		throw new MifosException(e);
        	}
        }

        hierarchy = personnelServiceFacade.getLoanOfficerCustomersHierarchyForDay(userId, new DateTime(selectedDate));
        
        modelAndView.addObject("nearestDates", nearestDates);
        modelAndView.addObject("hierarchy", hierarchy);
    }
	
}
