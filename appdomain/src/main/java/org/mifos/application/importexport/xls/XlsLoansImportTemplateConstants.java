package org.mifos.application.importexport.xls;

public enum XlsLoansImportTemplateConstants {
    TITLE(0),HEADER(1),FIRST_ROW_WITH_DATA(2),
    ACCOUNT_NUMBER(0),CUSTOMER_GLOBAL_ID(1),PRODUCT_NAME(2),STATUS_NAME(3),
    CANCEL_FlAG_REASON(4),LOAN_AMOUNT(5),INTEREST_RATE(6),NO_OF_INSTALLMENTS(7),
    DISBURLSAL_DATE(8),GRACE_PERIOD(9),
    SOURCE_OF_FOUNDS(10),PURPOSE(11),COLLATERAL_TYPE(12),COLLATERAL_NOTES(13),EXTERNAL_ID(14);
    private final short value;
    private XlsLoansImportTemplateConstants(int value){
        this.value=(short)value;
    }
    public short getValue(){
        return value;
    }
}
