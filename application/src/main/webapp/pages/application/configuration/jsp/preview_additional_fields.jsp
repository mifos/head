<%--
Copyright (c) 2005-2009 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<span style="display: none" id="page.id">preview_additional_fields</span>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">
	function fnCreateCancel(){
				customfieldsactionform.action="customFieldsAction.do?method=cancelCreate";
				customfieldsactionform.submit();
		  	}
		  	
		  	function fnPrevious(){
				customfieldsactionform.action="customFieldsAction.do?method=previous";
				customfieldsactionform.submit();
		  	}		
</script>
<html-el:form action="/customFieldsAction.do?method=create" onsubmit="return func_disableSubmitBtn('submitBtn');">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="450" align="left" valign="top" bgcolor="#FFFFFF">      
     <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="27%">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                      <td class="timelineboldgray">
                      <mifos:mifoslabel name="configuration.additional_fields_information" />                      
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="73%" align="right">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorange">
                      <mifos:mifoslabel name="configuration.reviewandsubmit" />                      
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td align="left" valign="top" class="paddingleftCreates">
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange">
                <span class="heading">
				<mifos:mifoslabel name="configuration.define_additional_fields" />
              	 - </span>
              	 <mifos:mifoslabel name="configuration.preview_additional_fields" />                      
              	
              	 </td>
              </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="configuration.preview_instruction" />
                </td>
              </tr>
            </table>         <br>   
            
            
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <font class="fontnormalRedBold"> 
					<html-el:errors	bundle="configurationUIResources" /> 
				</font>
                <td width="100%" height="23" class="fontnormal">
                
                <span class="fontnormalbold">
                <mifos:mifoslabel name="configuration.additional_fields_information" />	
                </span>
                <br>
                <span class="fontnormal">
		        <mifos:mifoslabel name="configuration.category" isColonRequired="Yes"/>
		        </span>
		        <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'allCategories')}" var="category">
					<c:if test="${category.id == sessionScope.customfieldsactionform.categoryType}">
						<c:out value="${category.name}" />
					</c:if>
				</c:forEach>
		        <br>		    
                
                <span class="fontnormal">
     	 		<mifos:mifoslabel name="configuration.label" isColonRequired="Yes"/>	
     	 		</span>							     	 
     	 		<c:out value="${sessionScope.customfieldsactionform.labelName}"/>  	 
		    	<br> 
		    	
				<span class="fontnormal"> <mifos:mifoslabel name="configuration.mandatory" isColonRequired="Yes"/> </span>
				<c:out value="${sessionScope.customfieldsactionform.mandatoryStringValue}"/> 
				<br>
				
		    	<span class="fontnormal">
		        <mifos:mifoslabel name="configuration.data_type" isColonRequired="Yes"/>
		        </span>
		        <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'allDataTypes')}" var="oneDataType">
					<c:if test="${oneDataType.id == sessionScope.customfieldsactionform.dataType}">
						<c:out value="${oneDataType.name}" />
					</c:if>
				</c:forEach>
		        <br>
		        </span>
		        <span class="fontnormal">
     	 		<mifos:mifoslabel name="configuration.default_value" isColonRequired="Yes"/>	
     	 		</span>							     	 
     	 		<span class="fontnormal">
     	 		<c:out value="${sessionScope.customfieldsactionform.defaultValue}"/> 
     	 		</span> 	 
		    	<br> 
		    	<br>     		        
			    <span class="fontnormal">
			    <html-el:button  styleId="preview_additional_fields.button.edit" property="button" styleClass="insidebuttn" onclick="javascript:fnPrevious();">
					<mifos:mifoslabel name="configuration.edit_additional_field" ></mifos:mifoslabel>
				</html-el:button>      
				</span>
                </td>
              </tr>
            </table>
            
            
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp; </td>
              </tr>
            </table>
            <br>
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                <html-el:submit styleId="preview_additional_fields.button.submit" property="submitBtn" styleClass="buttn">
							<mifos:mifoslabel name="configuration.submit" />
				</html-el:submit>&nbsp;
				<html-el:button  styleId="preview_additional_fields.button.cancel" property="calcelButton" styleClass="cancelbuttn" onclick="javascript:fnCreateCancel();">
							<mifos:mifoslabel name="configuration.cancel" />
				</html-el:button>                
                </td>
              </tr>
            </table>
            <br>
          </td>
        </tr>
      </table>      <br></td>
  </tr>
</table>
<br><html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	</html-el:form>
	</tiles:put>
</tiles:insert>

