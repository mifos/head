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
	Text box parameter control
-----------------------------------------------------------------------------%>
<%
	ScalarParameterBean parameterBean = ( ScalarParameterBean ) attributeBean.getParameterBean( );
	String encodedParameterName = ParameterAccessor.htmlEncode( parameterBean.getName( ) );
%>
<TR>
	<TD NOWRAP>
	<!-- For Mifos Birt reports hide
		<IMG SRC="birt/images/parameter.gif" ALT="<%= parameterBean.getDisplayName( ) %>" TITLE="<%= parameterBean.getToolTip( ) %>"/>
	-->	
	</TD>
	<TD NOWRAP>
	    <!-- For Mifos Birt reports hide
		<FONT TITLE="<%= parameterBean.getToolTip( ) %>">
		
		<LABEL FOR="<%= encodedParameterName %>"><%= parameterBean.getDisplayName( ) %>:</LABEL></FONT>
		-->	
		<%-- is required --%>
		<%
		if ( parameterBean.isRequired( ) )
		{
		%>
			<FONT COLOR="red"><LABEL FOR="<%= encodedParameterName %>">*</LABEL></FONT>
		<%
		}
		%>
	</TD>
</TR>
<TR>
	<TD NOWRAP></TD>
	<TD NOWRAP WIDTH="100%">
<%
	if ( parameterBean.allowNull( ) )
	{
%>
		<INPUT TYPE="HIDDEN"
			ID="<%= encodedParameterName + "_hidden" %>"
			NAME="<%= ParameterAccessor.PARAM_ISNULL %>"
			VALUE="<%= (parameterBean.getValue( ) == null)? encodedParameterName : "" %>">
		
		<LABEL FOR="<%= encodedParameterName + "_radio_notnull" %>" CLASS="birtviewer_hidden_label">Input text</LABEL>	
		<INPUT TYPE="RADIO"
			ID="<%= encodedParameterName + "_radio_notnull" %>"
			VALUE="<%= encodedParameterName %>"
			<%= (parameterBean.getValue( ) != null)? "CHECKED" : "" %>>
<%
	}
%>

<%
	if ( !parameterBean.allowNull( ) && !parameterBean.allowBlank( ) )
	{
%>
		<INPUT TYPE="HIDDEN"
			ID="<%= encodedParameterName + "_default" %>"
			VALUE="<%= ParameterAccessor.htmlEncode( ( parameterBean.getDefaultValue( ) == null )? "" : parameterBean.getDefaultValue( ) ) %>"
			>
<%
	}
%>
<!-- For Mifos Birt reports hide
		<INPUT CLASS="BirtViewer_parameter_dialog_Input"
			TYPE="<%= parameterBean.isValueConcealed( )? "PASSWORD" : "TEXT" %>"
			NAME="<%= encodedParameterName %>"
			ID="<%= encodedParameterName %>" 
			TITLE="<%= parameterBean.getToolTip( ) %>"
			VALUE="<%= ParameterAccessor.htmlEncode( ( parameterBean.getValue( ) == null )? "" : parameterBean.getValue( ) ) %>" 
			<%= ( parameterBean.allowNull( ) && parameterBean.getValue( ) == null )? "DISABLED='true'" : "" %>
            >
-->
<!-- For Mifos Birt reports add-->
        <table width="70%" cellspacing="0" cellpadding="0" border="0">
             <tbody>
                  <tr>
                    <td class="headingorange" width="70%" valign="top" height="24" align="left">
                        <LABEL><%= ParameterAccessor.htmlEncode( ( parameterBean.getValue( ) == null )? "" : parameterBean.getValue( ) ) %></LABEL>
                        <br/>
                    </td>
                  </tr>
             </tbody>
         </table>
         <div class="fontnormal">
         <LABEL>Enter the parameters on which the report will be generated. The report will be generated as a pdf. 
		     <FONT COLOR="red"><LABEL>*</LABEL></FONT><LABEL>Fields marked with an asterisk are required. </LABEL>
             </br>
         </div>
         <div class="fontnormalbold">
             </br>
             <LABEL>&nbsp;Generate Report:</LABEL>
         </div>
         <!-- For Mifos Birt report -->
	    <%
		    String message = request.getParameter("message");
		    if (message != null) {
	    %>
	    <li class="fontnormalRedBold">
	    <%
		    	out.println(message);
		    }
	    %>
	    </li>
	    <!-- END -->
<!-- End -->
<%
	if ( parameterBean.allowNull( ) && !parameterBean.allowBlank( ) )
	{
%>
		<INPUT TYPE="HIDDEN"
			ID="isNotBlank" 
			NAME="<%= encodedParameterName %>"
			VALUE = "true">
<%
	}
%>            

<%
	if ( parameterBean.allowNull( ) )
	{
%>
		<BR>
		<LABEL FOR="<%= encodedParameterName + "_radio_null" %>" CLASS="birtviewer_hidden_label">Null Value</LABEL>	
		<INPUT TYPE="RADIO"
			ID="<%= encodedParameterName + "_radio_null"%>"
			VALUE="<%= encodedParameterName %>"
			<%= ( parameterBean.getValue( ) == null )? "CHECKED" : "" %>> Null Value
<%
	}
%>
	</TD>
</TR>