function FnCheckNumberOnPress_is_IS(evt){
	return FnCheckNumberOnPressValue(evt,48,57);
}

function FnCheckNumber_is_IS(event,min,max,objTextField) {
	return FnCheckNumberEnglish(event,min,max,objTextField.value)
}

function FnCheckNumCharsOnPress_is_IS(evt) {
	var keyCodePress = (window.event)?event.keyCode:evt.which;
/*	if((keyCodePress>=48)&&(keyCodePress<=57)||
			(keyCodePress>=65)&&(keyCodePress<=90)||
			(keyCodePress>=97)&&(keyCodePress<=122)||
			(keyCodePress==13)||(keyCodePress==8)||
			(keyCodePress==0))
		return true; 
	else
		return false;*/
	if((keyCodePress==60) || (keyCodePress==62))
		return false;
	else
		return true;
}

function FnCheckNumChars_is_IS(event,objTextField) {

	return true;

/*
	if(objTextField.value!=null && objTextField.value!="") {
		var pattern=new RegExp("^['\"><]*$");
		if(false==pattern.test(objTextField.value.toString())) {
		}
		else {
			alert("Alpha Numberic only");
			objTextField.focus();
			return false;
		}
	}
	else 
		return false;
		*/
		
}