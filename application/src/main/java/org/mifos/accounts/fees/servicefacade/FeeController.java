package org.mifos.accounts.fees.servicefacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FeeController {

    @Autowired
    private FeeServiceFacade feeServiceFacade;

    public FeeController(FeeServiceFacade feeServiceFacade) {
        this.feeServiceFacade = feeServiceFacade;
    }

    @RequestMapping("/viewAllFees.html")
    public void viewAllFees() {


    }



}
