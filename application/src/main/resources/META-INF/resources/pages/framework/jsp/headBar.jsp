<STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/datejs/date.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
<!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
<script type="text/javascript" src="pages/js/lab.js"></script>
  <tr>
    <td width="200" rowspan="2"><img src="pages/framework/images/logo.jpg" width="200" height="70"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal">
         <a id="changeLanguagLink" href="#">Change Language</a>
         <span id="dialog" title="Change Language">Change Language</span>
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