[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div>  
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<div class="span-20 bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewfunds"/]</span></div>
  	<br/><p>&nbsp;&nbsp;</p>
    <p class="font15 orangeheading">[@spring.message "viewfunds"/]</p>
    <p>&nbsp;&nbsp;</p> 
    <div class="span-15">
    	<div class="span-15 fontBold borderbtm paddingLeft">
        	<span class="span-5 ">[@spring.message "name"/]</span>
            <span class="span-8">[@spring.message "fundCode"/]</span>
            <span class="span-1 ">&nbsp;</span>
        </div>
        
        
        <div class="span-15 borderbtm paddingLeft">
        	<span class="span-5">[@spring.message "funds"/]</span>
            <span class="span-8">00</span>
            <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>
        </div>
        
        
        <div class="span-15 borderbtm paddingLeft"> 
	                <span class="span-5">Non Donor</span> 
	                <span class="span-8">00</span> 
	                <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>            
	    </div>
	    
	    <div class="span-15 borderbtm paddingLeft"> 
	                <span class="span-5">Funding Org A</span> 
	                <span class="span-8">00</span> 
	                <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>            
	    </div>  
        
        <div class="span-15 borderbtm paddingLeft"> 
	                <span class="span-5">Funding Org B</span> 
	                <span class="span-8">00</span> 
	                <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>            
	    </div>  
        
        <div class="span-15 borderbtm paddingLeft"> 
	                <span class="span-5">Funding Org C</span> 
	                <span class="span-8">00</span> 
	                <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>            
	    </div>  
        
        <div class="span-15 borderbtm paddingLeft"> 
	                <span class="span-5">Funding Org D</span> 
	                <span class="span-8">00</span> 
	                <span class="span-1"><a class="floatRT" href="#">[@spring.message "edit"/]</a></span>            
	    </div>  
              
	               
        
                
    </div>
   	</form> 
 </div><!--Main Content Ends-->  
[@mifos.footer/]