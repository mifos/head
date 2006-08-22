package org.mifos.framework.struts.plugin.helper;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.util.helpers.EntityType;

/**
    Together with {@link MasterDataEntity}
 	this class provides the old
 	way of handling entity types.  The new way
 	is {@link EntityType}.
 	*/
public interface EntityMasterConstants {
	public static Short Client=1;
	public static Short LoanProduct=2;
	public static Short SavingsProduct=3;
	public static Short ProductCategory=4;
	public static Short ProductConfiguration=5;
	public static Short Fees=6;
	public static Short Accounts=7;
	public static Short Admin=8;
	public static Short Checklist=9;
	public static Short Configuration=10;
	public static Short Customer=11;
	public static Short Group=12;
	public static Short Login=13;
	public static Short Meeting=14;
	public static Short Office=15;
	public static Short Penalty=16;
	public static Short Personnel=17;
	public static Short Program=18;
	public static Short Roleandpermission=19;
	public static Short Center=20;
	public static Short Savings=21;
	public static Short Loan=22;
}
