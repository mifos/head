<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="/mifos/collectionsheettags" prefix="collectionsheet"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="BulkEntryData"></span>
	<script type="text/javascript">
		//modified from http://jsbin.com/ahaxe
		(function($){
	        $.fn.autoGrowInput = function(o) {
	            o = $.extend({
	                maxWidth: 1000,
	                minWidth: 0,
	                comfortZone: 70
	            }, o);
	            
	            this.filter('input:text').each(function(){
	                
	                var minWidth = o.minWidth || $(this).width(),
	                    val = '',
	                    input = $(this),
	                    testSubject = $('<tester/>').css({
	                        position: 'absolute',
	                        top: -9999,
	                        left: -9999,
	                        width: 'auto',
	                        fontSize: input.css('fontSize'),
	                        fontFamily: input.css('fontFamily'),
	                        fontWeight: input.css('fontWeight'),
	                        letterSpacing: input.css('letterSpacing'),
	                        whiteSpace: 'nowrap'
	                    }),
	                    check = function() {
	                        
	                        if (val === (val = input.val())) {return;}
	                        
	                        // Enter new content into testSubject
	                        var escaped = val.replace(/&/g, '&amp;').replace(/\s/g,'&nbsp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	                        testSubject.html(escaped);
	                        
	                        // Calculate new width + whether to change
	                        var testerWidth = testSubject.width(),
	                            newWidth = (testerWidth + o.comfortZone) >= minWidth ? testerWidth + o.comfortZone : minWidth,
	                            currentWidth = input.width(),
	                            isValidWidthChange = (newWidth < currentWidth && newWidth >= minWidth)
	                                                 || (newWidth > minWidth && newWidth < o.maxWidth);
	                        
	                        // Animate width
	                        if (isValidWidthChange) {
	                            input.width(newWidth);
	                        }
	                        
	                    };
	                    
	                testSubject.insertAfter(input);
	                
	                //$(this).bind('keyup keydown blur update', check);
	                $('input[type=text]').bind('keyup keydown blur update change click', check); // update all text inputs 
	                
	            });
	            return this;
	        };
	    })(jQuery);
	</script>
	<script language="javascript">
		<!--
        // trap the return/enter key to prevent accidental form submission
        jQuery(document).ready(function() {
            jQuery(':input').bind("keypress", function(e) {
                var code = (e.keyCode ? e.keyCode : e.which);
                if (code == 13) { // Enter keycode
                    e.preventDefault();
                }
            });
            
            jQuery('input[type=text]').autoGrowInput({
                comfortZone: 10,
                minWidth: 40,
                maxWidth: 100
            });
            
            jQuery('input[type=text]').first().trigger('click'); // resize text inputs on page load (if necessary)
        });
       
		function fnSubmit(form, buttonSubmit) {
				buttonSubmit.disabled=true;
				form.method.value="preview";
				form.action="collectionsheetaction.do";
				form.submit();
			}
		</script>
		
		<script SRC="pages/application/collectionsheet/js/BulkEntry.js"></script>

		<html-el:form action="/collectionsheetaction">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BulkEntry')}" var="BulkEntry" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="900" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="33%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="bulkEntry.select" />
															<c:choose>
																<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
																	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
																</c:otherwise>
															</c:choose>
														</td>
													</tr>
												</table>
											</td>
											<td width="34%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<span id="bulkentry_data.label.enterdata"><mifos:mifoslabel name="bulkEntry.enterdata" /></span>
														</td>
													</tr>
												</table>
											</td>
											<td width="33%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="bulkEntry.revnsub" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td width="1613" align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading" id="bulkentry_data.heading"> <mifos:mifoslabel name="${ConfigurationConstants.BULKENTRY}" />- </span>
												<mifos:mifoslabel name="bulkEntry.enterdata" />
												<br>
												<br>
												<span class="fontnormal"> <mifos:mifoslabel name="bulkEntry.entdataclipre" /> <mifos:mifoslabel name="bulkEntry.clickcanc" /> </span>
												<br>
												<br>
												<table width="590" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="fontnormalbold">
															<c:choose>
																<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
																	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" isColonRequired="Yes"/>
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" isColonRequired="Yes"/>
																</c:otherwise>
															</c:choose>
															
															<c:out value="${BulkEntry.bulkEntryParent.customerDetail.displayName}" />
															<br>
															<mifos:mifoslabel name="bulkEntry.dateoftrxn" isColonRequired="Yes"/>
															
															<c:out value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BulkEntry.transactionDate)}' />

														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr valign="top">
											<td width="77%" height="52" class="fontnormal">
												<table width="740" border="0" cellspacing="0" cellpadding="0">
													<tr valign="top">
														<td width="207" class="fontnormal">
															<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" isColonRequired="Yes"/>
														
															<c:out value="${BulkEntry.office.officeName}" />
															<br>
															<mifos:mifoslabel name="bulkEntry.loanofficer" isColonRequired="Yes" />
															
															<c:out value="${BulkEntry.loanOfficer.displayName}" />
														</td>
														<td width="533" class="fontnormal">
															<mifos:mifoslabel name="bulkEntry.pmnttype" isColonRequired="Yes"/>
															

															<c:out value="${BulkEntry.paymentType.displayValue}" />
															<table>
																<tr id="BulkEntry.ReceiptId">
																	<td class="fontnormal">
																		<mifos:mifoslabel name="bulkEntry.rcptid" keyhm="BulkEntry.ReceiptId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
																		<c:out value="${BulkEntry.receiptId}" />
																	</td>
																</tr>
																<tr id="BulkEntry.ReceiptDate">
																	<td class="fontnormal">
																		<mifos:mifoslabel name="bulkEntry.rcptdate" keyhm="BulkEntry.ReceiptDate" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
																		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BulkEntry.receiptDate)}" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									<br>
									<logic:messagesPresent>
										<font class="fontnormalRedBold"> <html-el:errors bundle="bulkEntryUIResources" /> </font>
										<br>
									</logic:messagesPresent>
									<collectionsheet:bulkentrytag />
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<html-el:hidden property="method" value="preview" />
									<html-el:hidden property="input" value="get" />
									<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<c:choose>
													<c:when test="${requestScope.isDisabled}">
														<html-el:submit styleId="bulkentry_data.button.preview" styleClass="buttn"  disabled="true">
															<mifos:mifoslabel name="bulkEntry.preview" />
														</html-el:submit>
													</c:when>
													<c:otherwise>
														<html-el:submit styleId="bulkentry_data.button.preview" styleClass="buttn"  onclick="fnSubmit(this.form, this)">
															<mifos:mifoslabel name="bulkEntry.preview" />
														</html-el:submit>
													</c:otherwise>
												</c:choose>
												&nbsp;
												<html-el:button styleId="bulkentry_data.button.cancel" property="cancel" styleClass="cancelbuttn" onclick="fnCancel(this.form);">
													<mifos:mifoslabel name="bulkEntry.cancel" />
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						<br>
						</html-el:form>
						</tiles:put>
						</tiles:insert>
