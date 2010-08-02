<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="viewQuestionResponseDiv">
 	<c:set value="${requestScope.questionsHostForm.questionGroups}" var="questionGroups" />
	<!-- Question Groups -->
	<c:if test="${!empty questionGroups}">
	   <ul>
	       <c:forEach var="group" items="${questionGroups}" varStatus="groupLoopStatus">
	           <c:set var="groupIdx" value="${groupLoopStatus.index}"/>
	           <c:forEach var="section" items="${group.sectionDetails}" varStatus="sectionLoopStatus">
	           	   <c:set var="sectionIdx" value="${sectionLoopStatus.index}"/>
	               <li class="fontnormalbold"><c:out value="${section.name}"/></li>
	               
	               <c:forEach var="question" items="${section.questions}" varStatus="questionLoopStatus">
	               	  <c:set var="questionIdx" value="${questionLoopStatus.index}"/> 
	                  <li class="fontnormalbold">${question.title}:
	                     <c:choose> 
						   <c:when test="${question.questionType == 'MULTI_SELECT'}" > 
						        <c:out value="${questionGroups[groupIdx].sectionDetails[sectionIdx].questions[questionIdx].valuesAsArray}"/>
						   </c:when> 
						   <c:otherwise> 
						    <c:out value="${questionGroups[groupIdx].sectionDetails[sectionIdx].questions[questionIdx].value}"/>
						   </c:otherwise> 
						 </c:choose>
	                  </li>
	               </c:forEach>
	           </c:forEach>
	       </c:forEach>
	       <li>
	       	  <input type="button" class="insidebuttn" id="editQuestionResponses_button" onclick="fnEditResponses()" value="Edit  Responses" name="editQuestionResponses_button"/>	
	       </li>
	    
	    <script type="text/javascript">
	        function fnEditResponses() {
	        	location.href=<c:out value="${param.editResponseURL}"/>
	        }
	    </script>   
       </ul>
    </c:if>    
	<!-- Question Groups end -->
</div>


