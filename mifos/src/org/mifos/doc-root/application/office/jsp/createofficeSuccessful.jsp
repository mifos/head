<!-- 

/**

 * vieweditoffice.jsp    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="cancel";
	officeActionForm.submit();
  }
  function seeDetails(){
	document.officeActionForm.method.value="get";
	officeActionForm.submit();
  }
  function create(){
	document.officeActionForm.method.value="load";
	officeActionForm.submit();
  }  
</script>

		<html-el:form action="/OfficeAction.do" method="POST">

		<!-- 	<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" valign="top" bgcolor="#FFFFFF"
						class="paddingleftmain"> -->
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="70%" align="left" valign="top" class="paddingL15T15">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td class="headingorange"><mifos:mifoslabel
										name="office.labelCreatedSuccessfully"
										bundle="OfficeResources"></mifos:mifoslabel> <br>
									<br>
									</td>
								</tr>
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="office.labelPleaseNote" bundle="OfficeResources"></mifos:mifoslabel>
									<span class="fontnormal"><c:out	value="${requestScope.OfficeVo.officeName}"></c:out> <mifos:mifoslabel
										name="office.labelOfficeAssignedNumber"
										bundle="OfficeResources"></mifos:mifoslabel> </span> <c:out
										value="${requestScope.OfficeVo.globalOfficeNum}"></c:out> <br>
									<br>

									 
									
									
									<a href="/Mifos/OfficeAction.do?method=get&officeId=<c:out value='${requestScope.OfficeVo.officeId}'/>" >
									 <mifos:mifoslabel
										name="office.labelViewOfficeDetails" bundle="OfficeResources"></mifos:mifoslabel>
										
									</a>
									<!-- <html-el:link action="/OfficeAction.do?method=get"><mifos:mifoslabel
										name="office.labelViewOfficeDetails" bundle="OfficeResources"></mifos:mifoslabel></html-el:link>
										-->
										

									<span class="fontnormal"><br>
									<br>
									</span><span class="fontnormal"> 
									<html-el:link action="/OfficeAction.do?method=load" >
									
									 <mifos:mifoslabel
										name="office.labelAddNewOfficeNow" bundle="OfficeResources"></mifos:mifoslabel></html-el:link> </span></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					<!-- </td>
				</tr>
			</table> -->
			<html-el:hidden property="input" value="createSuccess" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="formOfficeStatus"  />
			<html-el:hidden property="method" value="get" />
			<html-el:hidden property="officeId"  />
		</html-el:form>

	</tiles:put>

</tiles:insert>
