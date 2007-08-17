package org.mifos.application.accounts.util.helpers;

import org.mifos.framework.util.helpers.Money;
import org.mifos.application.personnel.business.PersonnelBO;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tomb
 * Date: Aug 16, 2007
 * Time: 3:37:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PaymentDataTemplate {
    public Money getTotalAmount();

    public PersonnelBO getPersonnel();

    public Short getPaymentTypeId();

    public Date getTransactionDate();
}
