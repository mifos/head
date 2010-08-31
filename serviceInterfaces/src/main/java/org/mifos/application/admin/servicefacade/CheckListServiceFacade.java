package org.mifos.application.admin.servicefacade;

import java.util.List;

import org.mifos.dto.screen.AccountCheckBoxItemDto;
import org.mifos.dto.screen.CustomerCheckBoxItemDto;

public interface CheckListServiceFacade {

    List<CustomerCheckBoxItemDto> retreiveAllCustomerCheckLists();

    List<AccountCheckBoxItemDto> retreiveAllAccountCheckLists();
}
