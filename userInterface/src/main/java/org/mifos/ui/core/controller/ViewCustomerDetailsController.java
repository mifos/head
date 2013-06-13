package org.mifos.ui.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.GroupServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.dto.domain.AdminDocumentDto;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.screen.AccountPaymentDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.GroupInformationDto;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.reports.admindocuments.AdminDocumentsServiceFacade;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.mifos.ui.core.controller.util.helpers.UrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import freemarker.ext.servlet.IncludePage;

@Controller
public class ViewCustomerDetailsController {

	@Autowired
	ClientServiceFacade clientServiceFacade;
	@Autowired
	GroupServiceFacade groupServiceFacade;
	@Autowired
	CenterServiceFacade centerServiceFacade;
	@Autowired
	AdminServiceFacade adminServiceFacade;
	@Autowired
	AdminDocumentsServiceFacade adminDocumentsServiceFacade;

	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;
	@Autowired
	private QuestionnaireServiceFacade questionnaireServiceFacade;

	private final SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();

	@RequestMapping(value = "/viewClientDetails", method=RequestMethod.GET)
    public ModelAndView showClientDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        sitePreferenceHelper.resolveSiteType(modelAndView, "viewClientDetails", request);
        modelAndView.addObject("include_page", new IncludePage(request, response));

        String clientSystemId = request.getParameter("globalCustNum");
        ClientInformationDto clientInformationDto;

        clientInformationDto = clientServiceFacade.getClientInformationDto(clientSystemId);

        modelAndView.addObject("clientInformationDto", clientInformationDto);

        
        boolean isPhotoFieldHidden = this.adminServiceFacade.isHiddenMandatoryField("photo");
        modelAndView.addObject("isPhotoFieldHidden", isPhotoFieldHidden);

        modelAndView.addObject("currentPageUrl", UrlHelper.constructCurrentPageUrl(request));

        boolean containsQGForCloseClient = false;
        containsQGForCloseClient = questionnaireServiceFacade.getQuestionGroupInstances(clientInformationDto.getClientDisplay().getCustomerId(), "Close", "Client").size() > 0;
        modelAndView.addObject("containsQGForCloseClient", containsQGForCloseClient);
        request.getSession().setAttribute("backPageUrl", request.getAttribute("currentPageUrl"));

        clientServiceFacade.putClientBusinessKeyInSession(clientSystemId, request);

        return modelAndView;
	}
	
    @RequestMapping(value = "/printClientPaymentReceipt", method = RequestMethod.GET)
    public ModelAndView showLastPaymentReceipt(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = false) String globalAccountNum) {
        ModelAndView modelAndView = new ModelAndView("printClientPaymentReceipt");
        String gan = null;
        if (globalAccountNum == null) {
            gan = request.getSession().getAttribute("globalAccountNum").toString();
        } else {
            gan = globalAccountNum;
        }
        String clientSystemId = request.getParameter("globalCustNum");
        AccountPaymentDto clientAccountPayment = clientServiceFacade.getClientAccountPayments(gan).get(0);

        List<AdminDocumentDto> adminDocuments = adminDocumentsServiceFacade
                .getAdminDocumentsForAccountPayment(clientAccountPayment.getPaymentId());
        if (adminDocuments != null && !adminDocuments.isEmpty()) {
            clientAccountPayment.setAdminDocuments(adminDocuments);
        }

        modelAndView.addObject("clientAccountPayment", clientAccountPayment);
        modelAndView.addObject("globalAccountNum", gan);
        modelAndView.addObject("clientSystemId", clientSystemId);

        return modelAndView;
    }

	@RequestMapping(value = "/viewGroupDetails", method=RequestMethod.GET)
	public ModelAndView showGroupDetails(HttpServletRequest request, HttpServletResponse response) {
	    ModelAndView modelAndView = new ModelAndView();
	    sitePreferenceHelper.resolveSiteType(modelAndView, "viewGroupDetails", request);
	    modelAndView.addObject("include_page", new IncludePage(request, response));
	    
	    String groupSystemId = request.getParameter("globalCustNum");
	    GroupInformationDto groupInformationDto = this.groupServiceFacade.getGroupInformationDto(groupSystemId);

	    modelAndView.addObject("groupInformationDto", groupInformationDto);

	    boolean isGroupLoanAllowed = configurationServiceFacade.getBooleanConfig("ClientRules.GroupCanApplyLoans");
	    modelAndView.addObject("isGroupLoanAllowed", isGroupLoanAllowed);
	    
	    boolean isCenterHierarchyExists = configurationServiceFacade.getBooleanConfig("ClientRules.CenterHierarchyExists");
        modelAndView.addObject("isCenterHierarchyExists", isCenterHierarchyExists );

        modelAndView.addObject("currentPageUrl", UrlHelper.constructCurrentPageUrl(request));
        request.getSession().setAttribute("backPageUrl", request.getAttribute("currentPageUrl"));
        
        groupServiceFacade.putGroupBusinessKeyInSession(groupSystemId, request);
        
	    return modelAndView;
	}
	
	@RequestMapping(value = "/viewCenterDetails", method=RequestMethod.GET)
	public ModelAndView showCenterDetails(HttpServletRequest request, HttpServletResponse response) {
	    ModelAndView modelAndView = new ModelAndView();
	    sitePreferenceHelper.resolveSiteType(modelAndView, "viewCenterDetails", request);
	    modelAndView.addObject("include_page", new IncludePage(request, response));
	    
	    String centerSystemId = request.getParameter("globalCustNum");
	    CenterInformationDto centerInformationDto = this.centerServiceFacade.getCenterInformationDto(centerSystemId);
	    
	    modelAndView.addObject("centerInformationDto", centerInformationDto);
	    
        modelAndView.addObject("currentPageUrl", UrlHelper.constructCurrentPageUrl(request));
        request.getSession().setAttribute("backPageUrl", request.getAttribute("currentPageUrl"));   
        centerServiceFacade.putCenterBusinessKeyInSession(centerSystemId, request);
        
	    return modelAndView;
	}

}
