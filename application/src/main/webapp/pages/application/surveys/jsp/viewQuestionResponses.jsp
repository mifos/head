<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
    function fnEditResponses() {
        location.href = document.getElementById('editResponseURL').value;    	
    }
</script>
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
	                    <span class="fontnormalbold">${question.title}:</span>
	                     <c:choose> 
						   <c:when test="${question.questionType == 'MULTI_SELECT'}" >
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
	                  </li>
	               </c:forEach>
	           </c:forEach>
	           
	       </c:forEach>
	       <li>
	          <input type="hidden" name="editResponseURL" id="editResponseURL" value='<c:out value="${param.editResponseURL}" escapeXml="false"/>'/>
	       	  <input type="button" class="insidebuttn" id="editQuestionResponses_button" onclick="fnEditResponses()" value="Edit Additional Information" name="editQuestionResponses_button"/>	
	       </li>
	    
	       
       </ul>
    </c:if>    
	<!-- Question Groups end -->
</div>