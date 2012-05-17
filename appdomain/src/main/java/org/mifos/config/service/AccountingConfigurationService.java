package org.mifos.config.service;

public interface AccountingConfigurationService {

    Short getDigitsBeforeDecimal();

    Short getDigitsAfterDecimal();
    
    int getGlNameMode();
    
    boolean isDefaultMifosCurrency(String currencyCode);

}
