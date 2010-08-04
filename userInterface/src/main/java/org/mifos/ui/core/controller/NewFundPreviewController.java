package org.mifos.ui.core.controller;


import org.mifos.accounts.fund.servicefacade.FundCodeDto;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
@Controller
@RequestMapping("/newFundPreview")
public class NewFundPreviewController {
    private static final String TO_ADMIN_SCREEN = "AdminAction.do?method=load";
    private static final String REDIRECT_TO_VIEW_FUNDS = "viewFunds.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE="Cancel";
    private static final String EDIT_PARAM_VALUE="Edit fund information";

    private static final String EDIT_PARAM = "EDIT";


    @Autowired
    private FundServiceFacade fundServiceFacade;

    protected NewFundPreviewController() {
        // spring auto wiring
    }

    public NewFundPreviewController(final FundServiceFacade fundServiceFacade) {
        this.fundServiceFacade = fundServiceFacade;
    }
    @RequestMapping(method=RequestMethod.POST)
    @ModelAttribute("formBean")
    public FundDto showEditInformation(FundFormBean formBean){
        Integer codeId=fundServiceFacade.getFunds().size()+1;
        FundCodeDto codedto=new FundCodeDto();
        codedto.setId(formBean.getCode().getId());
        codedto.setValue("0"+(Integer.parseInt(formBean.getCode().getId())-1));
        FundDto dto=new FundDto();
        dto.setName(formBean.getName());
        dto.setCode(codedto);
        dto.setId(codeId.toString());
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel, FundFormBean formBean,
            BindingResult result, SessionStatus status) {
        String viewName = TO_ADMIN_SCREEN;

        if (EDIT_PARAM_VALUE.equals(edit)) {
            viewName = "editFunds";
        } else if (CANCEL_PARAM_VALUE.equals(cancel)) {
            viewName = REDIRECT_TO_VIEW_FUNDS;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "newFundPreview";
        } else {
            FundCodeDto codeDto=new FundCodeDto();
            codeDto.setId(formBean.getCode().getId());
            codeDto.setValue(formBean.getCode().getValue());
            FundDto fundDto=new FundDto();
            fundDto.setCode(codeDto);
            fundDto.setId(formBean.getId());
            fundDto.setName(formBean.getName());
            this.fundServiceFacade.createFund(fundDto);
            viewName = TO_ADMIN_SCREEN;
            status.setComplete();
        }
        return viewName;
    }
}
