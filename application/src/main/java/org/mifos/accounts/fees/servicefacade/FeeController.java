package org.mifos.accounts.fees.servicefacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FeeController {

    private FeeServiceFacade feeServiceFacade = null;

    @Autowired
    public FeeController(FeeServiceFacade feeServiceFacade) {
        this.feeServiceFacade = feeServiceFacade;
    }

}
