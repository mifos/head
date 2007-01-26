package org.mifos.framework.struts.tags;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.strutsel.taglib.html.ELSelectTag;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.LookUpMaster;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.LabelTagUtils;

public class MifosSelectNew extends ELSelectTag{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6576756988978091L;
	String key = null;
	String mifosBundle = null;
	String widthInPx=null;
	String master=null;
	String mappingKey;
	
	public MifosSelectNew() {
	}


	/**
	 * @return Returns the master.
	 */
	public String getMaster() {
		return master;
	}

	/**
	 * @param master The master to set.
	 */
	public void setMaster(String master) {
		this.master = master;
	}

	/**
	 * @return Returns the widthInPx.
	 */
	public String getWidthInPx() {
		return widthInPx;
	}
	/**
	 * @param widthInPx The widthInPx to set.
	 */
	public void setWidthInPx(String widthInPx) {
		this.widthInPx = widthInPx;
	}
	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return Returns the mappingKey.
	 */
	public String getMappingKey() {
		return mappingKey;
	}


	/**
	 * @param mappingKey The mappingKey to set.
	 */
	public void setMappingKey(String mappingKey) {
		this.mappingKey = mappingKey;
	}


	/**
	 * @return Returns the mifosBundle.
	 */
	public String getMifosBundle() {
		return mifosBundle;
	}


	/**
	 * @param mifosBundle The mifosBundle to set.
	 */
	public void setMifosBundle(String mifosBundle) {
		this.mifosBundle = mifosBundle;
	}


	@Override
	public int doEndTag() throws JspException {
		StringBuilder builder = new StringBuilder();
		Collection values = null;
        // Remove the page scope attributes we created
        pageContext.removeAttribute(Constants.SELECT_KEY);
		
		UserContext userContext = (UserContext)pageContext.getSession().getAttribute(LoginConstants.USERCONTEXT);
		if(userContext!= null){
			//Locale locale = userContext.getPereferedLocale();
			Locale locale=Locale.CANADA_FRENCH;
	    	String uiBundle="UIResources";
	    	String name=org.mifos.framework.util.helpers.Constants.SELECTTAG;
			String selectName = LabelTagUtils.getInstance().getLabel(pageContext,uiBundle,locale.getLanguage() + "_" + locale.getCountry(),name,null);
			//form the select option
			builder.append("<option value=\"\">").append(selectName).append("</option>");
						
			//get the collection of values
			values = LabelTagUtils.getInstance().getLookupValues(pageContext, mifosBundle, locale, key, mappingKey, null);
			
			if(values != null){
				for(Iterator iter = values.iterator();iter.hasNext();){
					LookUpMaster entityValue = (LookUpMaster)iter.next();
					builder.append("<option value=\"");
			
					if(master!=null && master.equalsIgnoreCase("no") && mappingKey!=null)
						builder.append(entityValue.getId());
					else
						builder.append(entityValue.getLookUpId());
					builder.append("\">")
					.append(entityValue.getLookUpValue())
					.append("</option>");
				}
						
				
			}//values!=null			
		}//userContext!=null
		
        if (saveBody != null) {
            builder.append(saveBody);
            saveBody = null;
        }
        builder.append("</select>");

        TagUtils.getInstance().write(pageContext, builder.toString());		
		
        return (EVAL_PAGE);
	}

	@Override
	public void setStyleClassExpr(String arg0) {
		super.setStyleClassExpr(arg0);
	}

}
