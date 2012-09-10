package org.mifos.ui.core.controller;

import java.util.Map;

import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        
        modelAndView.addObject("importedFiles", this.importTransactionsServiceFacade.getImportedFiles());

        return modelAndView;
    }
    
    @RequestMapping("/viewImportedTransactionsConfirmation.ftl")
    public ModelAndView validateAndConfirmUndo(@RequestParam String fileName) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Map<String, String>> errors = undoImportValidate(fileName);
        
        modelAndView.setViewName("viewImportedTransactionsConfirmation");
        modelAndView.addObject("fileName", fileName);
        modelAndView.addObject("invalid_trxns", errors.get("INVALID_TRXN"));
        modelAndView.addObject("valid_trxns", errors.get("VALID_TRXN"));
        
        return modelAndView;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/processImportedTransactionUndo")
    public String processFormSubmit(@RequestParam String fileName) {
        this.importTransactionsServiceFacade.undoFullImport(fileName);
        return "redirect:/viewImportedTransactions.ftl";
    }
    
    private Map<String, Map<String, String>> undoImportValidate(String fileName) {
        return this.importTransactionsServiceFacade.getUndoImportDateToValidate(fileName);
    }
}
