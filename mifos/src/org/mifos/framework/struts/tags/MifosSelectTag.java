/**
 * 
 */
package org.mifos.framework.struts.tags;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.strutsel.taglib.html.ELSelectTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.LabelTagUtils;



public class MifosSelectTag extends ELSelectTag {
	private static final long serialVersionUID = 1L;
	
	private FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
	
	private String keyhm=null;
	
	public MifosSelectTag() {
		super();
	}

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	@Override
	public int doStartTag() throws JspException {
		if(fieldConfigItf.isFieldHidden(getKeyhm()))
			return SKIP_BODY;
		else if (!fieldConfigItf.isFieldHidden(getKeyhm()) && fieldConfigItf.isFieldManadatory(getKeyhm()) ){
			StringBuffer inputsForhidden=new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""+getKeyhm()+"\" value=\""+getPropertyExpr()+"\"/>");
		    TagUtils.getInstance().write(this.pageContext,inputsForhidden.toString());
		}
		return super.doStartTag();
	}

    
	public int doEndTag() throws JspException {
		
		
		if(fieldConfigItf.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		

		String bundle="UIResources";
    	String name=org.mifos.framework.util.helpers.Constants.SELECTTAG;
    	
        // Remove the page scope attributes we created
        pageContext.removeAttribute(Constants.SELECT_KEY);

        // Render a tag representing the end of our current form
        StringBuffer results = new StringBuffer();
        results.append("<option value= \"\">");
        String preferredUserLocale=LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
        results.append(LabelTagUtils.getInstance().getLabel(pageContext,bundle,preferredUserLocale,name,null));
        results.append("</option>");
	
        if (saveBody != null) {
            results.append(saveBody);
            saveBody = null;
        }
        
        results.append("</select>");
        
        
        TagUtils.getInstance().write(pageContext, results.toString());
        return (EVAL_PAGE);
    }
}
