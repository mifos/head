package org.mifos.application.accounts.financial.util.helpers;

public interface FinancialConstants {
	public static final Short DEBIT = 0;
	public static final Short CREDIT = 1;
}

// TODO: make this an enum

// public enum FinancialConstants {
// DEBIT((short) 0), CREDIT((short) 1);
//
// short value;
//
// FinancialConstants(short value) {
// setValue(value);
// }
//
// public short getValue() {
// return value;
// }
//
// public void setValue(short value) {
// this.value = value;
// }
//
// public static FinancialConstants fromValue(short value) {
// for (FinancialConstants f : values())
// if (value == f.getValue())
// return f;
// throw new IllegalArgumentException("unknown value given");
// }
// }
