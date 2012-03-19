
[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[@layout.header "mifos" /]
<!-- Container Begins-->
<span id="page.id" title="Login"></span>
<div class="container" align="center"> &nbsp;
  <!--Header-->
  <span class="logo"></span>
  <div class="orangeline" style="margin-top:65px;">&nbsp;</div>
  <!--  Main Login Begins-->
    <div class="borders maindiv marginAuto">
      <form method="POST" action="j_spring_security_check" id="login.form">
        <div align="left" class="bluediv span-16 last"><span>[@spring.message "login.login"/]</span></div>
        <div class="span-7 LeftDiv normalFont" align="left">[@spring.message "login.welcomeToMifos"/]</div>
        <!--Begining of Right side div-->
        <div class="span-8 last" style="text-align:left; width:290px">
          <div class="error">
              <span id="login.error.message">
                  [#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content]
                             <span>${Session.SPRING_SECURITY_LAST_EXCEPTION.message}</span><br/>
                [/#if]
            </span>
        </div>
        <div style="text-align:right;">
              <span class="normalFont"><label for="login.input.username">[@spring.message "login.UserName" /]</label>:&nbsp;</span>
            <span ><input class="focused" type="text" name="j_username" id="login.input.username"></span>
          </div>
          <div class="paddingTop5" style="text-align:right;">
              <span class="normalFont">&nbsp;<label for="login.input.password">[@spring.message "login.password"/]</label>:&nbsp;</span>
            <span><input type="password" name="j_password" id="login.input.password"></span>
            <input type="hidden" name="spring-security-redirect" value="/home.ftl"/>
        </div>
          <div>&nbsp;</div>
          <div> <input type="submit" value="[@spring.message "login.login" /]" class="buttn" style="position: relative; left:136px;" id="login.button.login" />
        </div>
        </div>
        <!--End of Right side div-->
      </form>
    </div>
   	<div class="error">
      <span id="login.error.message">
      	[#if !isJetty]
         	<span>[@spring.message "login.serverisnotjetty"/]</span><br/>
        [/#if]
        </span>
    </div>
  </div>
  <!--Main Login Ends-->
<span id="chinese-info-on-login-page">
[@spring.message "login.chinese.translation.attribution" /]
</span>
<script type="text/javascript" >
        $("input.focused").focus();
</script>
[#if Application.LocaleSetting.labLevel > 0]
<b>Demo User / Password</b>
<br>mifos / testmifos
<br>mifos1 / testmifos
<br>mifos2 / testmifos
[/#if]
<!--Container Ends-->
[@layout.footer /]
