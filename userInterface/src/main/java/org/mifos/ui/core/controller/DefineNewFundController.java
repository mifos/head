package org.mifos.ui.core.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mifos.accounts.fund.servicefacade.FundCodeDto;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/defineNewFund")
public class DefineNewFundController {

    private static final String TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE="Cancel";
    private static final String EDIT_PARAM_VALUE="Edit fund information";

    @Autowired
    FundServiceFacade fundServiceFacade;

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView showPopulatedForm(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,@RequestParam(value = "SUBMIT", required = false) String submit,@RequestParam(value = EDIT_PARAM, required = false) String edit,
            FundFormBean bean, BindingResult result,
            SessionStatus status){
        ModelAndView modelAndView=new ModelAndView();
        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            modelAndView.setViewName(TO_ADMIN_SCREEN);
            status.setComplete();
        }else  if (EDIT_PARAM_VALUE.equals(edit)) {
            modelAndView.setViewName("defineNewFund");
            modelAndView.addObject("formBean",bean);
            status.setComplete();
        }else  if ("Submit".equals(submit)) {
            FundCodeDto codeDto=new FundCodeDto();
            codeDto.setId(bean.getCode().getId());
            codeDto.setValue(bean.getCode().getValue());
            FundDto fundDto=new FundDto();
            fundDto.setCode(codeDto);
            fundDto.setId(bean.getId());
            fundDto.setName(bean.getName());
            this.fundServiceFacade.createFund(fundDto);
            modelAndView.setViewName(TO_ADMIN_SCREEN);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("defineNewFund");
            modelAndView.addObject("formBean",bean);
        } else {
            List<FundCodeDto> codeList= this.fundServiceFacade.getFundCodes();
            Map<String, String> codeMap=new HashMap<String, String>();
            for (FundCodeDto fundCode:codeList) {
                codeMap.put(fundCode.getId(), fundCode.getValue());
            }
            modelAndView.setViewName("defineNewFund");
            modelAndView.addObject("formBean",bean);
            modelAndView.addObject("code", codeMap);
        }
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
     public ModelAndView showPopulatedForm(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
             FundFormBean bean, BindingResult result,
                                    SessionStatus status) {
        ModelAndView modelAndView=new ModelAndView(TO_ADMIN_SCREEN);
        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            modelAndView.setViewName(TO_ADMIN_SCREEN);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("defineNewFunds");
        } else {
            Integer codeId=fundServiceFacade.getFunds().size()+1;
            FundCodeDto codedto=new FundCodeDto();
            codedto.setId(bean.getCode().getId());
            codedto.setValue("0"+(Integer.parseInt(bean.getCode().getId())-1));
            FundDto dto=new FundDto();
            dto.setName(bean.getName());
            dto.setCode(codedto);
            dto.setId(codeId.toString());
            modelAndView.setViewName("newFundPreview");
            modelAndView.addObject("formBean", dto);
        }
        return modelAndView;
    }






}
