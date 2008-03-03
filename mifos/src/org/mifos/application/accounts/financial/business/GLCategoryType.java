package org.mifos.application.accounts.financial.business;

/**
 * A "category" is a top-level general ledger account. These are the categories
 * that must be present in a Mifos instance.
 * <p>
 * Note that some subcategories must also exist in the chart of accounts, see
 * the <a
 * href="http://www.mifos.org/knowledge/functional-specs/accounting-in-mifos">documentation
 * on accounting in Mifos</a> for more details.
 */
public enum GLCategoryType {
	ASSET, LIABILITY, INCOME, EXPENDITURE;
	
	/**
	 * Convert a given string to the corresponding enum type.
	 */
	public static GLCategoryType fromString(String typeAsString) {
		for (GLCategoryType t : values()) {
			if (t.toString().equals(typeAsString))
				return t;
		}
		throw new IllegalArgumentException("given string does not correspond" +
				" to any known category type");
	}
}
