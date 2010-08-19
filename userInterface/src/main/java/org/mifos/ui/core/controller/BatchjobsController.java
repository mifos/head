package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.BatchjobsDto;
import org.mifos.application.admin.servicefacade.BatchjobsServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/batchjobs")
public class BatchjobsController {

  @Autowired
  private BatchjobsServiceFacade batchjobsServiceFacade;

    public BatchjobsController() {
        // default contructor for spring autowiring
    }

    public BatchjobsController(final BatchjobsServiceFacade batchjobsServiceFacade) {
        this.batchjobsServiceFacade = batchjobsServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.batchjobs", "batchjobs.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadBatchjobsInfo(HttpServletRequest request) {
        ServletContext context = request.getSession().getServletContext();
        List<BatchjobsDto> batchjobs = batchjobsServiceFacade.getBatchjobs(context);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("request", request);
        model.put("batchjobs", batchjobs);

        Map<String, Object> status = new HashMap<String, Object>();
        List<String> errorMessages = new ArrayList<String>();
        status.put("errorMessages", errorMessages);

        ModelAndView modelAndView = new ModelAndView("batchjobs", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }

}
