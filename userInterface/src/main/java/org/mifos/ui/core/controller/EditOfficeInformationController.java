package org.mifos.ui.core.controller;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.dto.domain.OfficeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/editOfficeInformation")
@SuppressWarnings("PMD")
public class EditOfficeInformationController {

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String PREVIEW_PARAM = "PREVIEW";

    public EditOfficeInformationController(){
        //empty controller for spring auto wiring
    }

    public EditOfficeInformationController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView populateForm(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        Short officeId=officeServiceFacade.retrieveOfficeById(Short.parseShort(request.getParameter("officeLevelId"))).getLevelId();
        OfficeDto officeDto= officeServiceFacade.retrieveOfficeById(Short.parseShort(request.getParameter("officeLevelId")));
        OfficeFormBean formBean=new OfficeFormBean();
        if(officeDto.getAddress()!=null){
        formBean.setCity(officeDto.getAddress().getCity());
        formBean.setCountry(officeDto.getAddress().getCountry());
        formBean.setLine1(officeDto.getAddress().getLine1());
        formBean.setLine2(officeDto.getAddress().getLine2());
        formBean.setLine3(officeDto.getAddress().getLine3());
        formBean.setZip(officeDto.getAddress().getZip());
        formBean.setPhoneNumber(officeDto.getAddress().getPhoneNumber());
        formBean.setState(officeDto.getAddress().getState());
        }
        if(officeDto.getCustomFields()!=null){
        formBean.setCustomFields(officeDto.getCustomFields());
        }
        formBean.setGlobalNum(officeDto.getGlobalNum());
        formBean.setId(officeDto.getId());
        formBean.setLevelId(officeDto.getLevelId().toString());
        formBean.setLookupNameKey(officeDto.getLookupNameKey());
        formBean.setName(officeDto.getName());
        formBean.setOfficeLevelName(officeDto.getOfficeLevelName());
        formBean.setOfficeShortName(officeDto.getOfficeShortName());
        formBean.setOfficeStatusName(officeDto.getOfficeStatusName());
        if(officeDto.getLevelId()!=1){
        formBean.setParentId(officeDto.getParentId().toString());
        formBean.setParentOfficeName(officeDto.getParentOfficeName());
        }
        formBean.setSearchId(officeDto.getSearchId());
        formBean.setStatusId(officeDto.getStatusId().toString());

        formBean.setVersionNum(officeDto.getVersionNum());
        modelAndView.addObject("officeFormBean", formBean);
        modelAndView.addObject("parentOffices", getParentDetails(officeId.toString()));
        modelAndView.addObject("view", "disable");
        modelAndView.addObject("officeTypes", getOfficeTypes(officeDto.getLevelId().toString()));
        return  modelAndView;
    }


    public Map<String, String> getParentDetails(String officelevelId){
        List<OfficeDto> headOffices=officeServiceFacade.retrieveAllOffices().getHeadOffices();
        List<OfficeDto> regionalOffices=officeServiceFacade.retrieveAllOffices().getRegionalOffices();
        List<OfficeDto> divisionalOffices=officeServiceFacade.retrieveAllOffices().getDivisionalOffices();
        List<OfficeDto> areaOffices=officeServiceFacade.retrieveAllOffices().getAreaOffices();
        Map<String, String> parentOffices=new LinkedHashMap<String, String>();
        if(!officelevelId.equals("")){
            int officeId=Integer.parseInt(officelevelId);

            if(officeId>=2){
                for(OfficeDto officeDto:headOffices){
                    parentOffices.put(officeDto.getLevelId().toString(), "Head Office("+officeDto.getName()+")");
                }
            }
            if(officeId>=3){
                for(OfficeDto officeDto:regionalOffices){
                    parentOffices.put(officeDto.getLevelId().toString(), "Regional Office("+officeDto.getName()+")");
                }
            }
            if(officeId>=4){
                for(OfficeDto officeDto:divisionalOffices){
                    parentOffices.put(officeDto.getLevelId().toString(), "Divisional Office("+officeDto.getName()+")");
                }
            }
            if(officeId>=5){
                for(OfficeDto officeDto:areaOffices){
                    parentOffices.put(officeDto.getLevelId().toString(), "Area Office("+officeDto.getName()+")");
                }
            }
        }else{
            parentOffices.put("", "");
        }
        return parentOffices;
    }


    public Map<String, String> getOfficeTypes(String officeId){
        Map<String, String> officeTypes=new LinkedHashMap<String, String>();
        if(officeId.equals("1")){
        officeTypes.put("1", "Head Office");
        }else if(officeId.equals("new")){
        officeTypes.put("1", "Head Office");
        officeTypes.put("2", "Regional Office");
        officeTypes.put("3", "Divisional Office");
        officeTypes.put("4", "Area Office");
        officeTypes.put("5", "Branch Office");
        }else{
            officeTypes.put("2", "Regional Office");
            officeTypes.put("3", "Divisional Office");
            officeTypes.put("4", "Area Office");
            officeTypes.put("5", "Branch Office");
        }
        return officeTypes;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,@RequestParam(value = PREVIEW_PARAM, required = false) String preview,
                                    @Valid @ModelAttribute("officeFormBean") OfficeFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("editOfficeInformation");
            mav.addObject("officeTypes", getOfficeTypes(formBean.getLevelId()));
            if(!formBean.getLevelId().equals("1")){
            mav.addObject("parentOffices", getParentDetails(officeServiceFacade.retrieveOfficeById(formBean.getId()).getLevelId().toString()));
            formBean.setParentId(officeServiceFacade.retrieveOfficeById(formBean.getId()).getParentId().toString());
            }
            mav.addObject("officeTypes", getOfficeTypes(formBean.getLevelId()));
            mav.addObject("officeFormBean", formBean);
            mav.addObject("view", "enable");
        }
        else{
            if(StringUtils.isNotBlank(formBean.getOfficeShortName()) && StringUtils.isNotBlank(formBean.getName()) && StringUtils.isNotBlank(formBean.getLevelId()) && StringUtils.isNotBlank(preview)){
                if((!formBean.getLevelId().equals("1") && StringUtils.isNotBlank(formBean.getParentId())) || (formBean.getLevelId().equals("1") && !StringUtils.isNotBlank(formBean.getParentId()))){
                mav = new ModelAndView("officePreview");
                }else{
                mav = new ModelAndView("editOfficeInformation");
                }
                switch(Integer.parseInt(formBean.getLevelId())){
                case 1:
                    formBean.setOfficeLevelName("Head Office");
                    break;
                case 2:
                    formBean.setOfficeLevelName("Regional Office");
                    break;
                case 3:
                    formBean.setOfficeLevelName("Divisional Office");
                    break;
                case 4:
                    formBean.setOfficeLevelName("Area Office");
                    break;
                default:
                    formBean.setOfficeLevelName("Branch Office");
                    break;
                }
                if(!formBean.getLevelId().equals("1")){
                formBean.setParentOfficeName(officeServiceFacade.retrieveOfficeById(Short.parseShort(formBean.getParentId())).getName());
                }
                if(!formBean.getLevelId().equals("1")){
                    mav.addObject("parentOffices", getParentDetails(formBean.getLevelId()));
                    }
                    mav.addObject("officeTypes", getOfficeTypes(formBean.getLevelId()));
                mav.addObject("officeFormBean",formBean);
            }
            else{
            mav = new ModelAndView("editOfficeInformation");
            if(!formBean.getLevelId().equals("1")){
            mav.addObject("parentOffices", getParentDetails(formBean.getLevelId()));
            }
            mav.addObject("officeTypes", getOfficeTypes(formBean.getLevelId()));
            mav.addObject("officeFormBean", formBean);
            mav.addObject("view", "enable");
            }
        }
        return mav;
    }
}
