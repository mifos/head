package org.mifos.financialaccounting.struts.action;

public enum UploadXLSConstants {
	 SKIPPED_ROWS(3), FIRST_CLIENT_ROW(4), HEADER_ROW(3), TITLE_ROW(1),

    LAST_CELL(30),
   TRANSACTION_DATE_CELL(0),TRANSACTION_TYPE_CELL(1),OFFICE_LEVEL_CELL(2),OFFICE_NAME_CELL(3),MAIN_ACCOUNT_CELL(4),
   ACCOUNT_HEAD_CELL(5),AMOUNT_CELL(6),
   NARRATION_CELL(7),CHEQUE_NO_CELL(8),CHEQUE_DATE_CELL(9),BANK_NAME_CELL(10),BANK_BRANCH_CELL(11);


    private final short value;

    private UploadXLSConstants(int value) {
        this.value = (short) value;
    }

    public short value() {
        return value;
    }

    public String getCellNameKey() {
        StringBuilder sb = new StringBuilder("admin.importexport.xls.cell.");
        sb.append(this.toString().replaceFirst("_CELL", "").toLowerCase());
        return sb.toString();
    }

}
