package org.mifos.application.importexport.xls;

public enum XlsSavingsImportTemplateConstants {
    TITLE(0), HEADER(1), FIRST_ROW_WITH_DATA(2), ACCOUNT_NUMBER(0), 
    CUSTOMER_GLOBAL_ID(1), PRODUCT_NAME(2), STATUS_NAME(3), CANCEL_FlAG_REASON(4), SAVINGS_AMOUNT(5), SAVINGS_BALANCE(6);

    private final short value;

    private XlsSavingsImportTemplateConstants(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }
}
