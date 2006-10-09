package org.mifos.framework.components.taggenerator;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.struts.tags.MifosTagUtils;

public class PersonnelTagGenerator extends TagGenerator {
	
	public PersonnelTagGenerator() {
		setAssociatedGenerator(new OfficeTagGenerator());
	}
	
	@Override
	protected StringBuilder build(BusinessObject obj,Object randomNum){
		return build(obj,true,randomNum);
	}

	@Override
	protected StringBuilder build(BusinessObject obj, boolean selfLinkRequired, Object randomNum){
		PersonnelBO personnel=(PersonnelBO)obj;
		StringBuilder strBuilder = getAssociatedGenerator().build(personnel.getOffice(),randomNum);
		if(strBuilder==null)
			strBuilder = new StringBuilder();
		
		buildLink(strBuilder,personnel,selfLinkRequired,randomNum);
		return strBuilder;
	}
	
	private void buildLink(StringBuilder strBuilder, PersonnelBO personnel, boolean selfLinkRequired, Object randomNum){
		if(personnel==null)
			return;
		strBuilder.append(" / ");
		if(selfLinkRequired){
			createPersonnelLink(strBuilder, personnel,randomNum);
		}else{
			strBuilder.append("<b>"+MifosTagUtils.xmlEscape(getPersonnelName(personnel))+"</b>"); 
		}
	}
	
	private void createPersonnelLink(StringBuilder strBuilder, PersonnelBO personnel, Object randomNum){
		strBuilder.append("<a href=\"");
		strBuilder.append(getAction(personnel));
		strBuilder.append(personnel.getGlobalPersonnelNum());
		strBuilder.append("&randomNum=");
		strBuilder.append(randomNum);
		strBuilder.append("\">");
		strBuilder.append(MifosTagUtils.xmlEscape( getPersonnelName(personnel)));
		strBuilder.append("</a>");
	}
	
	private String getPersonnelName(PersonnelBO personnel){
		return personnel.getDisplayName();
	}
	
	private String getAction(PersonnelBO personnel){
		return "PersonAction.do?method=get&globalPersonnelNum=";
	}
	
	
}
