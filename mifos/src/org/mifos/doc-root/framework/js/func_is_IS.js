function FnCheckNumberOnPress_is_IS(evt){
	return FnCheckNumberOnPressValue(evt,48,57);
}

function FnCheckNumber_is_IS(event,min,max,objTextField) {
	return FnCheckNumberEnglish(event,min,max,objTextField.value)
}

function FnCheckNumCharsOnPress_is_IS(evt) {
	var keyCodePress = (window.event)?event.keyCode:evt.which;
	if((keyCodePress==60) || (keyCodePress==62))
		return false;
	else
		return true;
}
function FnCheckNumChars_is_IS(event,objTextField) {
	return true;
}