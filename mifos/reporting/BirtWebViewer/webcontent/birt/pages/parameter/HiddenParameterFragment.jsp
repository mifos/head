<%-----------------------------------------------------------------------------
	Copyright (c) 2004 Actuate Corporation and others.
	All rights reserved. This program and the accompanying materials 
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html
	
	Contributors:
		Actuate Corporation - Initial implementation.
-----------------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ page session="false" buffer="none" %>
<%@ page import="org.eclipse.birt.report.utility.ParameterAccessor,
				 org.eclipse.birt.report.context.BaseAttributeBean,
				 org.eclipse.birt.report.context.ScalarParameterBean" %>

<%-----------------------------------------------------------------------------
	Expected java beans
-----------------------------------------------------------------------------%>
<jsp:useBean id="attributeBean" type="org.eclipse.birt.report.context.BaseAttributeBean" scope="request" />

<%-----------------------------------------------------------------------------
	Hidden parameter control
-----------------------------------------------------------------------------%>
<%
	ScalarParameterBean parameterBean = ( ScalarParameterBean ) attributeBean.getParameterBean( );
	String encodedParameterName = ParameterAccessor.htmlEncode( parameterBean.getName( ) );
	String value = parameterBean.getValue( );
	if( value != null )
	{
%>

<TR>
	<TD NOWRAP></TD>
	<TD NOWRAP WIDTH="100%">
		<INPUT TYPE="HIDDEN" ID="control_type" VALUE="hidden">
		<INPUT TYPE="HIDDEN"
			NAME="<%= encodedParameterName %>"
			VALUE="<%= value %>"/>		
		    <!-- For Mifos Birt report -->
		    <font class="fontnormalRedBold">
		    <%
			    String message = request.getParameter("mifosErrorMessage");
	 		    if (message != null && request.getAttribute("errorHasBeenShown") == null) { 
	 		        request.setAttribute("errorHasBeenShown", "true");			    		 		    
			    	String[] messages = message.split("~");
			    	for(int i=0;i<messages.length;i++){	    
			    		out.println("<li>" + messages[i] + "</li>");
			    	}
			    }
		    %>
		    </font>						
	</TD>
</TR>

<%
	}
%>