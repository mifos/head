package org.mifos.ui.core.controller;

import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewImportedTransactionFileController {

    
    @Autowired
    private ImportTransactionsServiceFacade importTransactionsServiceFacade;
    
    @RequestMapping("/viewImportedTransactions.ftl")
    public ModelAndView viewImportedTransactions(@RequestParam(required = false) String fileName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("viewImportedTransactions");
        if (null == fileName || fileName.isEmpty()) {
            modelAndView.addObject("importedFiles", this.importTransactionsServiceFacade.getImportedFiles());
        } else 
        {
            this.importTransactionsServiceFacade.undoFullImport(fileName);
            modelAndView.addObject("importedFiles", this.importTransactionsServiceFacade.getImportedFiles());
        }
        return modelAndView;
    }
}
