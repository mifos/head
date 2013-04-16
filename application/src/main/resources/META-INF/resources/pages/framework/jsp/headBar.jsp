<STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/datejs/date.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
<!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
<script type="text/javascript" src="pages/js/lab.js"></script>
  <tr>
    <td width="200" rowspan="2"><img src="getMifosLogo.ftl" width="200" height="70"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal">
		<c:if test="${requestScope.currentPageUrl != null}">
			<c:choose>
				<c:when test="${requestScope.currentSitePreference == 'MOBILE'}">
					<a href="${requestScope.currentPageUrl}&site_preference=normal">
				</c:when>
				<c:otherwise>
					<a href="${requestScope.currentPageUrl}&site_preference=mobile">
				</c:otherwise>
			</c:choose>
				<mifos:mifoslabel name="framework.switchSiteType" bundle="FrameworkUIResources"></mifos:mifoslabel>
			</a>
			&nbsp;|
		</c:if>
         <a id="changeLanguagLink" href="#">Change Preferred Language</a>
         <span id="dialog" title="Change Preferred Language" style="display:none;">Change Preferred Language</span>
     &nbsp;|&nbsp;
         <a id="homeheader.link.yourSettings" href="yourSettings.do?method=get&randomNUm=${sessionScope.randomNUm}">
            <mifos:mifoslabel name="framework.yoursettings" bundle="FrameworkUIResources"></mifos:mifoslabel>
         </a>
     &nbsp;|&nbsp; 
         <a id="logout_link" href="j_spring_security_logout">
            <mifos:mifoslabel name="framework.logout" bundle="FrameworkUIResources"></mifos:mifoslabel>
         </a>
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     </td>
  </tr>