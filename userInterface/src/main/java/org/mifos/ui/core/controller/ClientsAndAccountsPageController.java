package org.mifos.ui.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.security.MifosUser;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.servlet.IncludePage;

@Controller
public class ClientsAndAccountsPageController {

    @Autowired
    private CenterServiceFacade centerServiceFacade;
    
    @Autowired
    private OfficeServiceFacade officeServiceFacade;
    
    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;
    
    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;
    
    private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
    
    @RequestMapping(value = "/clientsAndAccounts", method=RequestMethod.GET)
    public ModelAndView showClientsAndAccounts(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("customerSearch") CustomerSearchFormBean customerSearchFormBean, @RequestParam(required=false) Short officeId, 
            @RequestParam(required=false) Short loanOfficerId ){
        
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("include_page", new IncludePage(request, response)); 

        boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
        modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );
        
        if ( officeId != null && loanOfficerId != null){
            return showClientsAndAccountsBranchSearchLoanOfficer(request, customerSearchFormBean, modelAndView,officeId, loanOfficerId);
        } else if ( officeId != null ){
            return showClientsAndAccountsBranchSearch(request, customerSearchFormBean, modelAndView, officeId);
        } 
        return showClientsAndAccountsMainSearch(request, customerSearchFormBean, modelAndView);
    }
    
    public ModelAndView showClientsAndAccountsMainSearch(HttpServletRequest request, CustomerSearchFormBean customerSearchFormBean, 
            ModelAndView modelAndView ){
        sitePreferenceHelper.resolveSiteType(modelAndView, "clientsAndAccounts", request);
        
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Short userId = new Integer(user.getUserId()).shortValue();
        UserDetailDto userDetails = this.centerServiceFacade.retrieveUsersDetails(userId);
        
        List<OfficeDto> officeDtoList = officeServiceFacade.retrieveActiveBranchesUnderUser((short) user.getUserId());
        Map<String, String> officesMap = new HashMap<String, String>();
        for (OfficeDto officeDto : officeDtoList) {
            officesMap.put(officeDto.getId().toString(), officeDto.getName());
        }
        customerSearchFormBean.setOffices(officesMap);
        
        customerSearchFormBean.setOfficeName(userDetails.getOfficeName());
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/clientsAndAccountsBranchSearch", method=RequestMethod.GET)
    public ModelAndView showClientsAndAccountsBranchSearch(HttpServletRequest request, CustomerSearchFormBean customerSearchFormBean, 
            ModelAndView modelAndView, Short officeId ){
        sitePreferenceHelper.resolveSiteType(modelAndView, "clientsAndAccountsBranchSearch", request);
        
        customerSearchFormBean.setOfficeName(centerServiceFacade.retrieveOfficeName(officeId));
        customerSearchFormBean.setOfficeId(officeId);
        
        List<PersonnelDto> personnelDtoList = personnelServiceFacade.retrieveActiveLoanOfficersUnderOffice(officeId);
        modelAndView.addObject("personnelDtoList", personnelDtoList);
        
        return modelAndView;
    }
    
    @RequestMapping(value = "/clientsAndAccountsBranchSearchLoanOfficer", method=RequestMethod.GET)
    public ModelAndView showClientsAndAccountsBranchSearchLoanOfficer(HttpServletRequest request, CustomerSearchFormBean customerSearchFormBean, 
            ModelAndView modelAndView, Short officeId, Short loanOfficerId ){
        sitePreferenceHelper.resolveSiteType(modelAndView, "clientsAndAccountsBranchSearchLoanOfficer", request);
        
        customerSearchFormBean.setOfficeName(centerServiceFacade.retrieveOfficeName(officeId));
        customerSearchFormBean.setOfficeId(officeId);
        
        List<PersonnelDto> personnelDtoList = personnelServiceFacade.retrieveActiveLoanOfficersUnderOffice(officeId);
        modelAndView.addObject("personnelDtoList", personnelDtoList);
        
        List<CustomerDetailDto> customerList = this.centerServiceFacade.retrieveCustomersUnderUser(loanOfficerId);
        modelAndView.addObject("customerList", customerList);
        
        return modelAndView;
    }
}
