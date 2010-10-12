[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "login" /]
<!-- Container Begins-->
<span id="page.id" title="Login" />
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
        <div class="span-8 last" align="center">
          <div class="error">
          	<span id="login.error.message">
          		[#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content]
		 					<span>${Session.SPRING_SECURITY_LAST_EXCEPTION.message}</span><br/>		
				[/#if]
			</span>
		</div>					
		<div>
          	<span class="normalFont"><label for="login.input.username">[@spring.message "login.UserName" /]</label>:</span>
            <span ><input type="text" name="j_username" id="login.input.username"></span>
          </div>
          <div class="paddingTop5">
          	<span class="normalFont">&nbsp;<label for="login.input.password">[@spring.message "login.password"/]</label>&nbsp;:</span>
            <span><input type="password" name="j_password" id="login.input.password"></span>
        </div>
          <div>&nbsp;</div>
          <div> <input type="submit" value="[@spring.message "login.login" /]" class="buttn" style="margin-right: 16px;" id="login.button.login" />
        </div>
        </div>
        <!--End of Right side div-->
      </form>
    </div>
  <!--Main Login Ends-->
  
<!--Container Ends-->
[@mifos.footer /] 