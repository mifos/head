function FnCheckNumberOnPress_en_US(evt){
	return FnCheckNumberOnPressValue(evt,48,57);
}

function FnCheckNumber_en_US(event,min,max,objTextField) {
	return FnCheckNumberEnglish(event,min,max,objTextField.value)
}

function FnCheckNumCharsOnPress_en_US(evt) {
	var keyCodePress = (window.event)?event.keyCode:evt.which;
	if((keyCodePress==60) || (keyCodePress==62))
		return false;
	else
		return true;
}

function FnCheckNumChars_en_US(event,objTextField) {
	return true;
}