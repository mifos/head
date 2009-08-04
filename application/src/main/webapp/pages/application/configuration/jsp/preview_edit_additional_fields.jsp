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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="preview_edit_additional_fields" />
	
	<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
	<script language="javascript">
	function fnEditCancel(){
				customfieldsactionform.action="customFieldsAction.do?method=cancelEdit";
				customfieldsactionform.submit();
		  	}
		  	
		  	function fnEditPrevious(){
				customfieldsactionform.action="customFieldsAction.do?method=editPrevious";
				customfieldsactionform.submit();
		  	}		
</script>
		<html-el:form action="/customFieldsAction.do?method=update" onsubmit="return func_disableSubmitBtn('submitBtn');">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> 
						<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="configuration.admin" />	
				</html-el:link> / 
       			 <html-el:link action="/customFieldsAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="configuration.view_additional_fields" /> 
				</html-el:link> / <html-el:link
					action="/customFieldsAction.do?method=viewCategory&amp;randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="${requestScope.category}" />
				</html-el:link>
				 </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"> 
									<mifos:mifoslabel name="configuration.edit_additional_fields" />
              	 					- </span>
              	 					<mifos:mifoslabel name="configuration.preview_additional_fields" />   
								</td>
							</tr>
							<tr>
								
							</tr>
						</table>
						
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">
									<font class="fontnormalRedBold"><html-el:errors bundle="configurationUIResources" /> </font>
								</td>
							</tr>
							<tr>
								<td width="100%" height="23" class="fontnormal">
								<span class="fontnormalbold">
					                <mifos:mifoslabel name="configuration.additional_fields_information" />	
					                </span>
					                <br>
									<span class="fontnormal">
		        <mifos:mifoslabel name="configuration.category" isColonRequired="Yes"/>
		        </span>
		        <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'currentCategory')}" var="category">
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
		        <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'currentDataType')}" var="oneDataType">
					<c:if test="${oneDataType.id == sessionScope.customfieldsactionform.dataType}">
						<c:out value="${oneDataType.name}" />
					</c:if>
				</c:forEach>
		        <br>
		        
		        <span class="fontnormal">
     	 		<mifos:mifoslabel name="configuration.default_value" isColonRequired="Yes"/>	
     	 		</span>							     	 
     	 
     	 		<c:out value="${sessionScope.customfieldsactionform.defaultValue}"/>  	 
		    	<br>      		        
			    <br>
     			
     			

									<span class="fontnormal"> </span>

									<html-el:button property="button" styleClass="insidebuttn" onclick="javascript:fnEditPrevious()">
										<mifos:mifoslabel name="configuration.edit_additional_field" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit property="submitBtn" styleClass="buttn">
										<mifos:mifoslabel name="configuration.submit" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" onclick="javascript:fnEditCancel()">
										<mifos:mifoslabel name="configuration.cancel" />
									</html-el:button>
								</td>
							</tr>
							
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
