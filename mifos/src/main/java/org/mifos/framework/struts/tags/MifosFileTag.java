package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.html.ELFileTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;

public class MifosFileTag extends ELFileTag {
	
	private FieldConfig fieldConfig = FieldConfig.getInstance();

	private String keyhm;

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	@Override
	public int doStartTag() throws JspException {
		if(fieldConfig.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm()) ){
			TagUtils.getInstance().write(this.pageContext,renderInputsForhidden());
		}
		return super.doStartTag();
	}
	
	public String renderInputsForhidden(){
		XmlBuilder html = new XmlBuilder();
		html.singleTag("input", "type","hidden","name",getKeyhm(),"value",getPropertyExpr());
		return html.toString();
	}

}
