<!-- 

/**

 * CreateCategoryConfirm.jsp    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="javascript">
		<!--
			function fnGet() {
				mifosproddefactionform.action="mifosproddefaction.do";
				mifosproddefactionform.submit();
			}
			function fnLoad() {
				mifosproddefactionform.method.value="load";
				mifosproddefactionform.action="mifosproddefaction.do";
				mifosproddefactionform.submit();
			}
		//-->
		</script>

		<html-el:form action="/mifosproddefaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="product.addsuccessful" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="product.plsnote" bundle="ProductDefUIResources" />: <span
								class="fontnormal"> <mifos:mifoslabel name="product.prodnum"
								bundle="ProductDefUIResources" />:</span> <c:out
								value="${requestScope.Context.valueObject.globalPrdOfferingNum}" />
							<span class="fontnormal"><br>
							</span><span class="fontnormal"></span><span class="fontnormal"><br>
							<br>
							</span><html-el:link href="javascript:fnGet()">
								<mifos:mifoslabel name="product.viewdet"
									bundle="ProductDefUIResources" />
							</html-el:link> <span class="fontnormal"><br>
							<br>
							</span><span class="fontnormal"> <html-el:link
								href="javascript:fnLoad()">
								<mifos:mifoslabel name="product.defprdcat"
									bundle="ProductDefUIResources" />
							</html-el:link></span></td>
						</tr>
						<html-el:hidden property="method" value="get" />
						<html-el:hidden property="productCategoryID"
							value="${requestScope.Context.valueObject.productCategoryID}" />
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
