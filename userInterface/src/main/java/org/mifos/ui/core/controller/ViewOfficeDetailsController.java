package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.dto.domain.OfficeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewOfficeDetails")
public class ViewOfficeDetailsController {

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    protected ViewOfficeDetailsController(){
        //empty controller for spring auto wiring
    }

    public ViewOfficeDetailsController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView showDetails(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("viewOfficeDetails");
        OfficeFormBean officeFormBean=new OfficeFormBean();
        OfficeDto officeDto=officeServiceFacade.retrieveOfficeById(Short.parseShort(request.getParameter("id")));
        officeFormBean.setCustomFields(officeDto.getCustomFields());
        officeFormBean.setName(officeDto.getName());
        officeFormBean.setStatusId(officeDto.getStatusId().toString());
        officeFormBean.setOfficeShortName(officeDto.getOfficeShortName());
        officeFormBean.setOfficeLevelName(officeDto.getOfficeLevelName());
        officeFormBean.setId(officeDto.getId());
        if(officeDto.getAddress()!=null){
            officeFormBean.setCity(officeDto.getAddress().getCity());
            officeFormBean.setCountry(officeDto.getAddress().getCountry());
            officeFormBean.setLine1(officeDto.getAddress().getLine1());
            officeFormBean.setLine2(officeDto.getAddress().getLine2());
            officeFormBean.setLine3(officeDto.getAddress().getLine3());
            officeFormBean.setState(officeDto.getAddress().getState());
            officeFormBean.setZip(officeDto.getAddress().getZip());
            officeFormBean.setPhoneNumber(officeDto.getAddress().getPhoneNumber());
        }
        if(!(officeDto.getOfficeLevelName().equalsIgnoreCase("Head Office"))){
            officeFormBean.setParentId(officeDto.getParentId().toString());
            officeFormBean.setParentOfficeName(officeDto.getParentOfficeName());
        }
        officeFormBean.setGlobalNum(officeDto.getGlobalNum());
        officeFormBean.setId(officeDto.getId());
        officeFormBean.setLevelId(officeDto.getLevelId().toString());
        if(!officeDto.getLookupNameKey().isEmpty()){
        officeFormBean.setLookupNameKey(officeDto.getLookupNameKey());
        }
        officeFormBean.setName(officeDto.getName());
        officeFormBean.setOfficeLevelName(officeDto.getOfficeLevelName());
        officeFormBean.setOfficeShortName(officeDto.getOfficeShortName());
        officeFormBean.setOfficeStatusName(officeDto.getOfficeStatusName());
        if(!(officeDto.getOfficeLevelName().equalsIgnoreCase("Head Office"))){
        officeFormBean.setParentId(officeDto.getParentId().toString());
        officeFormBean.setParentOfficeName(officeDto.getParentOfficeName());
        }
        officeFormBean.setSearchId(officeDto.getSearchId());
        officeFormBean.setStatusId(officeDto.getStatusId().toString());
        officeFormBean.setVersionNum(officeDto.getVersionNum());
        modelAndView.addObject("officeFormBean", officeFormBean);
        modelAndView.addObject("officeAddress", officeDto.getAddress());
                return  modelAndView;
    }
}
