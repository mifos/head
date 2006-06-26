package org.mifos.framework.components.customTableTag;

public class HeaderDetails {

  private String headerStyle=null;

    
  public String getHeaderStyle() {
	return headerStyle;
}


  public void setHeaderStyle(String headerStyle) {
	this.headerStyle = headerStyle;
  }


  public void getHeaderInfo(StringBuilder tableInfo){
	  tableInfo.append(" class=\""+getHeaderStyle()+"\" ");
  }

}
