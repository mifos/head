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

<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript">
    function fnEditResponses() {
        location.href = document.getElementById('editResponseURL').value;    	
    }
</script>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<div id="viewQuestionResponseDiv" class="${param.responseDivStyleClass}">
 	<c:set value="${requestScope.questionsHostForm.questionGroups}" var="questionGroups" />
	<!-- Question Groups -->
	<c:if test="${!empty questionGroups}">
	   <ul>
	       <c:forEach var="group" items="${questionGroups}" varStatus="groupLoopStatus">
	           <c:set var="groupIdx" value="${groupLoopStatus.index}"/>
	           <c:forEach var="section" items="${group.sectionDetails}" varStatus="sectionLoopStatus">
	           	   <c:set var="sectionIdx" value="${sectionLoopStatus.index}"/>
	           	   <br/>
	               <li class="fontnormalbold"><c:out value="${section.name}"/></li>
	               
	               <c:forEach var="question" items="${section.questions}" varStatus="questionLoopStatus">
	               	  <c:set var="questionIdx" value="${questionLoopStatus.index}"/> 
	                  <li>
	                    <span class="fontnormalbold">${question.text}:</span>
	                    <span class="fontnormal">
	                     <c:choose> 
						   <c:when test="${question.questionType == 'MULTI_SELECT' || question.questionType == 'SMART_SELECT' || question.questionType == 'SMART_SINGLE_SELECT'}" >
						   		<c:forEach var="questionAnswer" items="${question.valuesAsArray}" varStatus="valuesLoopStatus">
						   		   <c:if test="${valuesLoopStatus.index != 0}">
						   		      <c:out value=","/>
						   		   </c:if>
						   		   <c:out value="${questionAnswer}"/>
						   		</c:forEach>						   		 
						   </c:when> 
						   <c:otherwise> 
						    <c:out value="${question.value}"/>
						   </c:otherwise> 
						 </c:choose>
						 </span>
	                  </li>
	               </c:forEach>
	           </c:forEach>
	           
	       </c:forEach>
	       <li>
	          <input type="hidden" name="editResponseURL" id="editResponseURL" value='<c:out value="${param.editResponseURL}" escapeXml="false"/>'/>
	          <fmt:bundle basename="org.mifos.config.localizedResources.SurveysUIResources" >
	       	  <input type="button" class="insidebuttn" id="editQuestionResponses_button" onclick="fnEditResponses()" name="editQuestionResponses_button" 
	       	     value="<fmt:message key='Questionnaire.EditAdditonalInformation'/>" />
	       	  </fmt:bundle>   
	       </li>
	    
	       
       </ul>
    </c:if>    
	<!-- Question Groups end -->
</div>