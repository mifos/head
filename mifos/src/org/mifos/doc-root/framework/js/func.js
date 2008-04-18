function FnCheckNumberOnPress(event) {
	var locale = document.all.h_user_locale.value;
	var func="FnCheckNumberOnPress_"+locale+"(event)";
	if(eval(func)==false) {
		return false;
	}
	return true;
}
function FnCheckNumberOnPressValue(evt,min,max) {
	var keyCodePress = (window.event)?event.keyCode:evt.which;
	if(keyCodePress< min || keyCodePress> max) {
		if(keyCodePress==8 || keyCodePress==0) {
			return true;
		}
		else {
			return false;
		}
	}
	return true;
}
function  FnCheckNumber(event,min,max,objTextField) {
	var locale = document.all.h_user_locale.value;
	var func="FnCheckNumber_"+locale+"(event,min,max,objTextField)";
	if(eval(func)==false) {
		objTextField.focus();
		return false;
	}
}
function FnCheckNumberEnglish(event,min,max,value) {
	if(value!=null && value!="") {
		var pattern=new RegExp("^[0-9]{1,}$");
		if(true==pattern.test(value.toString())) {
			return fnCheckRange(event,min,max,value);
		}
		else {
			alert("Numbers only");
			return false;
		}
	}
	else
		return true;
}
function fnCheckRange(event,min,max,value) {
	if(value!=null && value!="") {
		if(min!=null && min!="" && max!=null && max!="") {
			if((value<min) || (value>max)) {
				alert("value should be between "+min+" and "+max);
				return false;
			}
		}
		else if((min==null || min=="") && (max!=null && max!="")) {
			if((value>max)) {
				alert("value should be less than "+max);
				return false;
			}
		}
		else if((max==null || max=="") && (min!=null && min!="")) {
			if((value<min)) {
				alert("value should be greater than "+min);
				return false;
			}
		}
	}
	return true;
}
function  FnCheckNumCharsOnPress(event,objTextField) {
	var locale = document.all.h_user_locale.value;
	var func="FnCheckNumCharsOnPress_"+locale+"(event,objTextField)";
	return eval(func);
}
function  FnCheckNumChars(event,objTextField) {
	var locale = document.all.h_user_locale.value;
	var func="FnCheckNumChars_"+locale+"(event,objTextField)";
	return eval(func);
}
function genericConvertor(textValue,convertorMethod) {
	var  char_code;
	var  asci_value;
	var asci_txt="";
	for(i=0;i<textValue.length;i++) {
		char_code=textValue.charCodeAt(i);
		asci_value=eval(convertorMethod+"("+char_code+")");
		output = eval("String.fromCharCode(" + asci_value + ")");
		asci_txt += output;
	}
	return asci_txt;
}	
function convertorMap(char_code,minDigit,maxDigit) {
	if(char_code>=minDigit&&char_code<=maxDigit) {
		return 48+(char_code-minDigit);
	}
	else 
		return char_code;
}
function fnAdd(theSel, theText,theHidValue) {
	var theValue;
	if(theText.value=="") {
		alert("Enter value");
		return false;
	}
	if(theHidValue.value=="")
		theValue=theText.value+"_"+theText.value;
	else {
		var val=theHidValue.value;
		var splitValue = val.split("_");
		theValue=splitValue[0]+"_"+theText.value;
	}
	addOption(theSel,theText.value,theValue);
	theText.value="";
	theHidValue.value="";
}
function fnEdit(theSelFrom, theSelTo,theHidValue) {
	if(theSelTo.value!="") {
		alert("Add or Clear value in enter before Editing");
		return false;
	}
	else if(theHidValue.value!="") {
		alert("Add value in enter before Editing");
		return false;
	}
	var selLength = theSelFrom.length;
	var selectedText ;
	var selectedValues;
	var selectedCount = 0;
	var i;
	for(i=selLength-1; i>=0; i--) {
		if(theSelFrom.options[i].selected) {
			selectedCount++;
		}
	}
	if(selectedCount>1) {
		alert("Edit only one value at a time");
		return false;
	}
	else {
		for(i=selLength-1; i>=0; i--) {
			if(theSelFrom.options[i].selected) {
				selectedCount++;
				selectedText = theSelFrom.options[i].text;
				selectedValues = theSelFrom.options[i].value;
				deleteOption(theSelFrom, i);
				var splitValue=selectedValues.split("_");
				if(splitValue[0]==splitValue[1]) {
					theSelTo.value=selectedText;
	        			theHidValue.value="";
				}
				else {
					theSelTo.value=selectedText;
        				theHidValue.value=selectedValues;
       				}
       			return true;			
			}
		}
	}
}
function addOption(theSel, theText, theValue) {
	var newOpt = new Option(theText, theValue);
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
	theSel.options[selLength].selected=true;
}
function selectAll(theSelFrom) {
	var selLength = theSelFrom.length;
	for(i=selLength-1; i>=0; i--) 
		theSelFrom.options[i].selected=true;
}
function deleteOption(theSel, theIndex) {
	var selLength = theSel.length;
		if(selLength>0) {
			theSel.options[theIndex] = null;
		}
}
function fnSelectToolTip (event) {
	var obj = event.srcElement;
       with(document.getElementById("s1")) {
		if(obj.selectedIndex!=-1) {
			innerHTML = obj.options[obj.selectedIndex].text;
			with(style) {
				if(event.type == "mouseout") {
					display = "none";
				}
				else {
					if(event.type == "mouseover") {
						display = "inline";
					}
					else {
						display = "inline";
						top = event.y-10;
					}
				}
			}
		}
   	}
}
