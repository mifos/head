package org.mifos.framework.struts.tags;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

/*
 * This is used in the struts-config.xml files.
 */
public class MifosPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory{
    
   

	@Override
	public MessageResources createResources(String config) {
        return new MifosPropertyMessageResources(this, config, this.returnNull);
    }
	
	 /**
	 * serial version UID for serailization
	 */
	private static final long serialVersionUID = 354254366564561L;
	
}
