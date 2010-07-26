package org.mifos.ui.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



@Controller
@RequestMapping("defineMandatoryHiddenFields.ftl")
public class DefineMandatoryHiddenFieldsController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    AdminServiceFacade adminServiceFacade;


    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("definemandatory/hiddenfields", "defineMandatoryHiddenFields.ftl").build();
    }


    @ModelAttribute("fields")
    public Map<String, Boolean> getDetails(){
        Map<String, Boolean> map=new HashMap<String, Boolean>();
        map.put("hideSystemExternalId",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemExternalId());
        map.put("mandatorySystemExternalId",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemExternalId());
        map.put("mandatorySystemExternalId",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemExternalId());
        map.put("hideSystemEthnicity",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemEthnicity());
        map.put("mandatorySystemEthnicity",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemEthnicity());
        map.put("hideSystemCitizenShip",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCitizenShip());
        map.put("mandatorySystemCitizenShip",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemCitizenShip());
        map.put("hideSystemHandicapped",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemHandicapped());
        map.put("mandatorySystemHandicapped",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemHandicapped());
        map.put("hideSystemEducationLevel",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemEducationLevel());
        map.put("mandatorySystemEducationLevel",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemEducationLevel());
        map.put("hideSystemPhoto",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemPhoto());
        map.put("mandatorySystemPhoto",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemPhoto());
        map.put("hideSystemAssignClientPostions",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemAssignClientPostions());
        map.put("mandatorySystemAddress1",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatorySystemAddress1());
        map.put("hideSystemAddress2",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemAddress2());
        map.put("hideSystemAddress3",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemAddress3());
        map.put("hideSystemCity",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCity());
        map.put("hideSystemState",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemState());
        map.put("hideSystemCountry",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCountry());
        map.put("hideSystemPostalCode",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemPostalCode());
        map.put("hideSystemReceiptIdDate",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemReceiptIdDate());
        map.put("hideSystemCollateralTypeNotes",adminServiceFacade.retrieveHiddenMandatoryFields().isHideSystemCollateralTypeNotes());
        map.put("hideClientMiddleName",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientMiddleName());
        map.put("mandatoryClientMiddleName",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientMiddleName());
        map.put("hideClientSecondLastName",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientSecondLastName());
        map.put("mandatoryClientSecondLastName",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientSecondLastName());
        map.put("hideClientGovtId",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientGovtId());
        map.put("mandatoryClientGovtId",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientGovtId());
        map.put("mandatoryMaritalStatus",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryMaritalStatus());
        map.put("hideClientPovertyStatus",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientPovertyStatus());
        map.put("mandatoryClientPovertyStatus",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientPovertyStatus());
        map.put("mandatoryClientFamilyDetails",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientFamilyDetails());
        map.put("hideClientSpouseFatherInformation",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientSpouseFatherInformation());
        map.put("mandatoryClientSpouseFatherInformation",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientSpouseFatherInformation());
        map.put("hideClientSpouseFatherMiddleName",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientSpouseFatherMiddleName());
        map.put("hideClientSpouseFatherSecondLastName",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientSpouseFatherSecondLastName());
        map.put("mandatoryClientSpouseFatherSecondLastName",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientSpouseFatherSecondLastName());
        map.put("hideClientPhone",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientPhone());
        map.put("mandatoryClientPhone",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientPhone());
        map.put("hideClientTrained",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientTrained());
        map.put("mandatoryClientTrained",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientTrained());
        map.put("mandatoryClientTrainedOn",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientTrainedOn());
        map.put("mandatoryClientBusinessWorkActivities",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryClientBusinessWorkActivities());
        map.put("hideClientBusinessWorkActivities",adminServiceFacade.retrieveHiddenMandatoryFields().isHideClientBusinessWorkActivities());
        map.put("mandatoryNumberOfChildren",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryNumberOfChildren());
        map.put("hideGroupTrained",adminServiceFacade.retrieveHiddenMandatoryFields().isHideGroupTrained());
        map.put("mandatoryLoanAccountPurpose",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryLoanAccountPurpose());
        map.put("mandatoryLoanSourceOfFund",adminServiceFacade.retrieveHiddenMandatoryFields().isMandatoryLoanSourceOfFund());
        map.put("familyDetailsRequired",adminServiceFacade.retrieveHiddenMandatoryFields().isFamilyDetailsRequired());
     return map;
    }





    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    MandatoryHiddenFieldsDto bean,
                                    BindingResult result,
                                    SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineMandatoryHiddenFields";
        } else {
                adminServiceFacade.updateHiddenMandatoryFields(bean);
            status.setComplete();
        }

        return viewName;
    }

}
