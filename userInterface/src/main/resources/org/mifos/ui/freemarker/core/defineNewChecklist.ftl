[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<script type="text/javascript" language="javascript">

function restrictScript(evt){var keyCodePress=(window.event)?event.keyCode:evt.which;if((keyCodePress==60)||(keyCodePress==62))
return false;else
return true;}

var k=0;var i=0;var numberOfItems=0;var count=0;var flag=0;var totalDetails=0;var detailTxt="";function createCheckList()
{var arr=document.getElementsByName("mycheckBOx");if(arr)
{for(;count<arr.length;)
{count++;i=count;numberOfItems=i;}
k=numberOfItems;while(true)
{var chkBox=document.getElementsByName("detailsList["+k+"]");if(chkBox.length>0)
{k++;}
else
{break;}}}
var re=/\s/g;;var aTextArea=document.getElementsByName('text')[0].value;var str=aTextArea.replace(re,"");if(str.length==0)
{event.returnValue=false;return false;}
var val=document.getElementsByName("text")[0].value;var qt="'";var value=val.replace(qt,"&#39;");for(i=0;i<val.length;i++){value=value.replace(qt,"&#39;");}
if(document.getElementsByName('text')[0].value)
{var para=document.getElementById("myDiv");var detailsTxt=document.createElement("TD");detailsTxt.className="fontnormal";var divIdName="my"+k+"Div";detailsTxt.setAttribute("id",divIdName);var textArea=document.getElementsByName('text')[0].value;detailsTxt.innerHTML+="<input type='checkbox'  name='checkBox("+k+")' value='"+value+"'>";var incrementer=0;while(incrementer<textArea.length)
{var temp=incrementer;detailsTxt.innerHTML+=textArea.substr(temp,80);detailsTxt.innerHTML+="<br>";incrementer=incrementer+80;}
detailsTxt.innerHTML+="<input type='hidden'  name='detailsList["+k+"]' value='"+value+"' >";para.appendChild(detailsTxt);k++;numberOfItems++;document.getElementsByName('text')[0].value="";}}
function RemoveSelected()
{var arr=document.getElementsByName("mycheckBOx");if(arr)
{for(;count<arr.length;)
{count++;i=count;numberOfItems=count;}}
arrElements=document.getElementsByTagName("input");var iterator=i;for(x=0;x<arrElements.length;x++)
{element=arrElements[x];if(element.checked)
{element.parentNode.parentNode.removeChild(element.parentNode);x--;iterator--;numberOfItems--;}}}

function isButtonRequired()
{if(numberOfItems>0)
{document.getElementById("removeButton").style.display="block";}
else if(numberOfItems==0)
{document.getElementById("removeButton").style.display="none";}}
function setNumberOfPreviousItems()
{var array=document.getElementsByName("numberOfPreviousItems");var counter;if(array!=null)
for(counter=0;counter<array.length-1;counter++)
{}
i=i+counter;numberOfItems=i;}
</script>
</head>
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content definePageMargin">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "organizationPreferences.defineNewCheckLists.checklistinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "organizationPreferences.defineNewCheckLists.addnewchecklist" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "organizationPreferences.defineNewCheckLists.enterchecklistinformation" /]</span></p>
          <div>[@spring.message "organizationPreferences.defineNewCheckLists.completethefieldsbelow.ThenclickPreview." /] </div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
          <p>&nbsp;&nbsp;</p>
                 <p class="fontBold">[@spring.message "organizationPreferences.defineNewCheckLists.checklistdetails" /]  </p>
                 <p>&nbsp;&nbsp;</p>
          <div class="prepend-3 pull-3 span-21 last">
          	<div class="span-20 "><span class="span-6 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.defineNewCheckLists.name" /]</span><span class="span-5">&nbsp;
    				<input type="text" /></span>
  			</div>
          	<div class="span-20"><span class="span-6 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.defineNewCheckLists.type" /]</span><span class="span-5">&nbsp;
    				<select name="select">
    				  <option>--Select--</option>
                        <option>center</option>
                        <option>group</option>
                        <option>client</option>
                        <option>loans</option>
                        <option>savings</option>
				    </select></span>
  			</div>
        	<div class="span-20"><span class="span-6 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.defineNewCheckLists.displayedwhenmovingintoStatus" /]&nbsp;:</span><span class="span-5">&nbsp;
   					<select name="select">
   					 <option>--Select--</option>
                        <option>active</option>
                        <option>inactive</option>
				    </select></span>
			</div>
            <div class="span-20"><span class="span-6 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.defineNewCheckLists.items" /]</span><span class="span-10">[@spring.message "organizationPreferences.defineNewCheckLists.entertextforeachitemandclickAddtosavetheiteminthelistbelow" /]<br />
            	<p><TEXTAREA name="text" cols="50" rows="5" onKeyPress="return restrictScript(event);"></TEXTAREA><INPUT type="button" name="button" value="Add&gt;&gt;" onClick="createCheckList();isButtonRequired()" class="buttn2"></span></p>
			</div>
            <div class="span-20">
            	<div id="myDiv" class="span-18"></div>
                <div id="removeButton" style="display: none; "><INPUT type="button" class="buttn2" name="removeSelected" value="Remove Selected" onClick="RemoveSelected(); isButtonRequired()"></div>
			</div>
          </div>
          <p>&nbsp;&nbsp;</p>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="button" name="preview" value="Preview" onclick="#"/>
            	<input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]