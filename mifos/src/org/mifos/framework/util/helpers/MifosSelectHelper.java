package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;


public class MifosSelectHelper {

	private static MifosSelectHelper instance = new MifosSelectHelper(); 
	private MifosSelectHelper() {
		map.put(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID, new String[]{"org.mifos.application.master.util.valueobjects.PrdCategoryStatus", ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID});
	}
	public static MifosSelectHelper getInstance(){
		return instance;
	}
	
	private Map map = new HashMap();
	
	public String[] getValue(String key){ 
		return (String[])map.get(key);
	}

	
}
